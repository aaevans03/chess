package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.InvalidMoveException;
import dataaccess.mysql.MySqlAuthDAO;
import dataaccess.mysql.MySqlGameDAO;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import server.ObjectEncoderDecoder;
import server.exceptions.InvalidAuthTokenException;
import server.exceptions.InvalidInputException;
import serverfacade.ResponseException;
import websocket.commands.MakeMoveCommand;
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
            case CONNECT -> connect(session, clientAuthToken, clientGameID);
            case MAKE_MOVE -> {
                MakeMoveCommand moveCommand =
                        (MakeMoveCommand) objectEncoderDecoder.decode(message, MakeMoveCommand.class);
                makeMove(session, clientAuthToken, clientGameID, moveCommand.getMove());
            }
            case LEAVE -> leave(session, clientAuthToken, clientGameID);
            case RESIGN -> resign(session, clientAuthToken, clientGameID);
            default -> throw new InvalidInputException();
        }
    }

    private void connect(Session session, String currentAuthToken, int id) throws ResponseException {
        try {
            // get the game from the DB
            var currentGame = gameDB.getGame(id);
            var username = authDB.getAuthData(currentAuthToken).username();

            // determine type of notification
            NotificationType notificationType = getConnectNotificationType(currentGame, username);

            // send a LOAD_GAME message back to the client
            boolean isEnded = (gameDB.getGame(id).game().getTeamTurn() == null);

            var loadGameMessage = new LoadGameMessage(currentGame.game(), isEnded);
            var encodedLoadGameMessage = objectEncoderDecoder.encode(loadGameMessage);

            notifyClient(session, encodedLoadGameMessage);

            // notification sent to all other clients in the game that a player has connected as player or observer
            notifyAllClients(currentAuthToken, id, notificationType, "");

        } catch (ResponseException ex) {
            sendError(session, ex.getMessage());
        }
    }

    private NotificationType getConnectNotificationType(GameData currentGame, String username) {
        NotificationType notificationType;
        var whiteUsername = currentGame.whiteUsername();
        var blackUsername = currentGame.blackUsername();

        if (whiteUsername != null && whiteUsername.equals(username)) {
            notificationType = NotificationType.PLAYER_JOIN_WHITE;
        } else if (blackUsername != null && blackUsername.equals(username)) {
            notificationType = NotificationType.PLAYER_JOIN_BLACK;
        } else {
            notificationType = NotificationType.OBSERVER_JOIN;
        }
        return notificationType;
    }

    private void makeMove(Session session, String currentAuthToken, int id, ChessMove chessMove) throws IOException, ResponseException {
        // check if game is being played
        if ((gameDB.getGame(id).game().getTeamTurn() == null)) {
            sendError(session, "Move cannot be made, game has ended.");
            return;
        }

        // check if it's the current user's turn
        var currentGameData = gameDB.getGame(id);
        var currentGame = currentGameData.game();

        var currentUserColor = getCurrentUserColor(currentAuthToken, currentGameData);

        if (currentUserColor == null) {
            sendError(session, "Move cannot be made, you are not joined in the game.");
            return;
        } else if (!currentUserColor.equals(currentGame.getTeamTurn())) {
            sendError(session, "Move cannot be made, wait for your next turn.");
            return;
        }

        // check if they can move the requested piece
        var currentChessBoard = currentGame.getBoard();

        if (chessMove == null) {
            sendError(session, "No move provided.");
            return;
        }

        var requestedPiece = currentChessBoard.getPiece(chessMove.getStartPosition());

        if (requestedPiece == null) {
            sendError(session, "Move cannot be made, no piece in that space.");
            return;
        }

        var teamColor = requestedPiece.getTeamColor();

        if (teamColor == null || teamColor != currentUserColor) {
            sendError(session, "Move cannot be made, the requested piece is not your team color's.");
            return;
        }

        // verify the validity of the move
        var validMoves = currentGame.validMoves(chessMove.getStartPosition());
        boolean promotionMove = false;

        for (var move : validMoves) {

            if (move.getPromotionPiece() != null && move.getEndPosition().equals(chessMove.getEndPosition())) {
                promotionMove = true;
            }

            if (move.equals(chessMove)) {
                try {
                    var pieceType = currentChessBoard.getPiece(chessMove.getStartPosition()).getPieceType();
                    // if move is valid, update the game with the move made
                    currentGame.makeMove(chessMove);
                    gameDB.updateGame(id, null, null, currentGame);

                    // send a LOAD_GAME message back to all clients
                    var loadGameMessage = new LoadGameMessage(currentGame, false);
                    var encodedLoadGameMessage = objectEncoderDecoder.encode(loadGameMessage);
                    gameConnections.get(id).broadcast("", encodedLoadGameMessage);

                    // send a MOVE_MADE message back to all other clients
                    sendMoveMadeMessage(currentAuthToken, id, chessMove, pieceType);

                    ChessGame.TeamColor otherUserColor = (currentUserColor.equals(ChessGame.TeamColor.WHITE) ?
                            ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE);

                    String otherUsername = getPlayerUsername(otherUserColor, currentGameData);

                    if (currentGame.isInStalemate(otherUserColor)) {
                        notifyAllClients("", id, NotificationType.STALEMATE, null);
                        endGame(id);
                    } else if (currentGame.isInCheckmate(otherUserColor)) {
                        notifyAllClients("", id, NotificationType.CHECKMATE, otherUsername);
                        endGame(id);
                    } else if (currentGame.isInCheck(otherUserColor)) {
                        notifyAllClients("", id, NotificationType.CHECK, otherUsername);
                    }

                    return;
                } catch (InvalidMoveException e) {
                    sendError(session, "Move cannot be made, please try again.");
                }
            }
        }

        if (promotionMove) {
            sendError(session, "This pawn needs to be promoted to either a rook, knight, bishop or queen," +
                    "\n          please specify which one.");
        } else {
            sendError(session, "Invalid move entered, please try again.");
        }
    }

    private void sendMoveMadeMessage(String currentAuthToken, int id, ChessMove chessMove,
                                     ChessPiece.PieceType pieceType) throws ResponseException {
        // determine what spaces they are based on chess board notation
        char initialCol = (char) (chessMove.getStartPosition().getColumn() - 1 + 'a');
        int initialRow = chessMove.getStartPosition().getRow();

        char finalCol = (char) (chessMove.getEndPosition().getColumn() - 1 + 'a');
        int finalRow = chessMove.getEndPosition().getRow();

        String moveMessage = pieceType.toString().toLowerCase() + " at " + initialCol + initialRow + " to " + finalCol + finalRow;

        notifyAllClients(currentAuthToken, id, NotificationType.MOVE_MADE, moveMessage);
    }

    /**
     * Get a user's color given their authToken in a game
     *
     * @param currentAuthToken User to get the color of
     * @param currentGame      Chess game to pull color out of
     * @return WHITE, BLACK, or null
     */
    private ChessGame.TeamColor getCurrentUserColor(String currentAuthToken, GameData currentGame) {
        var username = authDB.getAuthData(currentAuthToken).username();

        var whiteUsername = currentGame.whiteUsername();
        var blackUsername = currentGame.blackUsername();

        if (whiteUsername != null && whiteUsername.equals(username)) {
            return ChessGame.TeamColor.WHITE;
        } else if (blackUsername != null && blackUsername.equals(username)) {
            return ChessGame.TeamColor.BLACK;
        } else {
            return null;
        }
    }

    /**
     * Given a teamColor and gameData, pull the requested username out
     *
     * @param teamColor Color to pull username out of
     * @param gameData  Game data to pull username out of
     * @return Username, or team color name if username is null
     */
    private String getPlayerUsername(ChessGame.TeamColor teamColor, GameData gameData) {
        switch (teamColor) {
            case WHITE -> {
                if (gameData.whiteUsername() != null) {
                    return gameData.whiteUsername();
                } else {
                    return teamColor.toString();
                }
            }
            case BLACK -> {
                if (gameData.blackUsername() != null) {
                    return gameData.blackUsername();
                } else {
                    return teamColor.toString();
                }
            }
            default -> {
                return null;
            }
        }
    }

    private void leave(Session session, String currentAuthToken, int id) throws ResponseException {
        try {
            // game is updated in DB to remove root client
            var currentGame = gameDB.getGame(id);
            var username = authDB.getAuthData(currentAuthToken).username();

            var whiteUsername = currentGame.whiteUsername();
            var blackUsername = currentGame.blackUsername();

            // determine which username to remove
            if (whiteUsername != null && whiteUsername.equals(username)) {
                gameDB.updateGame(id, "SET NULL", ChessGame.TeamColor.WHITE, null);
            } else if (blackUsername != null && blackUsername.equals(username)) {
                gameDB.updateGame(id, "SET NULL", ChessGame.TeamColor.BLACK, null);
            }

            // notification to all other clients that client left
            notifyAllClients(currentAuthToken, id, NotificationType.LEAVE_GAME, "");

        } catch (ResponseException ex) {
            sendError(session, ex.getMessage());
        }
    }

    private void resign(Session session, String currentAuthToken, int id) throws ResponseException {
        if (gameDB.getGame(id).game().getTeamTurn() == null) {
            sendError(session, "Can't resign, game has already ended.");
        } else {
            // check if the client is joined in the game
            var currentGameData = gameDB.getGame(id);

            if (getCurrentUserColor(currentAuthToken, currentGameData) == null) {
                sendError(session, "Move cannot be made, you are not joined in the game.");
                return;
            }

            // end the game
            endGame(id);

            // notification to all clients informing that client resigned
            notifyAllClients(currentAuthToken, id, NotificationType.RESIGN, "");

            var msg = objectEncoderDecoder.encode(new NotificationMessage("You resigned, game over!"));
            notifyClient(session, msg);
        }
    }

    private void endGame(int id) {
        var curGameData = gameDB.getGame(id);

        var curGame = curGameData.game();
        curGame.setTeamTurn(null);

        gameDB.updateGame(id, null, null, curGame);
    }

    private void notifyAllClients(String currentAuthToken, int id, NotificationType notificationType,
                                  String customMsg) throws ResponseException {
        try {
            var username = "";
            if (!currentAuthToken.isEmpty()) {
                username = authDB.getAuthData(currentAuthToken).username();
            }
            String msg;

            switch (notificationType) {
                case PLAYER_JOIN_WHITE -> msg = String.format("%s has joined the game as white!", username);
                case PLAYER_JOIN_BLACK -> msg = String.format("%s has joined the game as black!", username);
                case OBSERVER_JOIN -> msg = String.format("%s has joined the game as an observer!", username);
                case MOVE_MADE -> msg = String.format("%s moved the %s.", username, customMsg);
                case LEAVE_GAME -> msg = String.format("%s has left the game", username);
                case RESIGN -> msg = String.format("%s resigned, game over!", username);
                case CHECK -> msg = String.format("%s is in check!", customMsg);
                case CHECKMATE -> msg = String.format("%s is in checkmate, game over!", customMsg);
                case STALEMATE -> msg = "Stalemate, game over!";
                default -> msg = "Error occurred";
            }

            var notification = objectEncoderDecoder.encode(new NotificationMessage(msg));
            gameConnections.get(id).broadcast(currentAuthToken, notification);

        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    private void notifyClient(Session session, String msg) throws ResponseException {
        try {
            session.getRemote().sendString(msg);
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    private void sendError(Session session, String errorMessage) throws ResponseException {
        try {
            var error = objectEncoderDecoder.encode(new ErrorMessage(errorMessage));
            session.getRemote().sendString(error);
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    @OnWebSocketError
    public void onWebSocketError(Session session, Throwable ex) throws ResponseException {
        sendError(session, ex.getMessage());
    }
}
