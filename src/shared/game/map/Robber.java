package shared.game.map;

import shared.locations.HexLocation;

/**
 * @author Alex
 * Robber: Class to represent the Robber, which moves about
 * the map when 7s are rolled. The Robber then proceeds to 
 * purloin large sums of money (well, goods) from the selected player. 
 */
public class Robber 
{
	/**
	 * The singleton instance of the Robber class.
	 */

	/**
	 * location: Represents the Robber's current location
	 * on the playing board. (i.e., which hex s/he is on)
	 */
	private HexLocation location = null;

	/**
	 * Private constructor in order to ensure that we only have
	 * one instance of Robber.
	 */
	public Robber()
	{

	}

	/**
	 * GetSingleton function!
	 * @return the singleton instance of the Robber class.
     */




	/**
	 * robPlayer: Robs the player at the given index.
	 * @param playerToRob: the player to rob.
	 * @pre The canBeRobbed() function for the player must return true.
	 * 						canPlaceRobber must also be true at the particular location of the player.
	 * 						Otherwise, the player cannot be robbed.
	 * @post The player will be deficient of a particular resource.
	 * @exception: Throws exception if the index is invalid.
	 */
	public void robPlayer(Index playerToRob)
	{
		
	}
	
	/**
	 * canPlaceRobber: Determines whether or not the
	 * robber can be placed at the given HexLocation.
	 * @param loc: given hex location, can't be null, can't place 
	 * if robber already exists on said tile. 
	 * 
	 * @pre loc is not null.
	 * @post true if you can place robber there; false otherwise.
	 * @exception: throws if loc is not found.
	 */
	boolean canPlaceRobber(HexLocation loc)
	{
		return false;
	}

	/**
	 * Clears the Robber so everything is reset!
	 */
	public void clear()
	{
		location = null;
	}

	/**
	 * Getters and Setters follow:
	 */
	public HexLocation getLocation() 
	{
		return location;
	}

	public void setLocation(HexLocation location) 
	{
		if (location == null)
		{
			location = new HexLocation(-1, 0);
		}
		this.location = location;
	}
	
	
}
