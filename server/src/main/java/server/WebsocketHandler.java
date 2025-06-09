package server;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;

import java.io.IOException;

@WebSocket
public class WebsocketHandler {

    ObjectEncoderDecoder objectEncoderDecoder = new ObjectEncoderDecoder();
    Session session;

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        this.session = session;
        UserGameCommand cmd = (UserGameCommand) objectEncoderDecoder.decode(message, UserGameCommand.class);

        sendMessageBack(cmd.getAuthToken(), cmd.getGameID());
    }

    private void sendMessageBack(String currentAuthToken, int id) {
        try {
            session.getRemote().sendString(String.format("your authToken is %s, gameID is %d", currentAuthToken, id));
        } catch (IOException e) {
            System.out.println("failed to send message back");
        }
    }
}
