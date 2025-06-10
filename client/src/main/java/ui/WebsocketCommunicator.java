package ui;

import com.google.gson.Gson;
import serverfacade.ResponseException;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebsocketCommunicator extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;

    public WebsocketCommunicator(String url, NotificationHandler notificationHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            // add a message handler to the WS session
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    messageHandler(message);
                }
            });

        } catch (DeploymentException | URISyntaxException | IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void messageHandler(String message) {
        var gson = new Gson();
        ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);

        switch (serverMessage.getServerMessageType()) {
            case LOAD_GAME -> {
                // game sent back
                LoadGameMessage loadGameMessage = gson.fromJson(message, LoadGameMessage.class);
                System.out.println(loadGameMessage.getGame());
            }
            case ERROR -> {
                // error, invalid command sent to server
            }
            case NOTIFICATION -> {
                // notification is broadcast in client
                NotificationMessage notificationMessage = gson.fromJson(message, NotificationMessage.class);
                notificationHandler.notify(notificationMessage);
            }
        }
    }

    // unnecessary for our application, required for endpoint
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connect(String authToken, int id) throws ResponseException {
        var cmd = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, id);
        sendMessage(cmd);
    }

    public void sendMessage(UserGameCommand cmd) throws ResponseException {
        try {
            this.session.getBasicRemote().sendText(new Gson().toJson(cmd));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }
}
