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
import websocket.messages.NotificationType;

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
            var currentGame = gameDB.getGame(id);
            var username = authDB.getAuthData(currentAuthToken).username();

            // determine type of notification
            NotificationType notificationType;
            if (currentGame.whiteUsername().equals(username)) {
                notificationType = NotificationType.PLAYER_JOIN_WHITE;
            } else if (currentGame.blackUsername().equals(username)) {
                notificationType = NotificationType.PLAYER_JOIN_BLACK;
            } else {
                notificationType = NotificationType.OBSERVER_JOIN;
            }

            // send a LOAD_GAME message back to the client
            var loadGameMessage = new LoadGameMessage(currentGame.game());
            var encodedLoadGameMessage = objectEncoderDecoder.encode(loadGameMessage);

            var conn = new Connection(currentAuthToken, session);
            conn.send(encodedLoadGameMessage);

            // notification sent to all other clients in the game that a player has connected as player or observer
            notifyAll(currentAuthToken, id, notificationType);

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

    private void notifyAll(String currentAuthToken, int id,
                           NotificationType notificationType) throws ResponseException {
        try {
            var username = authDB.getAuthData(currentAuthToken).username();
            String msg = "";

            switch (notificationType) {
                case PLAYER_JOIN_WHITE -> msg = String.format("%s has joined the game as white!", username);
                case PLAYER_JOIN_BLACK -> msg = String.format("%s has joined the game as black!", username);
                case OBSERVER_JOIN -> msg = String.format("%s has joined the game as an observer!", username);
                case MOVE_MADE -> msg = "A move has been made";
                case LEAVE_GAME -> msg = String.format("%s has left the game", username);
                case RESIGN -> msg = String.format("%s has resigned", username);
                case CHECK -> msg = "Check!";
                case CHECKMATE -> msg = "Checkmate!";
                case STALEMATE -> msg = "Stalemate!";
                default -> msg = "Error occurred";
            }

            System.out.println(msg);
            var notification = objectEncoderDecoder.encode(new NotificationMessage(msg));
            gameConnections.get(id).broadcast(currentAuthToken, notification);

        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }
}
