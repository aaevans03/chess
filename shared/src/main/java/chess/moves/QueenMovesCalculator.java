package chess.moves;

import chess.*;

import java.util.Collection;

public class QueenMovesCalculator extends MovesCalculator {

    QueenMovesCalculator(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        super(board, myPosition, piece);
    }

    public Collection<ChessMove> pieceMoves() {
        var initialRow = initialPos.getRow();
        var initialCol = initialPos.getColumn();

        // calculate moves in 8 directions, use two loops
        for (int rowDirection = -1; rowDirection <= 1; rowDirection++) {
            for (int colDirection = -1; colDirection <= 1; colDirection++) {
                calculateOneDirection(initialRow, initialCol, rowDirection, colDirection);
            }
        }
        return newMoves;
    }
}
