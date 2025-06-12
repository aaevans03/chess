package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import websocket.messages.ErrorMessage;
import websocket.messages.NotificationMessage;

import java.util.Collection;

public interface NotificationHandler {
    void notify(NotificationMessage notification);

    void printBoard(ChessGame.TeamColor teamColor, ChessBoard chessBoard, Collection<ChessMove> moves);

    void notifyError(ErrorMessage errorMessage);

    void printValidMoves(ChessGame.TeamColor teamcolor, ChessBoard chessBoard, Collection<ChessMove> moves);
}
