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
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class WebsocketHandler {

    ObjectEncoderDecoder objectEncoderDecoder = new ObjectEncoderDecoder();
    private final ConcurrentHashMap<Integer, ConnectionManager> gameConnections = new ConcurrentHashMap<>();

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

        var clientGameID = cmd.getGameID();

        // If connections associated with a game already exist, then add the current session
        if (gameConnections.containsKey(clientGameID)) {
            gameConnections.get(clientGameID).add(clientAuthToken, session);
        }
        // Otherwise, add a new ID to the gameConnections
        else {
            gameConnections.put(clientGameID, new ConnectionManager());
            gameConnections.get(clientGameID).add(clientAuthToken, session);
        }

        switch (cmd.getCommandType()) {
            case CONNECT -> {
                connect(session, clientAuthToken, clientGameID);
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
            // get the game from the DB
            var currentGame = gameDB.getGame(id).game();

            // send a LOAD_GAME message back to the client
            var loadGameMessage = new LoadGameMessage(currentGame);
            var encodedLoadGameMessage = objectEncoderDecoder.encode(loadGameMessage);

            var conn = new Connection(currentAuthToken, session);
            conn.send(encodedLoadGameMessage);

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

            var notification = objectEncoderDecoder.encode(new NotificationMessage(msg));
            gameConnections.get(id).broadcast(currentAuthToken, notification);
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }
}
