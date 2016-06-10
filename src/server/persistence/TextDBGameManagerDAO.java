package server.persistence;

import com.google.gson.Gson;

import com.sun.corba.se.impl.orbutil.ObjectWriter;
import server.ourserver.ServerFacade;
import server.ourserver.commands.ICommand;
import shared.game.CatanGame;

import org.json.JSONException;
import org.json.JSONObject;
import shared.game.CatanGame;

import javax.activation.CommandObject;
import java.io.*;
import java.sql.SQLData;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by williamjones on 6/7/16.
 */
public class TextDBGameManagerDAO implements IGameManager
{
    private int gameid=-1000;
    private File commands=new File("commands"+gameid+".txt");
    private File game=new File("commands"+gameid+".txt");
    private FileWriter db=new FileWriter(commands);
    private FileWriter dbg=new FileWriter(game);

    public int getGameid() {
        return gameid;
    }

    public void setGameid(int gameid) {

        this.gameid = gameid;
        commands=new File("commands"+gameid+".txt");
        try {
            db=new FileWriter(commands);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public TextDBGameManagerDAO() throws IOException
    {
            db.write("{");
    }
    
	@Override
	public void storeGameModel() {
		// TODO Auto-generated method stub
		
	}
    @Override
    public void addCommand(ICommand commandObject) throws JSONException, IOException
    {
        db.write((commandObject.toString()));
        db.flush();
    }
    public static int commandNumber=-1;
	@Override
	public ArrayList<ICommand> getCommands() {
        ArrayList<ICommand> commandsloadedfromdb=new ArrayList<>();
        Scanner myscanner=null;
        try {
             myscanner=new Scanner(commands);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
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
        return commandsloadedfromdb;
	}
	@Override
	public CatanGame getGameModel(int gameid) {
		// TODO Auto-generated method stub
		return null;
	}


    @Override
    public void clearInfo() {
        commands=new File("game"+gameid+".txt");
        try
        {
            db=new FileWriter(commands);
            db.write("{");
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void loadInfo()
    {
        if(gameid!=-1000) {
            try
            {
                dbg.write(ServerFacade.getInstance().getGameModel(gameid).toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public ArrayList<CatanGame> getCommandsList() {
        return null;
    }

    @Override
    public void getGameModel() {

    }

    @Override
    public Object getGameList() {
        return "Not yet implemented";
    }
}
