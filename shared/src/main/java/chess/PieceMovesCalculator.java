package chess;

import java.util.Collection;

public interface PieceMovesCalculator {

    // Interface method (does not have a body, body is implemented by the children)
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);
}
/*
class BishopMovesCalculator implements PieceMovesCalculator {

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        // calculate the moves of a bishop
        // algorithm: calculate all moves diagonal to the bishop, and
    }
}
*/