package ui;

import com.google.gson.Gson;
import serverfacade.ResponseException;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebsocketCommunicator extends Endpoint {

    Session session;

    public WebsocketCommunicator(String url) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            // add a message handler to the WS session
            this.session.addMessageHandler((MessageHandler.Whole<String>) message -> {
                ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);

                switch (serverMessage.getServerMessageType()) {
                    case LOAD_GAME -> {
                        // game sent back
                    }
                    case ERROR -> {
                        // error, invalid command sent to server
                    }
                    case NOTIFICATION -> {
                        // notification to broadcast in client, replace later
                        System.out.println(serverMessage);
                    }
                }
            });

        } catch (DeploymentException | URISyntaxException | IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void sendMessage(UserGameCommand cmd) throws ResponseException {
        try {
            this.session.getBasicRemote().sendText(new Gson().toJson(cmd));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }
}
