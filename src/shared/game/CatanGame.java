package shared.game;

import java.util.ArrayList;

import shared.chat.Chat;
import shared.game.map.CatanMap;
import shared.game.player.Player;
/**
 * Catan Game object so that we can have a game accessible to be modified. 
 */


public class CatanGame 
{
	
	/**
	 * an array containing the players for the game. 
	 */
	ArrayList<Player>myplayers=new ArrayList();
	/**
	 * the map for the game. 
	 */
	CatanMap mymap=new CatanMap(0, null);
	/**
	 * the chat system
	 */
	Chat mychat=new Chat();
	
	/**
	 *  a function to see if we can start the game
	 *  @exception throws exception if not able to start the game for anything that would prevent from starting
	 *  such as not enough players, invalid Map,  Internet problems,  Server failure etc. 
	 *  @custom.mytag1 post:return true if we can start the game. 
	 * @return
	 */
	boolean canStartGame()
	{
	return true;	
	}
	/**
	 * a function that starts the game nothing too fancy.   
	 */
	void startGame()
	{
		
	}

}
