package chess.moves;

import chess.*;

import java.util.Collection;

public class BishopMovesCalculator extends MovesCalculator {

    BishopMovesCalculator(ChessBoard board, ChessPosition initialPos, ChessPiece piece) {
        super(board, initialPos, piece);
    }

    public Collection<ChessMove> pieceMoves() {
        var initialRow = initialPos.getRow();
        var initialCol = initialPos.getColumn();

        // calculate moves in 4 directions
        calculateOneDirection(initialRow, initialCol, 1, 1);
        calculateOneDirection(initialRow, initialCol, -1, 1);
        calculateOneDirection(initialRow, initialCol, -1, -1);
        calculateOneDirection(initialRow, initialCol, 1, -1);

        return newMoves;
    }
}