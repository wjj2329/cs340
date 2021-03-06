package client.communication;

import client.base.*;
import client.model.ModelFacade;
import client.model.TurnStatus;
import server.proxies.IServer;
import server.proxies.ServerProxy;
import shared.definitions.CatanColor;
import shared.game.CatanGame;
import shared.game.map.Index;
import shared.game.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;


/**
 * Chat controller implementation
 */
public class ChatController extends Controller implements IChatController, Observer
{
	/**
	 * Please note that the localPlayer object does NOT get a current color until later!
	 * That's why, down below, I don't use the playerSendingChat variable. Do not change this!
	 * EVER! :O never mind...
	 */
	private Player playerSendingChat = ModelFacade.facadeCurrentGame.currentgame.getCurrentPlayer();
	//new Player("Broses", CatanColor.RED, new Index(1));
	//ModelFacade.facadeCurrentGame
	private List<LogEntry> allLogEntries = new ArrayList<>();
	//private IServer server = new ServerProxy();
	private IAction sendChatAction;
	public ChatController(IChatView view)
	{
		super(view);
		ModelFacade.facadeCurrentGame.addObserver(this);

	}

	@Override
	public IChatView getView() {
		return (IChatView)super.getView();
	}

	@Override
	public void sendMessage(String message)
	{
		if(!ModelFacade.facadeCurrentGame.getModel().getTurntracker().getStatus().equals(TurnStatus.FIRSTROUND)&&!ModelFacade.facadeCurrentGame.getModel().getTurntracker().equals(TurnStatus.SECONDROUND))
		{
			//System.out.println("MY Player Index is this "+ModelFacade.facadeCurrentGame.getLocalPlayer().getPlayerIndex().getNumber());
			ModelFacade.facadeCurrentGame.getServer().sendChat("sendChat",
					ModelFacade.facadeCurrentGame.getLocalPlayer().getPlayerIndex().getNumber(), message);
		}
	}



	@Override
	public void update(Observable o, Object arg)
	{
		if(ModelFacade.facadeCurrentGame.getModel()==null)
		{
			return;
		}
		if(ModelFacade.facadeCurrentGame.getModel().getTurntracker()==null)
		{
			return;
		}
		if(ModelFacade.facadeCurrentGame.getModel().getTurntracker().getStatus()==null)
		{
			return;
		}
		//System.out.println("I GET UPDATED in the chat update");
		if(!ModelFacade.facadeCurrentGame.getModel().getTurntracker().getStatus().equals(TurnStatus.FIRSTROUND)&&!ModelFacade.facadeCurrentGame.getModel().getTurntracker().getStatus().equals(TurnStatus.SECONDROUND))
		{
			//System.out.println("I GET THROUGH THE IF");
			List<LogEntry> entries = new ArrayList<>();
			CatanColor playercolor = CatanColor.PUCE;
			//System.out.println(ModelFacade.facadeCurrentGame.currentgame.getMychat().getChatMessages().getMessages().size());
			for (int i = 0; i < ModelFacade.facadeCurrentGame.currentgame.getMychat().getChatMessages().getMessages().size(); i++)
			{
				for (Index loc : ModelFacade.facadeCurrentGame.currentgame.getMyplayers().keySet()) {
					if (ModelFacade.facadeCurrentGame.currentgame.getMychat().getChatMessages().getMessages().get(i).getSource().equals(ModelFacade.facadeCurrentGame.currentgame.getMyplayers().get(loc).getName())) {
						playercolor = ModelFacade.facadeCurrentGame.currentgame.getMyplayers().get(loc).getColor();
					}
				}
				//System.out.println("I Add to the entries this "+playercolor.toString()+" this message "+ModelFacade.facadeCurrentGame.getMychat().getChatMessages().getMessages().get(i).getMessage());
				//System.out.println("THE SIZE OF THIS CHAT ARRAY IS ALSO "+ModelFacade.facadeCurrentGame.getMychat().getChatMessages().getMessages().size());
				entries.add(new LogEntry(playercolor, ModelFacade.facadeCurrentGame.getMychat().getChatMessages().getMessages().get(i).getMessage()));
			}

			List<LogEntry> entries2 = new ArrayList<>();

			while(!entries.isEmpty())
			{
				int smallest=100000;
				int indexforsmallest=0;
				String[]mywords= new String[50];
				String realmessage="";
				for (int i = 0; i < entries.size(); i++)
				{

					String message=entries.get(i).getMessage();
					mywords=message.split("#$%");
					int check=Integer.parseInt(mywords[1]);
					if(check<smallest)
					{
						realmessage=mywords[0];
						smallest=check;
						indexforsmallest=i;
					}
				}
				//System.out.println("I ADD SOMETHING "+mywords[0]);
				entries2.add(new LogEntry(entries.get(indexforsmallest).getColor(),realmessage));
				entries.remove(indexforsmallest);
			}


			if(entries2.size()!=0) {
				getView().setEntries(entries2);
			}
		}

	}
}

