package chess.movesCalculator;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class RookMovesCalculator implements PieceMovesCalculator {

    private final Collection<ChessMove> pieceMoves;
    private final ChessPosition myPosition;
    private final ChessBoard board;
    private final ChessGame.TeamColor pieceColor;

    public RookMovesCalculator(ChessPosition myPosition, ChessBoard board, ChessGame.TeamColor pieceColor) {
        pieceMoves = new ArrayList<>();
        this.myPosition = myPosition;
        this.board = board;
        this.pieceColor = pieceColor;
    }

    // calculate the moves of a rook
    public Collection<ChessMove> pieceMoves() {
        // algorithm: calculate all moves diagonal to the rook, and returns it in a ChessMove Collection
        var originalRow = myPosition.getRow();
        var originalCol = myPosition.getColumn();

        // 4 for loops: one in each direction
        calculateOneDirection(originalRow, originalCol, 1, 0);
        calculateOneDirection(originalRow, originalCol, -1, 0);
        calculateOneDirection(originalRow, originalCol, 0, 1);
        calculateOneDirection(originalRow, originalCol, 0, -1);

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
            // increment the direction
            newRow += rowDirection;
            newCol += colDirection;

            // calculate new position
            var newPosition = new ChessPosition(newRow, newCol);

            // Check to see if the new coordinates are within the bounds
            if (0 < newRow && newRow <= 8 && 0 < newCol && newCol <= 8) {
                // if there's a piece there, see if it's from its own team
                if (board.getPiece(newPosition) != null) {
                    ownColor = (board.getPiece(newPosition).getTeamColor() == pieceColor);
                }
                // if the piece is not blocked or if the piece there is not of its own color, you can move into the space
                if (!blocked && !ownColor) {
                    pieceMoves.add(new ChessMove(myPosition, new ChessPosition(newRow, newCol), null));
                }
                // if there is a piece there, you can't move past it
                if (board.getPiece(newPosition) != null) {
                    blocked = true;
                }
            }
        }
    }
}
