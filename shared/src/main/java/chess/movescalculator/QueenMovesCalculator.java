package chess.movescalculator;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;

public class QueenMovesCalculator extends StraightMovesCalculator implements PieceMovesCalculator {

    // initialize QueenMovesCalculator using superclass
    public QueenMovesCalculator(ChessPosition myPosition, ChessBoard board, ChessGame.TeamColor pieceColor) {
        super(myPosition, board, pieceColor);
    }

    // calculate the moves of a queen
    public Collection<ChessMove> pieceMoves() {
        // algorithm: calculate all moves diagonal to the queen, and returns it in a ChessMove Collection
        var originalRow = myPosition.getRow();
        var originalCol = myPosition.getColumn();

        // 8 for loops: one in each direction
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                calculateOneDirection(originalRow, originalCol, i, j);
            }
        }
        return pieceMoves;
    }
}
