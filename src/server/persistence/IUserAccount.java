package server.persistence;

import java.util.List;

import server.database.DatabaseException;
import shared.game.player.Player;


import shared.game.player.Player;

/**
 * Created by williamjones on 6/7/16.
 * IUserAccount interface.
 */
public interface IUserAccount
{
	Player validateUser(Player player);

	void addUser(Player user) throws DatabaseException;

	List<Player> getAllUsers();

	void setColor(Player user) throws DatabaseException;

	boolean isUserInGame(Player user);

}
