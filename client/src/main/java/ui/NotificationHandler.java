package ui;

import chess.ChessBoard;
import chess.ChessGame;
import websocket.messages.ErrorMessage;
import websocket.messages.NotificationMessage;

public interface NotificationHandler {
    void notify(NotificationMessage notification);

    void printBoard(ChessGame.TeamColor teamColor, ChessBoard chessBoard);

    void notifyError(ErrorMessage errorMessage);
}
