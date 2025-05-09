package chess.moves;

import chess.*;

import java.util.Collection;

public class RookMovesCalculator extends MovesCalculator {

    RookMovesCalculator(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        super(board, myPosition, piece);
    }

    public Collection<ChessMove> pieceMoves() {
        var initialRow = initialPos.getRow();
        var initialCol = initialPos.getColumn();

        // calculate moves in 4 directions
        calculateOneDirection(initialRow, initialCol, 1, 0);
        calculateOneDirection(initialRow, initialCol, -1, 0);
        calculateOneDirection(initialRow, initialCol, 0, 1);
        calculateOneDirection(initialRow, initialCol, 0, -1);

        return newMoves;
    }
}
