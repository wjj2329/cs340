package server.ourserver.commands;

import server.ourserver.ServerFacade;
import shared.game.CatanGame;
import shared.game.ResourceList;
import shared.game.map.Hex.Hex;
import shared.game.map.Index;
import shared.game.map.vertexobject.Settlement;
import shared.game.player.Player;
import shared.locations.HexLocation;
import shared.locations.VertexLocation;

/**
 * The BuildSettlementCommand class
 */
public class BuildSettlementCommand implements ICommand {

	
	/**
	 * Executes the task
	 * 	player builds a settlement at the vertex location 
	 */
	@Override
	public Object execute()
	{

		// TODO Auto-generated method stub
		return null;
	}

	public void buildsettlement(int playerIndex, HexLocation location, VertexLocation vertex)
	{
		CatanGame currentgame=new CatanGame();//this won't work I need a current game some how.  Will wait till that is implemented.  willswap this variable with that
		Index myindex=new Index(playerIndex);
		Player playertoupdate=null;
		for(Index myind:currentgame.getMyplayers().keySet())
		{
			if(currentgame.getMyplayers().get(myind).getPlayerIndex().getNumber()==playerIndex)
			{
				playertoupdate=currentgame.getMyplayers().get(myind);
			}
		}
		ResourceList newlist=playertoupdate.getResources();
		newlist.setBrick(newlist.getBrick()-1);
		newlist.setSheep(newlist.getSheep()-1);
		newlist.setWheat(newlist.getWheat()-1);
		newlist.setWood(newlist.getWood()-1);
		vertex.setHassettlement(true);
		Settlement settle1 = new Settlement(location, vertex, myindex);
		Hex h = currentgame.getMymap().getHexes().get(settle1.getHexLocation());
		try {
			h.buildSettlement(vertex, new Index(playerIndex));
		} catch (Exception e) {
			e.printStackTrace();
		}
		currentgame.getMymap().getSettlements().add(settle1);
		settle1.setOwner(myindex);
		vertex.setSettlement(settle1);
		currentgame.getMyplayers().get(myindex).addToSettlements(settle1);
	}

}
