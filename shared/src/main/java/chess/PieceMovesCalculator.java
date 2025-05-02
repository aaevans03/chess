package chess;

import java.util.ArrayList;
import java.util.Collection;

public interface PieceMovesCalculator {

    // Interface method (does not have a body, body is implemented by the children)
    public Collection<ChessMove> pieceMoves();
}

class BishopMovesCalculator implements PieceMovesCalculator {

    private Collection<ChessMove> pieceMoves;
    private final ChessPosition myPosition;
    private final ChessBoard board;
    private final ChessGame.TeamColor pieceColor;

    BishopMovesCalculator(ChessPosition myPosition, ChessBoard board, ChessGame.TeamColor pieceColor) {
        pieceMoves = new ArrayList<ChessMove>();
        this.myPosition = myPosition;
        this.board = board;
        this.pieceColor = pieceColor;
    }

    // calculate the moves of a bishop
    public Collection<ChessMove> pieceMoves() {
        // algorithm: calculate all moves diagonal to the bishop, and returns it in a ChessMove Collection
        var originalRow = myPosition.getRow();
        var originalCol = myPosition.getColumn();

        var newRow = originalRow;
        var newCol = originalCol;

        // 4 for loops: one in each direction

        calculateOneDirection(originalRow, originalCol, 1, 1);
        calculateOneDirection(originalRow, originalCol, -1, 1);
        calculateOneDirection(originalRow, originalCol, -1, -1);
        calculateOneDirection(originalRow, originalCol, 1, -1);

/*      OLD CODE
        // Loop 1: up and right
        for (int i = 1; i < 8; i++) {
            newRow++;
            newCol++;

            if (newRow <= 8 && newCol <= 8) {
                pieceMoves.add(new ChessMove(myPosition, new ChessPosition(newRow,newCol),null));
                System.out.println("New chess move added");
            }
        }
*/

        return pieceMoves;
    }

    private void calculateOneDirection(int originalRow, int originalCol, int rowDirection, int colDirection) {
        var newRow = originalRow;
        var newCol = originalCol;

        boolean blocked = false;
        boolean ownColor = false;

        for (int i = 1; i < 7; i++) {
            newRow += rowDirection;
            newCol += colDirection;

            var newPosition = new ChessPosition(newRow, newCol);

            // Check to see if the new coordinates are within the bounds
            if (0 < newRow && newRow <= 8 && 0 < newCol && newCol <= 8) {
                if (board.getPiece(newPosition) != null) {
                    ownColor = (board.getPiece(newPosition).getTeamColor() == pieceColor);
                }
                if (!blocked && !ownColor) {
                    pieceMoves.add(new ChessMove(myPosition, new ChessPosition(newRow, newCol), null));
                }
                if (board.getPiece(newPosition) != null) {
                    blocked = true;
                }
            }
        }
    }
}
