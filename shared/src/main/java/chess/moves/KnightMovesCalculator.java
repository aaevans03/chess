package chess.moves;

import chess.*;

import java.util.Collection;

public class KnightMovesCalculator extends MovesCalculator {

    KnightMovesCalculator(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        super(board, myPosition, piece);
    }

    public Collection<ChessMove> pieceMoves() {
        var initialRow = initialPos.getRow();
        var initialCol = initialPos.getColumn();

        // calculate moves for 8 squares
        calculateOneSquare(initialRow, initialCol, 1, -2);
        calculateOneSquare(initialRow, initialCol, 2, -1);
        calculateOneSquare(initialRow, initialCol, 2, 1);
        calculateOneSquare(initialRow, initialCol, 1, 2);
        calculateOneSquare(initialRow, initialCol, -1, 2);
        calculateOneSquare(initialRow, initialCol, -2, 1);
        calculateOneSquare(initialRow, initialCol, -1, -2);
        calculateOneSquare(initialRow, initialCol, -2, -1);

        return newMoves;
    }
}
