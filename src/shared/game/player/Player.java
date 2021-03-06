package shared.game.player;

import client.devcards.DevCard;
import client.model.Model;
import client.model.ModelFacade;
import client.model.TurnStatus;
import server.ourserver.ServerFacade;
import shared.definitions.*;
import shared.game.Bank;
import shared.game.CatanGame;
import shared.game.DevCardList;
import shared.game.ResourceList;
import shared.game.map.Hex.Hex;
import shared.game.map.Hex.RoadPiece;
import shared.game.map.Index;
import shared.game.map.Port;
import shared.game.map.Robber;
import shared.game.map.vertexobject.City;
import shared.game.map.vertexobject.Settlement;
import shared.locations.EdgeLocation;
import shared.locations.HexLocation;
import shared.locations.VertexDirection;
import shared.locations.VertexLocation;

import java.util.ArrayList;

/**
 * @author Alex
 * Player: Class that represents each individual player in the game.
 * There are 4 players in each game; thus, there are 4 objects of type
 * Player in each game.
 */
public class Player implements Comparable<Player>
{
	private Index playerIndex = null;
	private int roadSize=0;
	private int armySize=0;
	private boolean playedDevCard;
	private String password = "";

	/**
	 * numCities: How many cities an individual player has.
	 */
	private int numCitiesRemaining = MAX_NUM_CITIES;
	/**
	 * Color: Color is received as a String from the JSON file.
	 * However, we need to use the enum type CatanColor.
	 */
	private CatanColor color = null;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) {return false;}

        Player player = (Player) o;

        if (!password.equals(player.password)){ return false;}
        return name.equals(player.name);

    }

    @Override
    public int hashCode() {
        int result = password.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

    /**
	 * discarded: whether or not they discarded a card
	 */
	private boolean discarded = false;

	/**
	 * numMonuments: how many monuments a player has.
	 * I don't know what this is, but we get it for the JSON. So it is probably used for something.
	 * :D ~ Alex
	 */
	private int numMonuments = 0;

	/**
	 * Name: Player's name.
	 * Examples: Jessie, Rob, Tiny, Kahuna (max 7 char)
	 *
	 * According to the JSON this will be read as just a String.
	 * However, we may need a class to encapsulate "Name" in the future
	 * in order to avoid primitive obsession.
	 */
	private String name = "";

	/**
	 * newDevCards: list of new development cards
	 */
	private DevCardList newDevCards = new DevCardList(DEFAULT_VAL, DEFAULT_VAL, DEFAULT_VAL, DEFAULT_VAL, DEFAULT_VAL);

	/**
	 * oldDevCards: list of old development cards
	 */
	private DevCardList oldDevCards= new DevCardList(DEFAULT_VAL, DEFAULT_VAL, DEFAULT_VAL, DEFAULT_VAL, DEFAULT_VAL);

	/**
	 * ID of the player
	 * received from JSON file
	 *
	 * NOTE: Keep this until we determine if there's a difference
	 * between playerID and playerIndex.
	 */
	private Index playerID = new Index(1924);

	/**
	 * ResourceList: List of all the resource cards
	 * (brick, ore, sheep, wheat, wood)
	 */
	private ResourceList resources = new ResourceList();

	/**
	 * List of all the ports that the player currently has.
	 */
	private ArrayList<Port> playerPorts = null;

	/**
	 * List of settlements owned by the player.
	 * Use this to obtain how many CURRENT settlements a player has.
	 */
	private ArrayList<Settlement> settlements = new ArrayList<>();

	/**
	 * List of cities owned by the player.
	 * Use this to obtain how many CURRENT cities a player has.
	 * Settlements come before cities.
	 */
	private ArrayList<City> cities = new ArrayList<>();

	/**
	 * How many roads the Player CAN BUILD.
	 * Updated dynamically.
	 */
	private int numRoadPiecesRemaining = MAX_NUM_ROADS;

	//private int numRoadPiecesRemainingForSetupPhase = 2;

	/**
	 * All the RoadPieces owned by the Player
	 */
	private ArrayList<RoadPiece> roadPieces = new ArrayList<>();

	/**
	 * How many settlements the player CAN BUILD.
	 */
	private int numSettlementsRemaining = MAX_NUM_SETTLEMENTS;

	/**
	 * How many soldiers (soldier cards) the player CAN BUILD.
	 */
	private int numSoldierCards ;

	/**
	 * How many victory points the player has
	 * Everyone starts off with 2. 10+ on your turn to win!
	 */
	private int numVictoryPoints = 0;

	/**
	 * CurrentPlayer: Tracks whether or not this player is the current one!
	 * I.e. is it your turn?
	 */
	private boolean currentPlayer = false;

	/**
	 * Player constructor
	 * @pre name is between 3 and 7 characters
	 * 						CatanColor is not null
	 * 						playerID is not null
	 * @post same as above.
	 */
	public Player(String name, CatanColor color, Index playerID)
	{
		this.name = name;
		this.color = color;
		this.playerID = playerID;}

	public Player(int id, String username, String password)
	{
		playerID.setNumber(id);
		name = username;
		this.password = password;
	}

	public Player(String username, String password)
	{
		name = username;
		this.password = password;
	}

	/**
	 * Function to determine whether or not a player can trade with another player.
	 * @param other: the other player
     */
	public boolean canRequestTrade(Player other, int amountRequesting, int amountSending, ResourceType typeRequesting,
								   ResourceType typeSending)
	{
		if (!currentPlayer || other.isCurrentPlayer())
		{
			return false;
		}

		if (amountSending > resources.getRequestedType(typeSending))
		{
			return false;
		}

		/*
		 This next part may need to be split up/adjusted because of the GUI.
		 The player can REQUEST as much as he wants. However, if the other player
		 doesn't have sufficient of the requested resources, then they will have the
		 option to accept the trade grayed out.

		 It is KEY that the operation be performed on Player OTHER! NOT, and I repeat NOT
		  the local player!
		  */
		if (!other.canBeTradedWith(amountRequesting, typeRequesting))
		{
			return false;
		}
		return true;
	}

	/**
	 * Returns true if the player can trade with the quantity requested.
	 * It is interconnected with the similar method canRequestTrade().
	 * @param quantityRequested: how many resources the player wants.
     */
	public boolean canBeTradedWith(int quantityRequested, ResourceType typeRequested)
	{
		if (quantityRequested > resources.getRequestedType(typeRequested))
		{
			return false;
		}
		return true;
	}

	/**
	 * Determines whether or not the player has the resources to be robbed
	 * Need to check this before Robber robs player
	 * @pre: Robber's location is not null
	 */
	public boolean canBeRobbed()
	{
		HexLocation currentRobberLocation = ModelFacade.facadeCurrentGame.currentgame.myrobber.getLocation();
		if (resources.getBrick() == 0 && resources.getOre() == 0 && resources.getSheep() == 0
				&& resources.getWheat() == 0 && resources.getWood() == 0)
		{
			return false;
		}
		if (currentPlayer)
		{
			return false;
		}

		/*
		A bit of helpful (hopefully) explanation for this next little bit:
			If the player doesn't have a settlement or city on the robber's hex, then they cannot be robbed.
		 */
		boolean hasAreaAffectedByRobber = false;
		for (Settlement settlement : settlements)
		{
			if (settlement.getHexLocation().equals(ModelFacade.facadeCurrentGame.currentgame.myrobber.getLocation()))
			{
				hasAreaAffectedByRobber = true;
			}
		}
		for (City city : cities)
		{
			if (city.getHexLocation().equals(ModelFacade.facadeCurrentGame.currentgame.myrobber.getLocation()))
			{
				hasAreaAffectedByRobber = true;
			}
		}
		if (!hasAreaAffectedByRobber)
		{
			return false;
		}
		return true;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public CatanColor getColor() {
		return color;
	}

	public void setColor(CatanColor color) {
		this.color = color;
	}

	public Index getPlayerID() {
		return playerID;
	}

	public void setPlayerID(Index playerID) {
		this.playerID = playerID;
	}

	public boolean canBuyDevCard()
	{

		if(resources.getOre()>0)
		{
			if(resources.getSheep()>0)
			{
				if(resources.getWheat()>0)
				{
					return true;
				}

			}
		}
		return false;
	}

	/**
	 * Function to determine whether or not the player can trade with the bank
	 * based on the parameters of their particular trade.
	 * Attention: The bank is no longer a singleton. lolwhoops.
	 * @param tradeType: the type of trade being performed, based on the port type
	 * @param typeFor_3_Or4Way: if it's 3-way or 4-way, we will need additional specifications
	 * @param typeRequesting: which type the player wants!
	 *
	 * Second PortType is only necessary if there is a 3:1 or 4:1 trade. This is because it is saved as THREE
	 * or FOUR, and thus we don't know if that means WHEAT, SHEEP, WOOD, etc.
	 *
	 * If the second type is listed as BLANK, then it is a 2:1 trade, and thus said second variable will never be used.
     */

	public boolean canDoTradeWithBank(TradeType tradeType, TradeType typeFor_3_Or4Way, ResourceType typeRequesting) throws Exception
	{
		// What kind of trade is it?
		switch (tradeType)
		{
			case WOOD: // Then it is a 2:1.
				if (resources.getWood() < TWO_WAY)
				{
					return false; // I think this should be fine.
				}
				break;
			case BRICK:
				if (resources.getBrick() < TWO_WAY)
				{
					return false;
				}
				break;
			case SHEEP:
				if (resources.getSheep() < TWO_WAY)
				{
					return false;
				}
				break;
			case WHEAT:
				if (resources.getWheat() < TWO_WAY)
				{
					return false;
				}
				break;
			case ORE:
				if (resources.getOre() < TWO_WAY)
				{
					return false;
				}
				break;
			case THREE: // 3:1.
				if (!multiWayTrade(typeFor_3_Or4Way, THREE_WAY))
				{
					return false;
				}
				break;
			case FOUR:
				if (!multiWayTrade(typeFor_3_Or4Way, FOUR_WAY))
				{
					return false;
				}
				break;
			default:
				break;
		}
		if (ModelFacade.facadeCurrentGame.currentgame.mybank.CanBankGiveResourceCard(typeRequesting))
		{
			return false;
		}
		return true;
	}

	/**
	 * Handles three or four way trades.
	 * @param theType: type of the trade
	 * @param threeOrFour: whether it is a 3-way or 4-way
     */
	private boolean multiWayTrade(TradeType theType, int threeOrFour)
	{
		//assert(!theType.equals(TradeType.FOUR));
		//assert(!theType.equals(TradeType.THREE));
		//assert(!theType.equals(TradeType.BLANK));
		//assert(threeOrFour == THREE_WAY || threeOrFour == FOUR_WAY);
		switch (theType)
		{
			case WOOD:
				if (resources.getWood() < threeOrFour)
				{
					return false;
				}
				break;
			case BRICK:
				if (resources.getBrick() < threeOrFour)
				{
					return false;
				}
				break;
			case SHEEP:
				if (resources.getSheep() < threeOrFour)
				{
					return false;
				}
				break;
			case ORE:
				if (resources.getOre() < threeOrFour)
				{
					return false;
				}
				break;
			case WHEAT:
				if (resources.getWheat() < threeOrFour)
				{
					return false;
				}
				break;
			default:
				break;
		}
		return true;
	}

	/**
	 * Function to build a road segment!
	 * Only decrease numRoadPiecesRemaining once!
	 * @param hex: the hex we want to build on. We will also compute the adjacent hex.
	 * @param edge: the edge that we want to build on. We will also compute the adjacent edge.
     */
	public boolean buildRoadPiece(Hex hex, EdgeLocation edge)
	{
		if (canBuildRoadPiece(hex, edge))
		{
			numRoadPiecesRemaining = numRoadPiecesRemaining - 1;
			Hex adjacent = computeAdjacentHex(hex, edge);
			EdgeLocation edge2 = computeOppositeEdge(edge, adjacent);
			// check the other hex
			if (hex.getResourcetype() == HexType.WATER && adjacent.getResourcetype() == HexType.WATER)
			{
				return false;
			}
			else
			{
				edge.setHasRoad(true);
				edge2.setHasRoad(true);
				RoadPiece piece = hex.buildRoad(edge, playerID);
				//piece.setLocation(edge.getNormalizedLocation());
				RoadPiece piece2 = adjacent.buildRoad(edge2, playerID);
				//piece2.setLocation(edge2.getNormalizedLocation()); // just changed this
				roadPieces.add(piece);
				//roadPieces.add(piece2); // maybe

				return true;
			}
		}
		return false;
	}

	/**
	 * Computes the neighboring hex which shares the border for the road.
	 *
	 * @param initial: the hex which we are computing the neighbor for.
	 * @param edge: the edgeLocation in which the road was already built on.
     */
	private Hex computeAdjacentHex(Hex initial, EdgeLocation edge)
	{
		Hex adjacent = null;
		switch (edge.getDir())
		{
			case NorthWest:
				HexLocation loc1 = new HexLocation(initial.getLocation().getX() - 1, initial.getLocation().getY());
				adjacent = ModelFacade.facadeCurrentGame.currentgame.getMymap().getHexes().get(loc1);
				break;
			case North:
				HexLocation loc2 = new HexLocation(initial.getLocation().getX(), initial.getLocation().getY() - 1);
				adjacent = ModelFacade.facadeCurrentGame.currentgame.getMymap().getHexes().get(loc2);
				break;
			case NorthEast:
				HexLocation loc3 = new HexLocation(initial.getLocation().getX() + 1, initial.getLocation().getY() - 1);
				adjacent = ModelFacade.facadeCurrentGame.currentgame.getMymap().getHexes().get(loc3);
				break;
			case SouthEast:
				HexLocation loc4 = new HexLocation(initial.getLocation().getX() + 1, initial.getLocation().getY());
				adjacent = ModelFacade.facadeCurrentGame.currentgame.getMymap().getHexes().get(loc4);
				break;
			case South:
				HexLocation loc5 = new HexLocation(initial.getLocation().getX(), initial.getLocation().getY() + 1);
				adjacent = ModelFacade.facadeCurrentGame.currentgame.getMymap().getHexes().get(loc5);
				break;
			case SouthWest:
				HexLocation loc6 = new HexLocation(initial.getLocation().getX() - 1, initial.getLocation().getY() + 1);
				adjacent = ModelFacade.facadeCurrentGame.currentgame.getMymap().getHexes().get(loc6);
				break;
			default:
				break;
		}
		//assert(adjacent != null);
		return adjacent;
	}

	public void buildSettlement(Hex buildingon, VertexLocation locationofsettlement) throws Exception
	{
		if(canBuildSettlementStartup(buildingon,locationofsettlement))
		{
			//System.out.println("the playerID is " + playerID.getNumber() + " aka " + this.playerID.getNumber());
			buildingon.buildSettlement(locationofsettlement, this.playerID);
			//resources.setBrick(resources.getBrick()-1);
			//resources.setWheat(resources.getWheat()-1);
			//resources.setSheep(resources.getSheep()-1);
			//resources.setWood(resources.getWood()-1);
			//this.numSettlementsRemaining--;
		}
	}

	public void buildCity(Hex buildingon, VertexLocation locationforcity) throws Exception {
		if(canBuildCity(buildingon,locationforcity))
		{
			buildingon.buildCity(locationforcity,this.getPlayerID());
			//resources.setOre(resources.getOre()-3);
			//resources.setWheat(resources.getWheat()-2);
			//this.numCitiesRemaining--;
		}
	}
	/**
	 * Determines whether or not the player can buy/build a road.
	 * SetUp phase is when the player can place a road wherever they want, I think.
	 * Other than the setup, the player must have an adjacent road, city, or
	 * settlement in order to build a road.
	 * @pre: hex is not null
	 * 		edge is not null
	 * @param hex: The hex that the player is trying to build on
	 * @param edge: The edge that the road will be placed on
	 */
	public boolean canBuildRoadPiece(Hex hex, EdgeLocation edge)
	{
		//System.out.println("I am calling canBuildRoadPiece");
		if (resources.getBrick() < MIN || resources.getWood() < MIN || numRoadPiecesRemaining < MIN)
		{
			return false;
		}
		if (!currentPlayer)
		{
			return false;
		}
		// Try this: The corresponding edge on the hex
		EdgeLocation truth = getCorrectEdgePointer(edge, hex);
		// don't want this to be null!
		if (truth == null)
		{
			return false;
		}
		if (truth.hasRoad())//(edge.hasRoad()) // might want to compute adjacent
		{
			//System.out.println("This edge has a road!");
			return false;
		}
		if (!checkForOtherRoadsAndStructures(hex, edge))
		{
			return false;
		}
		return true;
	}

	/**
	 * Helped fix the bug in which you could build a road on top of another road.
	 * 	I hadn't gotten the correct pointer. This function's purpose is to grab the correct
	 * 	pointer.
	 * @param loc: We really just need the direction from this.
	 * @param hex: The hex where we actually want to grab the EdgeLocation pointer from.
     */
	private EdgeLocation getCorrectEdgePointer(EdgeLocation loc, Hex hex)
	{
		switch (loc.getDir())
		{
			case NorthWest:
				return hex.getNw();
			case North:
				return hex.getN();
			case NorthEast:
				return hex.getNe();
			case SouthEast:
				return hex.getSe();
			case South:
				return hex.getS();
			case SouthWest:
				return hex.getSw();
			default:
				return null;
		}
	}

	/**
	 * Function that analyzes the neighboring settlements and roads.
	 * We don't need to worry about looking for cities since settlements will
	 * always be there.
	 * Directions: NorthWest, North, NorthEast, SouthEast, South, SouthWest.
     */
	private boolean checkForOtherRoadsAndStructures(Hex hex1, EdgeLocation edgeIAmTryingToPlaceRoadOn)
	{
		VertexLocation up = getUpVertex(edgeIAmTryingToPlaceRoadOn, hex1);
		VertexLocation down = getDownVertex(edgeIAmTryingToPlaceRoadOn, hex1);
		EdgeLocation upEdge = getUpEdge(edgeIAmTryingToPlaceRoadOn, hex1);
		EdgeLocation downEdge = getDownEdge(edgeIAmTryingToPlaceRoadOn, hex1);
		// Now need to do this with adjacent hex
		Hex adjacent = computeAdjacentHex(hex1, edgeIAmTryingToPlaceRoadOn);
		EdgeLocation oppositeEdge = computeOppositeEdge(edgeIAmTryingToPlaceRoadOn, adjacent);
		EdgeLocation upEdgeAdjacent = getUpEdge(oppositeEdge, adjacent);
		EdgeLocation downEdgeAdjacent = getDownEdge(oppositeEdge, adjacent);
		if (hex1 == null)
		{
			//System.out.println("cannot build: the hex is null");
			return false;
		}
		if (adjacent == null)
		{
			//System.out.println("cannot build: the adjacent edge is null");
			return false;
		}
		if (hex1.getResourcetype() == HexType.WATER && adjacent.getResourcetype() == HexType.WATER)
		{
			//System.out.println("cannot build: the resource types are both water");
			return false;
		}
		if (up != null && up.isHassettlement())
		{
			if (up.getSettlement().getOwner().equals(playerID))
			{
				return !(doesSettlementHaveRoadsAttached_New(edgeIAmTryingToPlaceRoadOn, up, hex1, adjacent)
						&& ModelFacade.facadeCurrentGame.getModel().getTurntracker().getStatus() == TurnStatus.SECONDROUND);
			}
		}
		if (down != null && down.isHassettlement())
		{
			if (down.getSettlement().getOwner().equals(playerID))
			{
				return !(doesSettlementHaveRoadsAttached_New(edgeIAmTryingToPlaceRoadOn, down, hex1, adjacent)
						&& ModelFacade.facadeCurrentGame.getModel().getTurntracker().getStatus() == TurnStatus.SECONDROUND);
			}
		}
		//System.out.println("ENTER LOTS OF CRAP: "); // <- this is beautiful
		//System.out.println("upEdge has road: " + upEdge.hasRoad());
		//System.out.println("downEdge has road: " + downEdge.hasRoad());
		//System.out.println("upEdgeAdjacent has road: " + upEdgeAdjacent.hasRoad());
		//System.out.println("downEdgeAdjacent has road: " + downEdgeAdjacent.hasRoad());
		//System.out.println("upEdge player " + upEdge.getRoadPiece().getPlayerWhoOwnsRoad().getNumber());
		//System.out.println("downEdge player " + downEdge.getRoadPiece().getPlayerWhoOwnsRoad().getNumber());
		//System.out.println("upEdgeAdjacent player " + upEdgeAdjacent.getRoadPiece().getPlayerWhoOwnsRoad().getNumber());
		//System.out.println("downEdgeAdjacent player " + downEdgeAdjacent.getRoadPiece().getPlayerWhoOwnsRoad().getNumber());
		/**
		 * At this point in the second round, they cannot build off of roads.
		 * One exception: this may not work.
		 */
		if (ModelFacade.facadeCurrentGame.getModel().getTurntracker().getStatus() == TurnStatus.SECONDROUND)
		{
			return false;
		}
		if (upEdge != null && upEdge.hasRoad() && upEdge.getRoadPiece().getPlayerWhoOwnsRoad().equals(playerID))
		{
			return true;
		}
		if (downEdge != null && downEdge.hasRoad() && downEdge.getRoadPiece().getPlayerWhoOwnsRoad().equals(playerID))
		{
			return true;
		}
		if (upEdgeAdjacent != null && upEdgeAdjacent.hasRoad() && upEdgeAdjacent.getRoadPiece().getPlayerWhoOwnsRoad().equals(playerID))
		{
			return true;
		}
		if (downEdgeAdjacent != null && downEdgeAdjacent.hasRoad() && downEdgeAdjacent.getRoadPiece().getPlayerWhoOwnsRoad().equals(playerID))
		{
			return true;
		}
		//System.out.println("cannot build: at the end; nothing else became true");
		return false;
	}

	/**
	 * Function to test if each of the four edges has a road.
	 * If one of them returns true, we can't build a piece there in round 2.
     */
	private boolean checkHasRoads(EdgeLocation e1, EdgeLocation e2, EdgeLocation e3, EdgeLocation e4)
	{
		if (e1.hasRoad() || e2.hasRoad() || e3.hasRoad() || e4.hasRoad())
		{
			return true;
		}
		return false;
	}

	/**
	 * If this function returns true, then we CANNOT build a road here in round 2! :)
	 * THIS WORKS LIKE AMAZINGNESS. THOU SHALT NOT DELETE THIS FUNCTION
	 * THOU SHALT PRAISE THIS FUNCTION FOR ITS BEAUTY
	 * @param edgeTryingToBuildRoadOn: the edge we are trying to build the road on
	 * @param v: the vertex location which contains our settlement
	 * @param hex: the hex
	 * @param adj: the adjacent hex
     */
	private boolean doesSettlementHaveRoadsAttached_New(EdgeLocation edgeTryingToBuildRoadOn, VertexLocation v,
														Hex hex, Hex adj)
	{
		switch (edgeTryingToBuildRoadOn.getDir())
		{
			// Adjacent hex is +0, -1
			case North:
				switch (v.getDir())
				{
					// Up Vertex
					case NorthEast:
						// Hex:
						EdgeLocation e1 = hex.getNe();
						EdgeLocation e2 = hex.getN();
						// Adjacent:
						EdgeLocation e3 = adj.getSe();
						EdgeLocation e4 = adj.getS();
						if (checkHasRoads(e1, e2, e3, e4))
						{
							return true;
						}
						break;
					// Down Vertex
					case NorthWest:
						// Hex:
						EdgeLocation e5 = hex.getN();
						EdgeLocation e6 = hex.getNw();
						// Adjacent:
						EdgeLocation e7 = adj.getSw();
						EdgeLocation e8 = adj.getS();
						if (checkHasRoads(e5, e6, e7, e8))
						{
							return true;
						}
						break;
				}
				break;
			// Adjacent hex is +1, -1
			case NorthEast:
				switch (v.getDir())
				{
					// Up vertex:
					case East:
						EdgeLocation e9 = hex.getNe();
						EdgeLocation e10 = hex.getSe();
						EdgeLocation e11 = adj.getSw();
						EdgeLocation e12 = adj.getS();
						if (checkHasRoads(e9, e10, e11, e12))
						{
							return true;
						}
						break;
					// Down vertex:
					case NorthEast:
						EdgeLocation e13 = hex.getNe();
						EdgeLocation e14 = hex.getN();
						EdgeLocation e15 = adj.getSw();
						EdgeLocation e16 = adj.getNw();
						if (checkHasRoads(e13, e14, e15, e16))
						{
							return true;
						}
						break;
				}
				break;
			// Adjacent hex is +1, +0.
			case SouthEast:
				switch (v.getDir())
				{
					// Up vertex:
					case SouthEast:
						EdgeLocation e17 = hex.getS();
						EdgeLocation e18 = hex.getSe();
						EdgeLocation e19 = adj.getNw();
						EdgeLocation e20 = adj.getSw();
						if (checkHasRoads(e17, e18, e19, e20))
						{
							return true;
						}
						break;
					// Down vertex:
					case East:
						EdgeLocation e21 = hex.getNe();
						EdgeLocation e22 = hex.getSe();
						EdgeLocation e23 = adj.getNw();
						EdgeLocation e24 = adj.getN();
						if (checkHasRoads(e21, e22, e23, e24))
						{
							return true;
						}
						break;
				}
				break;
			// Adjacent is +0, +1
			case South:
				switch (v.getDir())
				{
					// Up vertex:
					case SouthWest:
						EdgeLocation e25 = hex.getSw();
						EdgeLocation e26 = hex.getS();
						EdgeLocation e27 = adj.getN();
						EdgeLocation e28 = adj.getNw();
						if (checkHasRoads(e25, e26, e27, e28))
						{
							return true;
						}
						break;
					// Down vertex:
					case SouthEast:
						EdgeLocation e29 = hex.getSe();
						EdgeLocation e30 = hex.getS();
						EdgeLocation e31 = adj.getN();
						EdgeLocation e32 = adj.getNe();
						if (checkHasRoads(e29, e30, e31, e32))
						{
							return true;
						}
						break;
				}
				break;
			// Adjacent is -1, +1
			case SouthWest:
				switch (v.getDir())
				{
					// Up vertex:
					case West:
						EdgeLocation e33 = hex.getNw();
						EdgeLocation e34 = hex.getSw();
						EdgeLocation e35 = adj.getNe();
						EdgeLocation e36 = adj.getN();
						if (checkHasRoads(e33, e34, e35, e36))
						{
							return true;
						}
						break;
					// Down vertex:
					case SouthWest:
						EdgeLocation e37 = hex.getSw();
						EdgeLocation e38 = hex.getS();
						EdgeLocation e39 = adj.getNe();
						EdgeLocation e40 = adj.getSe();
						if (checkHasRoads(e37, e38, e39, e40))
						{
							return true;
						}
						break;
				}
				break;
			// Adjacent hex is -1, +0.
			case NorthWest:
				switch (v.getDir())
				{
					// Up vertex:
					case NorthWest:
						EdgeLocation e41= hex.getN();
						EdgeLocation e42 = hex.getNw();
						EdgeLocation e43 = adj.getSe();
						EdgeLocation e44 = adj.getNe();
						if (checkHasRoads(e41, e42, e43, e44))
						{
							return true;
						}
						break;
					// Down vertex:
					case West:
						EdgeLocation e45 = hex.getNw();
						EdgeLocation e46 = hex.getSw();
						EdgeLocation e47 = adj.getSe();
						EdgeLocation e48 = adj.getS();
						if (checkHasRoads(e45, e46, e47, e48))
						{
							return true;
						}
						break;
				}
				break;
			default:
				return false;
		}
		// If there is nothing nearby, return false.
		return false;
	}

	/**
	 * Computes the opposite edge in the neighboring hexagon.
	 * @param original: original edge
	 * @param adjacent: adjacent hex where we need to compute the new edge
     */
	private EdgeLocation computeOppositeEdge(EdgeLocation original, Hex adjacent)
	{
		if(adjacent==null)
		{
			return null;
		}
		switch (original.getDir())
		{
			case NorthWest:
				return adjacent.getSe();
			case North:
				return adjacent.getS();
			case NorthEast:
				return adjacent.getSw();
			case SouthEast:
				return adjacent.getNw();
			case South:
				return adjacent.getN();
			case SouthWest:
				return adjacent.getNe();
			default:
				break;
		}
		return null;
	}

	/**
	 * Returns the vertex that is clockwisely up from edge
	 * @post: the "return null" case at the end should never be reached.
	 * @param edgeIAmTryingToPlaceRoadOn: the edge we are trying to place the road on
	 * @param hex1: the corresponding hex.
     */
	private VertexLocation getUpVertex(EdgeLocation edgeIAmTryingToPlaceRoadOn, Hex hex1)
	{
		switch (edgeIAmTryingToPlaceRoadOn.getDir())
		{
			case NorthWest:
				return hex1.getNorthwest();
			case North:
				return hex1.getNortheast();
			case NorthEast:
				return hex1.getEast();
			case SouthEast:
				return hex1.getSoutheast();
			case South:
				return hex1.getSouthwest();
			case SouthWest:
				return hex1.getWest();
			default:
				break;
		}
		// This should never be executed.
		return null;
	}

	/**
	 * Gets the vertex directly below the given edge.
     */
	private VertexLocation getDownVertex(EdgeLocation edgeIAmTryingToPlaceRoadOn, Hex hex1)
	{
		switch (edgeIAmTryingToPlaceRoadOn.getDir())
		{
			case NorthWest:
				return hex1.getWest();
			case North:
				return hex1.getNorthwest();
			case NorthEast:
				return hex1.getNortheast();
			case SouthEast:
				return hex1.getEast();
			case South:
				return hex1.getSoutheast();
			case SouthWest:
				return hex1.getSouthwest();
			default:
				break;
		}
		// This should never be executed.
		return null;
	}

	/**
	 * Gets the edge "above" our current one
     */
	private EdgeLocation getUpEdge(EdgeLocation edgeIAmTryingToPlaceRoadOn, Hex hex1)
	{
		if(hex1==null||edgeIAmTryingToPlaceRoadOn==null)
		{
			return null;
		}
		switch (edgeIAmTryingToPlaceRoadOn.getDir())
		{
			case NorthWest:
				return hex1.getN();
			case North:
				return hex1.getNe();
			case NorthEast:
				return hex1.getSe();
			case SouthEast:
				return hex1.getS();
			case South:
				return hex1.getSw();
			case SouthWest:
				return hex1.getNw();
			default:
				break;
		}
		// This should never be executed
		return null;
	}

	/**
	 * Gets the edge "below" our current one
     */
	private EdgeLocation getDownEdge(EdgeLocation edgeIAmTryingToPlaceRoadOn, Hex hex1)
	{
		switch (edgeIAmTryingToPlaceRoadOn.getDir())
		{
			case NorthWest:
				return hex1.getSw();
			case North:
				return hex1.getNw();
			case NorthEast:
				return hex1.getN();
			case SouthEast:
				return hex1.getNe();
			case South:
				return hex1.getSe();
			case SouthWest:
				return hex1.getS();
			default:
				break;
		}
		// This should never be executed
		return null;
	}

	/**
	 * Function to see whether or not we can build a particular road
	 * at a particular location on the map.
	 * @param hex: hex we're trying to build on
	 * @param edge: edge we're trying to build the road on
     */
	public boolean canBuildRoadPieceSetupState(Hex hex, EdgeLocation edge)
	{
		if (hex == null || edge == null) // added the edge one
		{
			return false;
		}
		Hex adjacent = computeAdjacentHex(hex, edge);
		if (adjacent == null)
		{
			//System.out.println("The adjacent hex is null..."); // maybe?
			return false;
		}
		EdgeLocation edge2 = computeOppositeEdge(edge, adjacent);
		// check the other hex
		if (hex.getResourcetype() == HexType.WATER && adjacent.getResourcetype() == HexType.WATER)
		{
			//System.out.println("can't build road piece: both hexes are of type water");
			return false;
		}
		if (!currentPlayer)
		{
			//System.out.println("can't build road piece: not current player");
			return false;
		}
		EdgeLocation truth = getCorrectEdgePointer(edge, hex);
		// don't want this to be null!
		if (truth == null)
		{
			return false;
		}
		if (truth.hasRoad())
		{
			//System.out.println("can't build road piece: edge already has a road on it");
			return false;
		}
		if (!checkForOtherRoadsAndStructures(hex, edge))
		{
			//System.out.println("can't build road piece: no adjacent roads or structures belonging to player");
			return false;
		}
		//System.out.println("it all worked");
		return true;
	}

	/**
	 * Function to actually build the road during the setup phase.
	 * Players should only be able to build two road pieces.
     */
	public boolean buildRoadPieceSetupState(Hex hex, EdgeLocation edge)
	{
		if (canBuildRoadPieceSetupState(hex, edge))
		{
			Hex adjacent = computeAdjacentHex(hex, edge);
			EdgeLocation edge2 = computeOppositeEdge(edge, adjacent);
			// check the other hex
			if (hex.getResourcetype() == HexType.WATER && adjacent.getResourcetype() == HexType.WATER)
			{
				//System.out.println("can't build road piece: both hexes are of type water");
				return false;
			}
			else
			{
				RoadPiece piece = hex.buildRoad(edge, playerID);
				edge.setHasRoad(true);
				edge2.setHasRoad(true);
				//piece.setLocation(edge.getNormalizedLocation());
				RoadPiece piece2 = adjacent.buildRoad(edge2, playerID);
				//piece2.setLocation(edge2.getNormalizedLocation()); // changed this
				roadPieces.add(piece);
				//roadPieces.add(piece2);
				return true;
			}
		}
		return false;
	}

	private boolean isSettlementTurnOver(int round1Or2)
	{
		int compare;
		if (round1Or2 == 1)
		{
			compare = 1;
		}
		else
		{
			compare = 2;
		}
		if (settlements.size() == compare && roadPieces.size() == compare)
		{
			return true;
		}
		return false;
	}

		public void buildSettlementNormal(Hex buildingon, VertexLocation locationofsettlement) throws Exception {
		if (canBuildSettlementNormal(buildingon, locationofsettlement)) {
			buildingon.buildSettlementNormal(locationofsettlement, this.playerID);
			//resources.setBrick(resources.getBrick() - 1);
			//resources.setWheat(resources.getWheat() - 1);
			//resources.setSheep(resources.getSheep() - 1);
			//resources.setWood(resources.getWood() - 1);
			//this.numSettlementsRemaining--;
			//System.out.println("The number of brick peices left " + resources.getBrick());
			//System.out.println("my num of settlements Remaning is " + numSettlementsRemaining);
		}
	}

	/**
	 * Determines whether or not the player can accept a current trade.
	 * May need a TradeParameters object in order to assist with this
	 * method.
	 *
	 * Note: I am not sure yet if this method is necessary! There are
	 * implementations elsewhere. We should keep it for now until we
	 * know for sure.
	 */
	public boolean canAcceptTrade()
	{
		return false;
	}


	public boolean canBuildSettlementStartup(Hex hex, VertexLocation myLocation) throws Exception {

		if(hex==null||myLocation==null)
		{
			Exception e=new Exception();
			//e.printStackTrace();
			return false;
		}
		if (!hex.canBuildSettlementHereStartup(myLocation))
		{
			return false;
		}

		if (numSettlementsRemaining <= 0)
		{
			return false;
		}
		return true;
	}

	/**
	 * Determines whether or not a player can build a settlement on a particular hex
	 * @param hex: the hex
     */
	public boolean canBuildSettlementNormal(Hex hex, VertexLocation myLocation) throws Exception {
		if(hex==null||myLocation==null)
		{
			//System.out.println(" A NULL HEX");
			return false;
		}
		if (!hex.canBuildSettlementHereNormal(myLocation))
		{
			//System.out.println("MY HEX IS TOO BLAME");
			return false;
		}
		/*
		if (resources.getSheep() < 1 || resources.getWheat() < 1
				|| resources.getBrick() < 1 || resources.getWood() < 1)
		{
			return false;
		}
		if (numSettlementsRemaining <= 0)
		{
			return false;
		}
		*/
		return true;
	}

	public boolean canBuildCity(Hex hex, VertexLocation myLocation) throws Exception
	{
		if(hex==null||myLocation==null)
		{
			Exception e=new Exception();
			//e.printStackTrace();
			throw e;
		}

		if (!hex.canBuildCityHere(myLocation.getDir()))
		{
			return false;
		}
		/*
		if (resources.getOre() < 3 || resources.getWheat() < 2)
		{
			return false;
		}
		if (numCitiesRemaining <= 0)
		{
			return false;
		}
		*/
		return true;
	}

	/**
	 * Getters and setters:
	 */
	public int getNumCitiesRemaining()
	{
		return this.numCitiesRemaining;
	}
	public boolean isCurrentPlayer()
	{
		return currentPlayer;
	}

	public void setCurrentPlayer(boolean currentPlayer)
	{
		this.currentPlayer = currentPlayer;
	}

	public ResourceList getResources()
	{
		return resources;
	}

	public void setResources(ResourceList resources)
	{
		this.resources = resources;
	}



	public ArrayList<Settlement> getSettlements()
	{
		return settlements;
	}

	/**
	 * Adds the settlement to the player's settlements
	 * @pre: settlement is not null
	 * player has the proper resources required to build
	 * settlement can be placed at the given location
     */
	public void addToSettlements(Settlement settlement)
	{
		if (settlements.size() == 0 &&
				ModelFacade.facadeCurrentGame.getModel().getTurntracker().getStatus() == TurnStatus.FIRSTROUND)
		{
			//System.out.println("Setting canBuildFromMeInRound2 to false");
			settlement.setCanBuildFromMeInRound2(false);
		}
		settlements.add(settlement);
	}

	public void setSettlements(ArrayList<Settlement> settlements) {
		this.settlements = settlements;
	}

	public ArrayList<City> getCities()
	{
		return cities;
	}
	/**
	 * Adds the city to the player's cities
	 * @pre: city is not null
	 * city can be placed at the given location
	 * player has the proper resources required to build
	 * (including there being 1 settlement there)
     */
	public void addToCities(City city)
	{
		cities.add(city);
	}

	private static final int DEFAULT_VAL = 0;
	private static final int MIN = 1;
	private static final int TWO_WAY = 2;
	private static final int THREE_WAY = 3;
	private static final int FOUR_WAY = 4;
	private static final int MAX_NUM_CITIES = 4;
	private static final int MAX_NUM_SETTLEMENTS = 5;
	private static final int MAX_NUM_ROADS = 15;
	
	public void setIsDiscarded(boolean discarded)
	{
		this.discarded = discarded;
	}
	public boolean getIsDiscarded()
	{
		return this.discarded;
	}
	public int getArmySize() {
		return armySize;
	}

	public int getRoadSize() {
		return roadSize;
	}

	public ArrayList<RoadPiece> getRoadPieces()
	{
		return roadPieces;
	}

	public void setRoadPieces(ArrayList<RoadPiece> roadPieces)
	{
		this.roadPieces = roadPieces;
	}

	public void addToRoadPieces(RoadPiece roadPiece)
	{
		roadPieces.add(roadPiece);
	}
	public void setNumSettlementsRemaining(int number)
	{
		this.numSettlementsRemaining=number;
	}
	public  void setNumCitiesRemaining(int number)
	{
		this.numCitiesRemaining=number;
	}
	public DevCardList getOldDevCards()
	{
		return this.oldDevCards;
	}
	public DevCardList getNewDevCards()
	{
		return this.newDevCards;
	}

	public void setOldDevCards(DevCardList oldDevCards) {
		this.oldDevCards = oldDevCards;
	}

	public void setNewDevCards(DevCardList newDevCards) {
		this.newDevCards = newDevCards;
	}

	public boolean getplayedDevCard()
	{
		return this.playedDevCard;
	}


	public int getNumSettlementsRemaining()
	{
		return numSettlementsRemaining;
	}
	public void setPlayedDevCard(boolean playedDevCard)
	{
		this.playedDevCard = playedDevCard;
	}

	public int getNumMonuments()
	{
		return this.numMonuments;
	}

	public void setNumMonuments(int numMonuments)
	{
		this.numMonuments = numMonuments;
	}

	public int getNumVictoryPoints()
	{
		return this.numVictoryPoints;
	}

	public void setNumVictoryPoints(int numVictoryPoints)
	{
		this.numVictoryPoints = numVictoryPoints;
	}

	public int getNumRoadPiecesRemaining()
	{
		return numRoadPiecesRemaining;
	}

	public void setNumRoadPiecesRemaining(int numRoadPiecesRemaining)
	{
		this.numRoadPiecesRemaining = numRoadPiecesRemaining;
	}

	public int getNumSoldierCards()
	{
		return numSoldierCards;
	}

	public void setNumSoldierCards(int numSoldierCards)
	{
		this.numSoldierCards = numSoldierCards;
	}

	public boolean canAffordRoad()
	{
		if (resources.getBrick() < MIN || resources.getWood() < MIN)
		{
			return false;
		}
		return true;
	}

	public boolean canAffordSettlement()
	{
		if (resources.getSheep() < 1 || resources.getWheat() < 1
				|| resources.getBrick() < 1 || resources.getWood() < 1)
		{
			return false;
		}
		return true;
	}

	public boolean canAffordCity()
	{
		if (resources.getOre() < 3 || resources.getWheat() < 2)
		{
			return false;
		}
		return true;
	}

	public boolean canAffordDevCard()
	{
		if(resources.getOre()>0)
		{
			if(resources.getSheep()>0)
			{
				if(resources.getWheat()>0)
				{
					return true;
				}

			}
		}
		return false;
	}
	public Index getPlayerIndex()
	{
		return this.playerIndex;
	}
	public void setPlayerIndex(Index playerIndex)
	{
		this.playerIndex=playerIndex;
	}

	public void setCities(ArrayList<City> cities) {
		this.cities = cities;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	/**
	 * Overriding the CompareTo method!
	 */
	@Override
	public int compareTo(Player o)
	{
		return playerID.compareTo(o.getPlayerID());
	}

	public void setArmySize(int armySize)
	{
		this.armySize = armySize;		
	}

}
