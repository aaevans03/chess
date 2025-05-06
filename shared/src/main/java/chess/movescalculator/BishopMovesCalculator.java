package chess.movescalculator;

import java.util.Collection;
import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

public class BishopMovesCalculator extends StraightMovesCalculator implements PieceMovesCalculator {

    // initialize BishopMovesCalculator using superclass
    public BishopMovesCalculator(ChessPosition myPosition, ChessBoard board, ChessGame.TeamColor pieceColor) {
        super(myPosition, board, pieceColor);
    }

    // calculate the moves of a bishop
    public Collection<ChessMove> pieceMoves() {
        // algorithm: calculate all moves diagonal to the bishop, and returns it in a ChessMove Collection
        var originalRow = myPosition.getRow();
        var originalCol = myPosition.getColumn();

        // 4 for loops: one in each direction
        calculateOneDirection(originalRow, originalCol, 1, 1);
        calculateOneDirection(originalRow, originalCol, -1, 1);
        calculateOneDirection(originalRow, originalCol, -1, -1);
        calculateOneDirection(originalRow, originalCol, 1, -1);

        return pieceMoves;
    }
}