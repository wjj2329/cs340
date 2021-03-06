package server.ourserver.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONException;
import org.json.JSONObject;
import server.ourserver.ServerFacade;
import shared.locations.HexLocation;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Scanner;

/**
 * Created by williamjones on 5/26/16.
 /**
 * type name of move being executed
 *  playerIndex the player's position in the game's turn order
 *
 * @pre it is your turn, the client model's status is 'Playing'
 *
 * @post the cards in your new dev card hand have been transferred to your
 * 						old dev hand, it is the next player's turn.
 *
 *
 */

public class MovesFinishTurnHandler implements HttpHandler
{
    @Override
    public void handle(HttpExchange exchange) throws IOException
    {

        //System.out.println("I COME HERE TO HANDLE ENDING A TURN");
        String cookie = exchange.getRequestHeaders().getFirst("Cookie");
        int gameID = getGameIDfromCookie(cookie);
        int playerindex=-50;
        JSONObject data = null;
		exchange.getResponseHeaders().add("Content-type", "text/html");
        try
        {
            Scanner s = new Scanner(exchange.getRequestBody()).useDelimiter("\\A");
            String result = s.hasNext() ? s.next() : "";
            data = new JSONObject(result);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
			String response = "You gotta give me something to work with to finish a turn.";
			exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
			exchange.getResponseBody().write(response.getBytes());
			exchange.close();
        }
        try
        {
            playerindex=data.getInt("playerIndex");
        } catch (JSONException e) {
            e.printStackTrace();
			String response = "So you are missing some info to finish turn.";
			exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
			exchange.getResponseBody().write(response.getBytes());
			exchange.close();
        }

        try {
            ServerFacade.getInstance().finishTurn(playerindex,gameID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String response = "You managed to finish the turn. Congrats.";
        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
        exchange.getResponseBody().write(response.getBytes());
        //System.out.println("I SUCCESSFULLY END THE TURN");
        exchange.close();

    }
    public int getGameIDfromCookie(String cookie)
    {
        return Integer.parseInt(cookie.substring(cookie.indexOf("game=")+5, cookie.length()));
    }
}
