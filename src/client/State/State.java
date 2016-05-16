package client.State;

import shared.game.CatanGame;
import shared.game.map.Hex.Hex;
import shared.game.player.Player;
import shared.locations.EdgeLocation;

public enum State {


	LoginState{
		//player logins or registers 
		
		@Override
		public void login() {

		}

		@Override
		public void register() {

		}


		@Override
		public String getState() {
			return "LOGIN";
		}
	},
	
	GamePlayingState{
		//player is actually playing the game.
		@Override
		public void playSoldierCard() {

		}

		@Override
		public void playRoadBuildingCard() {

		}

		@Override
		public void playMonumentCard() {

		}

		@Override
		public void playMonopolyCard() {

		}

		@Override
		public void buildCity() {

		}

		@Override
		public void buildSettlement() {

		}

		@Override
		public void buildRoad() {

		}

		@Override
		public void rollNumber() {

		}
		
		@Override
		public String getState() {
			return "GAMEPLAYING";
		}

		@Override
		public boolean canBuildRoadPiece(Hex hex, EdgeLocation edge)
		{
			return (CatanGame.singleton.getCurrentPlayer().canBuildRoadPiece(hex, edge));
		}
	},
	SetUpState{
		//First set up round player gets 2 free settlements and roads
		
		@Override
		public void buildSettlement() {

		}

		@Override
		public void buildRoad() {

		}

		@Override
		public String getState() {
			return "SETUP";
		}
	},
	PlayerWaitingState{
		//player waits for other players to join game can add AI player

		@Override
		public void addAI() {

		}

		@Override
		public String getState() {
			return "PLAYERWAITING";
		}
	},
	JoinGameState
	{
		//player can join or re-join games or create game
		@Override
		public void joinGame()
		{

		}
		@Override
		public void createGame()
		{

		}
		
		@Override
		public String getState() {
			return "JOINGAME";
		}
	};
	
	
	//default methods do nothing.
	public void login(){}
	public void joinGame(){}
	public void register(){}
	public void playSoldierCard(){}
	public void playRoadBuildingCard(){}
	public void playMonumentCard(){}
	public void playMonopolyCard(){}
	public void buildCity(){}
	public void buildSettlement(){}
	public void buildRoad(){}
	public void placeRobber(){}
	public void rollNumber(){}
	public void createGame(){}
	public void addAI(){}
	public String getState(){ return null; }
	public boolean canBuildRoadPiece(Hex hex, EdgeLocation edge)
	{
		return false;
	}

}
