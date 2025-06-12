package ui;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import serverfacade.ResponseException;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
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
    ChessGame currentGame = null;
    ChessGame.TeamColor currentTeamColor = ChessGame.TeamColor.WHITE;
    boolean isEnded = false;

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
                currentGame = loadGameMessage.getGame();
                notificationHandler.printBoard(currentTeamColor, currentGame.getBoard());

                NotificationMessage turnNotification;

                if (loadGameMessage.isEnded()) {
                    isEnded = true;
                    turnNotification = new NotificationMessage("The game has ended.");
                } else {
                    turnNotification = new NotificationMessage(String.format("It is %s's turn", currentGame.getTeamTurn()));
                }
                notificationHandler.notify(turnNotification);
            }
            case ERROR -> {
                // error, invalid command sent to server
                ErrorMessage errorMessage = gson.fromJson(message, ErrorMessage.class);
                notificationHandler.notifyError(errorMessage);
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

    public void connect(String authToken, int id, ChessGame.TeamColor color) throws ResponseException {
        currentTeamColor = color;
        var cmd = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, id);
        sendMessage(cmd);
    }

    public void disconnect(String authToken, int id) throws ResponseException {
        try {
            if (session.isOpen()) {
                var cmd = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, id);
                sendMessage(cmd);
                this.session.close();
            }
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void redrawBoard() {
        notificationHandler.printBoard(currentTeamColor, currentGame.getBoard());
        NotificationMessage turnNotification;
        if (isEnded) {
            turnNotification = new NotificationMessage("The game has ended.");
        } else {
            turnNotification = new NotificationMessage(String.format("It is %s's turn", currentGame.getTeamTurn()));
        }

        notificationHandler.notify(turnNotification);
    }

    public void makeMove(String authToken, int id, ChessMove move) throws ResponseException {
        var cmd = new MakeMoveCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, id, move);
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
