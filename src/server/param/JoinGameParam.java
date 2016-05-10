package server.param;

public class JoinGameParam extends Param {

	int gameID;
	String color;
	
	public  JoinGameParam(int gameID, String color){
		this.gameID = gameID;
		this.color = color; 
	}
	
	@Override
	public String getRequest() {
		return "{id:\"" + gameID + "\", "+
				"color: \"" + color + "\"}";
	}

	@Override
	public String getRequestType() {
		return "POST";
	}
	

}