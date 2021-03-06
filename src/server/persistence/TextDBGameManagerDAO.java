package server.persistence;

import server.ourserver.ServerFacade;
import server.ourserver.commands.*;
import shared.game.CatanGame;

import org.json.JSONException;
import org.json.JSONObject;
import shared.game.ResourceList;
import shared.locations.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by williamjones on 6/7/16.
 *
 */
public class TextDBGameManagerDAO implements IGameManager
{


    public static int commandNumber=0;
    public TextDBGameManagerDAO() throws IOException
    {
           // db.write("{");
    }
    
	@Override
	public void storeGameModel(int gameid)
    {
		// TODO Auto-generated method stub
		
	}
    @Override
    public void addCommand(ICommand commandObject, int gameId) throws JSONException, IOException
    {
        FileWriter db=new FileWriter("commands"+gameId+".txt",true);
        db.write((commandObject.toString()));
        //db.write("{\"currentvalue\":"+commandNumber+"}");

        db.flush();
    }
	@Override
	public ArrayList<ICommand> getCommands(int idgame) throws JSONException, IOException {
        ArrayList<ICommand> commandsloadedfromdb=new ArrayList<>();

        FileReader db=new FileReader("commands"+idgame+".txt");
        Scanner myscanner=null;
        myscanner=new Scanner(db);
        StringBuilder getting=new StringBuilder();
        while(myscanner.hasNext())
        {
            getting.append(myscanner.next());
        }
        if(getting.charAt(1)==',')
        {
            getting.deleteCharAt(1);
        }
        getting.append("}");
        System.out.println("MY NICE JSON FILE IS THIS "+getting.toString());
        JSONObject mycommands=null;
        try {
             mycommands=new JSONObject(getting.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(mycommands.toString());
        for(int i=0; i<9;i++)
        {
            JSONObject jsonObject=null;
            try {
                jsonObject=mycommands.getJSONObject(Integer.toString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String type= jsonObject.getString("type");
            switch(type)
            {
                case "SendChatCommand":
                {
                    String message=jsonObject.getString("message");
                    int playerindex=jsonObject.getInt("playerindex");
                    int gameid=jsonObject.getInt("gameid");
                    commandsloadedfromdb.add(new SendChatCommand(message,playerindex,gameid));
                    break;
                }
                case "RollNumberCommand":
                {
                    int rollNumber=jsonObject.getInt("rollNumber");
                    int gameid=jsonObject.getInt("gameid");
                    commandsloadedfromdb.add(new RollNumberCommand(rollNumber,gameid));
                    break;
                }
                case "RobPlayerCommand":
                {
                    HexLocation location=new HexLocation(jsonObject.getInt("x"),jsonObject.getInt("y"));
                    int playerRobbing=jsonObject.getInt("playerRobbing");
                    int playerbeingrobbed=jsonObject.getInt("playerbeingrobbed");
                    int gameid=jsonObject.getInt("gameid");
                    commandsloadedfromdb.add(new RobPlayerCommand(location,playerRobbing,playerbeingrobbed,gameid));
                    break;
                }
                case "PlayYearOfPlentyCommand":
                {
                    int gameID=jsonObject.getInt("gameid");
                    String resource2=jsonObject.getString("resource2");
                    String resource1=jsonObject.getString("resource1");
                    int playerindex=jsonObject.getInt("playerindex");
                    commandsloadedfromdb.add(new PlayYearOfPlentyCommand(gameID,resource1,resource2,playerindex));
                    break;
                }
                case "PlaySoldierCommand":
                {
                    HexLocation location=new HexLocation(jsonObject.getInt("x"),jsonObject.getInt("y"));
                    int playerRobbing=jsonObject.getInt("playerRobbing");
                    int playerBeingRobbed=jsonObject.getInt("playerBeingRobbed");
                    int gameid=jsonObject.getInt("gameid");
                    commandsloadedfromdb.add(new PlaySoldierCommand(location,playerRobbing,playerBeingRobbed,gameid));
                    break;
                }
                case "PlayRoadBuildingCommand":
                {
                    int playerIndex=jsonObject.getInt("playerIndex");
                    HexLocation location=new HexLocation(jsonObject.getInt("x"),jsonObject.getInt("y"));
                    EdgeLocation edge=new EdgeLocation(location,getDirectionFromString(jsonObject.getString("edge")));
                    boolean free=jsonObject.getBoolean("free");
                    int gameid=jsonObject.getInt("gameid");
                    commandsloadedfromdb.add(new PlayRoadBuildingCommand(playerIndex,location,edge,free,gameid));
                    break;
                }
                case"PlayMonumentCommand":
                {
                    int playerindex=jsonObject.getInt("playerindex");
                    int gameID=jsonObject.getInt("gameID");
                    commandsloadedfromdb.add(new PlayMonumentCommand(playerindex,gameID));
                    break;
                }
                case "PlayMonopolyCommand":
                {
                    int playerindex=jsonObject.getInt("playerindex");
                    String resource=jsonObject.getString("resource");
                    int gameID=jsonObject.getInt("gameID");
                    commandsloadedfromdb.add(new PlayMonopolyCommand(playerindex,resource,gameID));
                    break;
                }
                case "OfferTradeCommand":
                {
                    int gameid=jsonObject.getInt("gameid");
                    int playerIndex=jsonObject.getInt("playerIndex");
                    ResourceList offer=new ResourceList(jsonObject.getInt("brick"),jsonObject.getInt("ore"),jsonObject.getInt("sheep"),jsonObject.getInt("wheat"), jsonObject.getInt("wood"));
                    int receiver=jsonObject.getInt("receiver");
                    commandsloadedfromdb.add(new OfferTradeCommand(gameid,playerIndex,offer,receiver));
                    break;
                }
                case "MaritimeTradeCommand":
                {
                    String getResource=jsonObject.getString("getResource");
                    String giveResource=jsonObject.getString("giveResource");
                    int playerIndex_NOT_ID=jsonObject.getInt("playerIndex_NOT_ID");
                    int ratio=jsonObject.getInt("ratio");
                    int gameID=jsonObject.getInt("gameID");
                    commandsloadedfromdb.add(new MaritimeTradeCommand(getResource,giveResource,playerIndex_NOT_ID,ratio,gameID));
                    break;
                }
                case "FinishTurnCommand":
                {
                    int playerIndex=jsonObject.getInt("playerIndex");
                    int gameid=jsonObject.getInt("gameid");
                    commandsloadedfromdb.add(new FinishTurnCommand(playerIndex,gameid));
                    break;
                }
                case "DiscardCardsCommand":
                {
                    int playerIndex=jsonObject.getInt("playerIndex");
                    ResourceList cardsToDiscard=new ResourceList(jsonObject.getInt("brick"),jsonObject.getInt("ore"),jsonObject.getInt("sheep"),jsonObject.getInt("wheat"), jsonObject.getInt("wood"));
                    int gameid=jsonObject.getInt("gameid");
                    commandsloadedfromdb.add(new DiscardCardsCommand(playerIndex,cardsToDiscard,gameid));
                    break;
                }
                case "BuyDevCardCommand":
                {
                    int playerIndex=jsonObject.getInt("playerIndex");
                    int gameid=jsonObject.getInt("gameid");
                    commandsloadedfromdb.add(new BuyDevCardCommand(playerIndex,gameid));
                    break;
                }
                case"BuildSettlementCommand":
                {
                    int playerIndex=jsonObject.getInt("playerIndex");
                    HexLocation location=new HexLocation(jsonObject.getInt("x"),jsonObject.getInt("y"));
                    VertexLocation vertex=new VertexLocation(location,convertToVertexDirection(jsonObject.getString("dir")));
                    boolean free=jsonObject.getBoolean("free");
                    int gameid=jsonObject.getInt("gameid");
                    commandsloadedfromdb.add(new BuildSettlementCommand(playerIndex,location,vertex,free,gameid));
                    break;
                }
                case "BuildRoadCommand":
                {
                    int playerIndex=jsonObject.getInt("playerIndex");
                    HexLocation location=new HexLocation(jsonObject.getInt("x"), jsonObject.getInt("y"));
                    EdgeLocation edge=new EdgeLocation(location, getDirectionFromString(jsonObject.getString("edge")));
                    boolean free=jsonObject.getBoolean("free");
                    int gameid=jsonObject.getInt("gameid");
                    commandsloadedfromdb.add(new BuildRoadCommand(playerIndex,location,edge,free,gameid));
                    break;
                }
                case"BuildCityCommand":
                {
                    int playerIndex=jsonObject.getInt("playerIndex");
                    HexLocation location=new HexLocation(jsonObject.getInt("x"),jsonObject.getInt("y"));
                    VertexLocation vertex=new VertexLocation(location,convertToVertexDirection(jsonObject.getString("vertex")));
                    int gameid=jsonObject.getInt("gameid");
                    commandsloadedfromdb.add(new BuildCityCommand(playerIndex,location,vertex,gameid));
                    break;
                }
                case"AcceptTradeCommand":
                {
                    int gameid=jsonObject.getInt("gameid");
                    int playerindex=jsonObject.getInt("playerIndex");
                    boolean willaccept=jsonObject.getBoolean("willAccept");
                    commandsloadedfromdb.add(new AcceptTradeCommand(gameid,playerindex,willaccept));
                    break;
                }
            }
        }

        return commandsloadedfromdb;
	}
	@Override
	public CatanGame getGameModel(int gameid) {
		// TODO Auto-generated method stub
		return null;
	}

    @Override
    public void createnewGameFile(int gameid)
    {
        System.out.println("I create a new file with id "+gameid);
      File newfile=new File("game"+gameid+".txt");
        try {
            FileWriter db=new FileWriter(newfile);
            db.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }
        File commandfile=new File("commands"+gameid+".txt");
        try {
            FileWriter db=new FileWriter(commandfile);
            db.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void clearInfo(int gameid) {

        File newreplacement=new File("commands"+gameid+".txt");
        try
        {
           FileWriter db=new FileWriter(newreplacement);
           // db.write("{");
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    private VertexDirection convertToVertexDirection(String direction)
    {
        switch (direction)
        {
            case "W":
                return VertexDirection.West;
            case "NW":
                return VertexDirection.NorthWest;
            case "NE":
                return VertexDirection.NorthEast;
            case "E":
                return VertexDirection.East;
            case "SE":
                return VertexDirection.SouthEast;
            case "SW":
                return VertexDirection.SouthWest;
            default:
                break;
            //assert false;
        }
        return null;
    }

    @Override
    public void loadInfo(int gameid) throws IOException, JSONException {
        CatanGame gametowrite=ServerFacade.getInstance().getGameByID(gameid);
        File newfile=new File("game"+gameid+".txt");
        FileWriter mywriter=new FileWriter(newfile);
        System.out.println("I LOAD THIS GAME WHIHC IS THIS who has an id of this "+gameid+gametowrite.getGameModel(gameid).toString());
        mywriter.write(gametowrite.getGameModel(gameid).toString());
        mywriter.flush();
    }

    @Override
    public ArrayList<CatanGame> getCommandsList() {
        return null;
    }

    @Override
    public void getGameModel() {

    }

    private EdgeDirection getDirectionFromString(String direction)
    {
        //System.out.println("the direction is: " + direction);
        switch (direction)
        {
            case "NW":
                return EdgeDirection.NorthWest;
            case "N":
                return EdgeDirection.North;
            case "NE":
                return EdgeDirection.NorthEast;
            case "SW":
                return EdgeDirection.SouthWest;
            case "S":
                return EdgeDirection.South;
            case "SE":
                return EdgeDirection.SouthEast;
            default:
                //System.out.println("Something is screwed up with the direction");
                //assert false;
                break;
        }
        return null;
    }
    @Override
    public Object getGameList() {
        return "Not yet implemented";
    }
}
