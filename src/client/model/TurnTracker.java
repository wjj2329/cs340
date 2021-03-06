package client.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import shared.game.map.Index;
import shared.game.player.Player;

/**
 * A class to keep track of turns
 */
@SuppressWarnings("serial")
public class TurnTracker implements Serializable
{

	/**
	 * The index of the player whom the current turn is on.
	 */
	private Index currentTurn;
	/**
	 * The current status of the game.
	 */
	private TurnStatus status;
	/**
	 * The index of the player who currently holds the largest road.
	 */
	private Index longestRoad;
	/**
	 * The index of the player who currently holds the largest army.
	 */
	private Index largestArmy;
	 
	public TurnTracker(TurnStatus status, Index currentTurn, Index longestRoad, Index largestArmy)
	{
		this.status = status;
		this.currentTurn = currentTurn;
		this.longestRoad = longestRoad;
		this.largestArmy = largestArmy;
	}
	/**
	  * A function that updates at the start of the turn the player with 
	  * the largest Army
	  * @pre There must be a player that has at least 3 army knights before this card can be given.
	  * @post Player is updates as having the Largest Army Card. 
	  * need to fix this.
	  */
	public Player updateUserWithLargestArmy(Map<Index, Player> allplayers)
	{
		int currentlargest = -1;
		for (Map.Entry<Index, Player> entry : allplayers.entrySet())
		{
			if(entry.getValue().getArmySize() >= 3)
			{
				if(currentlargest == -1 || 
						entry.getValue().getArmySize() > allplayers.get(new Index(currentlargest)).getArmySize())
				{
					//currentlargest = i;
					largestArmy.setNumber(entry.getValue().getArmySize());
				}
			}
		}
		
		return allplayers.get(currentlargest);
	}
	/**
	  * A function that updates at the start of the turn the player with 
	  * the largest RoadPiece
	  *  * @pre There must be a player that has at least 3 continuous road pieces before this card can be given.
	  * @post Player is updates as having the Largest RoadPiece Card.
	 *
	 * we will need to fix this. lo siento. fix the new index thing as well
	  */
	public Player updateUserWithLongestRoad(Map<Index, Player> allplayers)
	{
		int currentlargest = -1;
		//for (int i = 0; i < allplayers.size(); i++)
		for (Map.Entry<Index, Player> entry : allplayers.entrySet())
		{
			if(entry.getValue().getRoadSize() >= 3)
			{
				if(currentlargest == -1 || 
						entry.getValue().getRoadSize() > allplayers.get(new Index(currentlargest)).getRoadSize())
				{
					//currentlargest = i;
					longestRoad.setNumber(entry.getValue().getRoadSize());
				}
			}
		}
		
		return allplayers.get(currentlargest);
	}
	/**
	 * @return the currentTurn
	 */
	public Index getCurrentTurn()
	{
		return currentTurn;
	}
	/**
	 * @param currentTurn the currentTurn to set
	 */
	public void setCurrentTurn(Index currentTurn, Map<Index, Player> players)
	{
		this.currentTurn = currentTurn;
		updateUserWithLargestArmy(players);
		updateUserWithLongestRoad(players);
	}
	/**
	 * @return the status
	 */
	public TurnStatus getStatus()
	{
		return status;
	}
	/**
	 * @param status the status to set
	 *               It converts it from string to TurnStatus
	 */

	public void setStatus(TurnStatus status)
	{
		this.status = status;
	}

	public void setStatusFromString(String status)
	{
		switch (status.toLowerCase())
		{
			case "rolling":
				this.status = TurnStatus.ROLLING;
				break;
			case "robbing":
				this.status = TurnStatus.ROBBING;
				break;
			case "playing":
				this.status = TurnStatus.PLAYING;
				break;
			case "discarding":
				this.status = TurnStatus.DISCARDING;
				break;
			case "firstround":
				this.status = TurnStatus.FIRSTROUND;
				break;
			case "secondround":
				this.status = TurnStatus.SECONDROUND;
				break;
		}
	}
	/**
	 * @return the longestRoad
	 */
	public Index getLongestRoad()
	{
		return longestRoad;
	}
	/**
	 * @param longestRoad the longestRoad to set
	 */
	public void setLongestRoad(Index longestRoad)
	{
		this.longestRoad = longestRoad;
	}
	/**
	 * @return the largestArmy
	 */
	public Index getLargestArmy()
	{
		return largestArmy;
	}
	/**
	 * @param largestArmy the largestArmy to set
	 */
	public void setLargestArmy(Index largestArmy)
	{
		this.largestArmy = largestArmy;
	}
	

}
