package shared.game.map.vertexobject;

import shared.game.map.Index;
import shared.locations.HexLocation;
import shared.locations.VertexLocation;

/**
 * @author Alex
 * City: players can build a city in order to expand their territory
 */
public class City
{
	public City(HexLocation hexLocation, VertexLocation vertexLocation)
	{
		this.hexLocation=hexLocation;
		this.vertexLocation=vertexLocation;
	}

	public VertexLocation getVertexLocation() {
		return vertexLocation;
	}

	public void setVertexLocation(VertexLocation vertexLocation) {
		this.vertexLocation = vertexLocation;
	}

	public HexLocation getHexLocation() {
		return hexLocation;
	}

	public void setHexLocation(HexLocation hexLocation) {
		this.hexLocation = hexLocation;
	}

	private VertexLocation vertexLocation;

	private HexLocation hexLocation;

	/**
	 * owner: Index of the player who owns city
	 */
	private Index owner = null;
	
	/**
	 * Where the city is located on the map
	 */
	private HexLocation location = null;
	
	/**
	 * Direction: string
	 */
	private String direction = "";
	
	/**
	 * Determining whether or not we can place a city.
	 */
	public boolean canPlaceCity()
	{
		return false;
	}

	public HexLocation getLocation()
	{
		return location;
	}

	public void setLocation(HexLocation location)
	{
		this.location = location;
	}
}
