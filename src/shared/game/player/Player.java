package shared.game.player;

import shared.definitions.CatanColor;
import shared.definitions.PortType;
import shared.definitions.ResourceType;
import shared.game.Bank;
import shared.game.DevCardList;
import shared.game.ResourceList;
import shared.game.map.Hex.Hex;
import shared.game.map.Index;
import shared.game.map.Port;
import shared.game.map.Robber;
import shared.game.map.vertexobject.City;
import shared.game.map.vertexobject.Settlement;
import shared.locations.EdgeDirection;
import shared.locations.EdgeLocation;
import shared.locations.HexLocation;
import shared.locations.VertexLocation;

import java.util.ArrayList;

/**
 * @author Alex
 * Player: Class that represents each individual player in the game.
 * There are 4 players in each game; thus, there are 4 objects of type 
 * Player in each game. 
 */
public class Player 
{
	private int roadSize=0;
	private int armySize=0;
	/**
	 * numCities: How many cities an individual player has.
	 */
	private int numCitiesRemaining = MAX_NUM_CITIES;
	/**
	 * Color: Color is received as a String from the JSON file.
	 * However, we need to use the enum type CatanColor.
	 */
	private CatanColor color = null;
	
	/**
	 * discarded: whether or not they discarded a card
	 */
	private boolean discarded = false;
	
	/**
	 * numMonuments: how many monuments a player has.
	 * I don't know what this is, and I don't think we need this. Even though I think I created it.
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
	 * Each player has their own Index.
	 * Recording this information is key for calculating turns
	 * and getting the correct Player pointers.
	 * 
	 * We might even want to add a list of Indexes to the main game map,
	 * representing each player (if the need arises). 
	 */
	private Index playerIndex = null; // don't need this I don't think
	
	/**
	 * ID of the player
	 * received from JSON file
	 * 
	 * NOTE: Keep this until we determine if there's a difference
	 * between playerID and playerIndex. 
	 */
	private Index playerID = null;
	
	/**
	 * ResourceList: List of all the resource cards
	 * (brick, ore, sheep, wheat, wood)
	 */
	private ResourceList resources = null;

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
	private int numRoadPiecesRemaining = 15;
	
	/**
	 * How many settlements the player CAN BUILD.
	 */
	private int numSettlementsRemaining = MAX_NUM_SETTLEMENTS;
	
	/**
	 * How many soldiers (soldier cards) the player CAN BUILD.
	 */
	private int numSoldierCards = 0;
	
	/**
	 * How many victory points the player has
	 * Everyone starts off with 2. 10+ on your turn to win!
	 */
	private int numVictoryPoints = 0;

	/**
	 * Creating this because Java doesn't like passing booleans by reference -_-
	 * Please do not delete
	 */
	boolean neitherBorderingEdgeHasARoad = false;

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
		this.playerID = playerID;
		resources = new ResourceList();
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
		HexLocation currentRobberLocation = Robber.getSingleton().getLocation();
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
			if (settlement.getHexLocation().equals(Robber.getSingleton().getLocation()))
			{
				hasAreaAffectedByRobber = true;
			}
		}
		for (City city : cities)
		{
			if (city.getHexLocation().equals(Robber.getSingleton().getLocation()))
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
	 * Need to replace sampleBank with Bank singleton most likely.
	 * @param tradeType: the type of trade being performed, based on the port type
	 * @param typeFor_3_Or4Way: if it's 3-way or 4-way, we will need additional specifications
	 * @param typeRequesting: which type the player wants!
	 *
	 * Second PortType is only necessary if there is a 3:1 or 4:1 trade. This is because it is saved as THREE
	 * or FOUR, and thus we don't know if that means WHEAT, SHEEP, WOOD, etc.
	 *
	 * If the second type is listed as BLANK, then it is a 2:1 trade, and thus said second variable will never be used.
     */

	public boolean canDoTradeWithBank(PortType tradeType, PortType typeFor_3_Or4Way, ResourceType typeRequesting) throws Exception
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
			case FOUR: // 4:1.
				if (!multiWayTrade(typeFor_3_Or4Way, FOUR_WAY))
				{
					return false;
				}
				break;
			default:
				assert false;
		}
		if (!Bank.getSingleton().CanBankGiveResourceCard(typeRequesting))
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
	private boolean multiWayTrade(PortType theType, int threeOrFour)
	{
		assert(!theType.equals(PortType.BLANK));
		assert(!theType.equals(PortType.THREE));
		assert(!theType.equals(PortType.FOUR));
		assert(threeOrFour == THREE_WAY || threeOrFour == FOUR_WAY);
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
				assert false;
		}
		return true;
	}

	
	/**
	 * Determines whether or not the player can buy/build a road.
	 * SetUp phase is when the player can place a road wherever they want, I think.
	 * Other than the setup, the player must have an adjacent road, city, or
	 * settlement in order to build a road.
	 */
	public boolean canBuildRoad(Hex hex, EdgeLocation edge)
	{
		neitherBorderingEdgeHasARoad = false;
		if (resources.getBrick() < MIN || resources.getWood() < MIN || numRoadPiecesRemaining < MIN)
		{
			return false;
		}
		if (edge.hasRoad())
		{
			return false;
		}
		if (!checkAdjacentEdges(hex, edge))
		{
			return false;
		}
		if (!checkAdjacentSettlements(hex, edge))
		{
			return false;
		}
		return true;
	}

	private boolean checkAdjacentSettlements(Hex hex, EdgeLocation edge)
	{
		VertexLocation vertexClockwiseUpper = null;
		VertexLocation vertexClockwiseLower = null;
		switch(edge.getDir())
		{
			case NorthWest:
				vertexClockwiseUpper = hex.getNorthwest();
				vertexClockwiseLower = hex.getWest();
				break;
			case North:
				vertexClockwiseUpper = hex.getNortheast();
				vertexClockwiseLower = hex.getNorthwest();
				break;
			case NorthEast:
				vertexClockwiseUpper = hex.getEast();
				vertexClockwiseLower = hex.getNortheast();
				break;
			case SouthEast:
				vertexClockwiseUpper = hex.getSoutheast();
				vertexClockwiseLower = hex.getEast();
				break;
			case South:
				vertexClockwiseUpper = hex.getSouthwest();
				vertexClockwiseLower = hex.getSoutheast();
				break;
			case SouthWest:
				vertexClockwiseUpper = hex.getWest();
				vertexClockwiseLower = hex.getSouthwest();
				break;
			default:
				assert false;
		}
		if (vertexClockwiseLower.isHassettlement() && !vertexClockwiseLower.getSettlement().getOwner().equals(playerID))
		// need a pointer to settlement; get playerID
		{
			return false;
		}
		if (vertexClockwiseUpper.isHassettlement() && !vertexClockwiseUpper.getSettlement().getOwner().equals(playerID))
		{
			return false;
		}
		if (vertexClockwiseLower.isHascity() && !vertexClockwiseLower.getCity().getOwner().equals(playerID))
		{
			return false;
		}
		if (vertexClockwiseUpper.isHascity() && !vertexClockwiseUpper.getCity().getOwner().equals(playerID))
		{
			return false;
		}
		// there HAS to be a city, settlement, or road adjacent to it.
		if (!vertexClockwiseLower.isHassettlement() && !vertexClockwiseUpper.isHassettlement()
				&& !vertexClockwiseLower.isHascity() && !vertexClockwiseUpper.isHascity() && neitherBorderingEdgeHasARoad)
		{
			return false;
		}

		return true;
	}

	private boolean checkAdjacentEdges(Hex hex, EdgeLocation edge)
	{
		assert(edge != null && hex != null);
		EdgeLocation adjacentEdgeClockwiseUp = null;
		EdgeLocation adjacentEdgeClockwiseDown = null;
		switch (edge.getDir())
		{
			case NorthWest:
				adjacentEdgeClockwiseUp = hex.getN();
				adjacentEdgeClockwiseDown = hex.getSw();
				break;
			case North:
				adjacentEdgeClockwiseUp = hex.getNe();
				adjacentEdgeClockwiseDown = hex.getNw();
				break;
			case NorthEast:
				adjacentEdgeClockwiseUp = hex.getSe();
				adjacentEdgeClockwiseDown = hex.getN();
				break;
			case SouthEast:
				adjacentEdgeClockwiseUp = hex.getS();
				adjacentEdgeClockwiseDown = hex.getNe();
				break;
			case South:
				adjacentEdgeClockwiseUp = hex.getSw();
				adjacentEdgeClockwiseDown = hex.getSe();
				break;
			case SouthWest:
				adjacentEdgeClockwiseUp = hex.getNw();
				adjacentEdgeClockwiseDown = hex.getS();
				break;
			default:
				assert false;
		}
		if (adjacentEdgeClockwiseUp.hasRoad() && !adjacentEdgeClockwiseUp.getRoad().getPlayerWhoOwnsRoad().equals(playerID))
		{
			return false;
		}
		if (adjacentEdgeClockwiseDown.hasRoad() && !adjacentEdgeClockwiseDown.getRoad().getPlayerWhoOwnsRoad().equals(playerID))
		{
			return false;
		}
		// doesn't work completely because they CAN build it if they have a city there.
		if (!adjacentEdgeClockwiseUp.hasRoad() && !adjacentEdgeClockwiseDown.hasRoad())
		{
			neitherBorderingEdgeHasARoad = true;
		}
		else
		{
			neitherBorderingEdgeHasARoad = false;
		}
		return true; // if no issues
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

	/**
	 * Determines whether or not a player can build a settlement on a particular hex
	 * @param hex: the hex
     */
	public boolean canBuildSettlement(Hex hex, VertexLocation myLocation)
	{
		if (!hex.canBuildSettlementHere(myLocation))
		{
			return false;
		}
		if (resources.getSheep() < 1 || resources.getWheat() < 1
				|| resources.getBrick() < 1 || resources.getWood() < 1)
		{
			return false;
		}
		if (numSettlementsRemaining <= 0)
		{
			return false;
		}
		return true;
	}

	public boolean canBuildCity(Hex hex, VertexLocation myLocation)
	{
		if (!hex.canBuildCityHere(myLocation.getDir()))
		{
			return false;
		}
		if (resources.getOre() < 3 || resources.getWheat() < 2)
		{
			return false;
		}
		if (numCitiesRemaining <= 0)
		{
			return false;
		}
		return true;
	}

	/**
	 * Getters and setters:
	 */
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
		settlements.add(settlement);
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

	public int getArmySize() {
		return armySize;
	}

	public int getRoadSize() {
		return roadSize;
	}
}
