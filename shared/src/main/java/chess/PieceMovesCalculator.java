package chess;

import java.util.ArrayList;
import java.util.Collection;

public interface PieceMovesCalculator {

    // Interface method (does not have a body, body is implemented by the children)
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);
}

class BishopMovesCalculator implements PieceMovesCalculator {

    // calculate the moves of a bishop
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        // algorithm: calculate all moves diagonal to the bishop, and returns it in a ChessMove Collection
        var originalRow = myPosition.getRow();
        var originalCol = myPosition.getColumn();

        var newRow = originalRow;
        var newCol = originalCol;

        // create a Collection to store new list of piece moves
        Collection<ChessMove> pieceMoves = new ArrayList<ChessMove>();

        // 4 for loops: one in each direction
        System.out.println("Loop time");

        // Loop 1: up and right
        for (int i = 1; i < 8; i++) {
            newRow++;
            newCol++;

            if (newRow <= 8 && newCol <= 8) {
                pieceMoves.add(new ChessMove(myPosition, new ChessPosition(newRow,newCol),null));
                System.out.println("New chess move added");
            }
        }

        return pieceMoves;
    }
}
