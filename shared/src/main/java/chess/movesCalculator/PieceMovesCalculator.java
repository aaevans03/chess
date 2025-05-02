package chess.movesCalculator;

import chess.ChessMove;
import java.util.Collection;

public interface PieceMovesCalculator {

    // Interface method (does not have a body, body is implemented by the children)
    Collection<ChessMove> pieceMoves();
}