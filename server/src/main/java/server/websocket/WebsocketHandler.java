package server.websocket;

import dataaccess.mysql.MySqlAuthDAO;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import server.ObjectEncoderDecoder;
import server.exceptions.InvalidAuthTokenException;
import server.exceptions.InvalidInputException;
import websocket.commands.UserGameCommand;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

@WebSocket
public class WebsocketHandler {

    ObjectEncoderDecoder objectEncoderDecoder = new ObjectEncoderDecoder();
    private final ConnectionManager connections = new ConnectionManager();

    MySqlAuthDAO authDB = new MySqlAuthDAO();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
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
                sendMessageBack(clientAuthToken, clientGameID);
            }
            case MAKE_MOVE -> {
                makeMove(clientAuthToken, clientGameID);
            }
            case LEAVE -> {
                leave(clientAuthToken, clientGameID);
            }
            case RESIGN -> {
                resign(clientAuthToken, clientGameID);
            }
            default -> {
                throw new InvalidInputException();
            }
        }
    }

    private void connect(String currentAuthToken, int id) {
        // load game message sent back to the client
        // notification sent to all other clients in the game that a player has connected as player or observer
    }

    private void makeMove(String currentAuthToken, int id) {
        // verify the validity of the move
        // update the game with the move made
        // load game message sent back to all clients
        // notification sent to all other clients telling them the move
        // if move results in check, checkmate or stalemate: notification sent to all clients
    }

    private void leave(String authToken, int id) {
        // game is updated in DB to remove root client
        // notification to all other clients that client left
    }

    private void resign(String authToken, int id) {
        // server marks game as over (no more moves can be made)
        // game updated in DB
        // notification to all clients informing that client resigned
    }

    private void sendMessageBack(String currentAuthToken, int id) {
        try {
            var msg = String.format("user with authToken %s has joined game %d", currentAuthToken, id);
            System.out.println(msg);

            var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, msg);

            connections.broadcast(currentAuthToken, notification);
        } catch (IOException e) {
            System.out.println("failed to send message back");
        }
    }
}
