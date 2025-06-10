package server.websocket;

import com.google.gson.Gson;
import dataaccess.mysql.MySqlAuthDAO;
import dataaccess.mysql.MySqlGameDAO;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import server.ObjectEncoderDecoder;
import server.exceptions.InvalidAuthTokenException;
import server.exceptions.InvalidInputException;
import serverfacade.ResponseException;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;

@WebSocket
public class WebsocketHandler {

    ObjectEncoderDecoder objectEncoderDecoder = new ObjectEncoderDecoder();
    private final ConnectionManager connections = new ConnectionManager();

    MySqlAuthDAO authDB = new MySqlAuthDAO();
    MySqlGameDAO gameDB = new MySqlGameDAO();
    Gson gson = new Gson();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws ResponseException, IOException {
        UserGameCommand cmd = (UserGameCommand) objectEncoderDecoder.decode(message, UserGameCommand.class);

        var clientAuthToken = cmd.getAuthToken();

        // validate authToken
        if (authDB.getAuthData(clientAuthToken) == null) {
            throw new InvalidAuthTokenException();
        }

        // add connection to connection manager
        connections.add(clientAuthToken, session);

        var clientGameID = cmd.getGameID();

        switch (cmd.getCommandType()) {
            case CONNECT -> {
                connect(session, clientAuthToken, clientGameID);
                broadcast(clientAuthToken, clientGameID);
            }
            case MAKE_MOVE -> {
                makeMove(session, clientAuthToken, clientGameID);
            }
            case LEAVE -> {
                leave(session, clientAuthToken, clientGameID);
            }
            case RESIGN -> {
                resign(session, clientAuthToken, clientGameID);
            }
            default -> {
                throw new InvalidInputException();
            }
        }
    }

    private void connect(Session session, String currentAuthToken, int id) throws IOException {
        try {
            // load game message sent back to the client

            var currentGame = gameDB.getGame(id).game();

            var loadGameMessage = new LoadGameMessage(currentGame);

            var encodedLoadGameMessage = new Gson().toJson(loadGameMessage);

            session.getRemote().sendString(encodedLoadGameMessage);

            // notification sent to all other clients in the game that a player has connected as player or observer
            broadcast(currentAuthToken, id);

        } catch (IOException | ResponseException ex) {
            var errorMessage = gson.toJson(new ErrorMessage(ex.getMessage()));
            session.getRemote().sendString(errorMessage);
        }
    }

    private void makeMove(Session session, String currentAuthToken, int id) {
        // verify the validity of the move
        // update the game with the move made
        // load game message sent back to all clients
        // notification sent to all other clients telling them the move
        // if move results in check, checkmate or stalemate: notification sent to all clients
    }

    private void leave(Session session, String authToken, int id) {
        // game is updated in DB to remove root client
        // notification to all other clients that client left
    }

    private void resign(Session session, String authToken, int id) {
        // server marks game as over (no more moves can be made)
        // game updated in DB
        // notification to all clients informing that client resigned
    }

    private void broadcast(String currentAuthToken, int id) throws ResponseException {
        try {
            var username = authDB.getAuthData(currentAuthToken).username();

            var msg = String.format("user '%s' has joined game %d", username, id);
            System.out.println(msg);

            var notification = new NotificationMessage(msg);

            connections.broadcast(currentAuthToken, notification);
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }
}
