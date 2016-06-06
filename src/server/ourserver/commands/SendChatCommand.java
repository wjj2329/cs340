package server.ourserver.commands;

import client.model.TurnStatus;
import server.ourserver.ServerFacade;
import shared.chat.Chat;
import shared.chat.ChatLine;
import shared.game.CatanGame;
import shared.game.map.Index;
import shared.game.player.Player;

/**
 * The SendChatCommand
 */
public class SendChatCommand implements ICommand {

	/**
	 * Execute the task:
	 * 	at anytime the player can send a string (their message) and the chat will contain it	
	 */
	String message;
	int playerindex;
	int gameid;
	private static int pos=0;

	public SendChatCommand(String message, int playerindex, int gameid)
	{
		this.message=message;
		this.playerindex=playerindex;
		this.gameid=gameid;
	}
	@Override
	public Object execute()
	{
		CatanGame currentgame= ServerFacade.getInstance().getGameByID(gameid);
		currentgame.getModel().setVersion(currentgame.getModel().getVersion()+1);
		Player playertoupdate=null;
		for(Index myind:currentgame.getMyplayers().keySet())
		{
			if(currentgame.getMyplayers().get(myind).getPlayerIndex().getNumber()==playerindex)
			{
				playertoupdate=currentgame.getMyplayers().get(myind);
			}
		}
		//System.out.println("MY message is this "+message);
		//System.out.println("The player who sent this message is this "+playertoupdate.getName());
		currentgame.getModel().getTurntracker().setStatus(TurnStatus.PLAYING);
		currentgame.getMychat().getChatMessages().getMessages().add(playerindex,new ChatLine(playertoupdate.getName()+": "+message+"#"+pos,playertoupdate.getName()));//not sure if this is correct. lol
		//System.out.println("I ADD TO SERVER");
		pos++;
		return null;
	}

}
