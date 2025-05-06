package chess.movescalculator;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;

public class RookMovesCalculator extends StraightMovesCalculator implements PieceMovesCalculator {

    // initialize RookMovesCalculator using superclass
    public RookMovesCalculator(ChessPosition myPosition, ChessBoard board, ChessGame.TeamColor pieceColor) {
        super(myPosition, board, pieceColor);
    }

    // calculate the moves of a rook
    public Collection<ChessMove> pieceMoves() {
        // algorithm: calculate all moves diagonal to the rook, and returns it in a ChessMove Collection
        var originalRow = myPosition.getRow();
        var originalCol = myPosition.getColumn();

        // 4 for loops: one in each direction
        calculateOneDirection(originalRow, originalCol, 1, 0);
        calculateOneDirection(originalRow, originalCol, -1, 0);
        calculateOneDirection(originalRow, originalCol, 0, 1);
        calculateOneDirection(originalRow, originalCol, 0, -1);

        return pieceMoves;
    }
}
