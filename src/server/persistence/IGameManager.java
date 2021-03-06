package server.persistence;

import org.json.JSONException;
import shared.game.CatanGame;

import shared.game.CatanGame;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import server.ourserver.commands.ICommand;
import shared.game.CatanGame;

import javax.activation.CommandObject;

/**
 * Created by williamjones on 6/7/16.
 * IGameManager class
 * The functions that are currently void will probably be changed.
 */
public interface IGameManager {

	public void clearInfo(int gameid);

	public void loadInfo(int gameid) throws IOException, JSONException;

	void storeGameModel(int gameid);

	void addCommand(ICommand commandObject, int gameId) throws JSONException, IOException;

	ArrayList<CatanGame> getCommandsList();

	void getGameModel();

	Object getGameList();

	ArrayList<ICommand> getCommands(int gameId) throws JSONException, IOException;

	CatanGame getGameModel(int gameId);

	void createnewGameFile(int gameid);
}
