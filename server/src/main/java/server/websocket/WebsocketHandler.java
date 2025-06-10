package server.websocket;

import dataaccess.mysql.MySqlAuthDAO;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import server.ObjectEncoderDecoder;
import server.exceptions.InvalidAuthTokenException;
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

        // validate authToken
        if (authDB.getAuthData(cmd.getAuthToken()) == null) {
            throw new InvalidAuthTokenException();
        }

        // add connection to connection manager
        connections.add(cmd.getAuthToken(), session);

        sendMessageBack(cmd.getAuthToken(), cmd.getGameID());
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
