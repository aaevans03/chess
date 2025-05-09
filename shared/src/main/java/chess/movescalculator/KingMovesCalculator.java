package chess.movescalculator;

import chess.*;

import java.util.Collection;

public class KingMovesCalculator extends MovesCalculator {

    KingMovesCalculator(ChessBoard board, ChessPosition initialPos, ChessPiece piece) {
        super(board, initialPos, piece);
    }

    public Collection<ChessMove> pieceMoves() {
        var initialRow = initialPos.getRow();
        var initialCol = initialPos.getColumn();

        // calculate moves for 8 squares, use two loops
        for (int rowDirection = -1; rowDirection <= 1; rowDirection++) {
            for (int colDirection = -1; colDirection <= 1; colDirection++) {
                calculateOneSquare(initialRow, initialCol, rowDirection, colDirection);
            }
        }
        return newMoves;
    }
}
