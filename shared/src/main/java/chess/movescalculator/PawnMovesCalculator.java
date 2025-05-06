package chess.movescalculator;

import chess.*;
import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator implements PieceMovesCalculator {

    private final Collection<ChessMove> pieceMoves;
    private final ChessPosition myPosition;
    private final ChessBoard board;
    private final ChessGame.TeamColor pieceColor;

    public PawnMovesCalculator(ChessPosition myPosition, ChessBoard board, ChessGame.TeamColor pieceColor) {
        pieceMoves = new ArrayList<>();
        this.myPosition = myPosition;
        this.board = board;
        this.pieceColor = pieceColor;
    }

    // calculate the moves of a pawn
    public Collection<ChessMove> pieceMoves() {
        var originalRow = myPosition.getRow();
        var originalCol = myPosition.getColumn();

        // change case based on piece color
        switch (pieceColor) {
            // case WHITE: move up
            case WHITE -> calculatePawnMovement(originalRow, originalCol, 1);
            // case BLACK: move down
            case BLACK -> calculatePawnMovement(originalRow, originalCol, -1);
        }
        return pieceMoves;
    }

    // calculate pawn movement based on if it is white or black
    private void calculatePawnMovement(int originalRow, int originalCol, int rowDirection) {
        var newRow = originalRow;
        boolean inStartingSpot = false;

        // WHITE piece and in starting row
        if (rowDirection == 1 && originalRow == 2) {
            inStartingSpot = true;
        }
        // BLACK piece and in starting row
        else if (rowDirection == -1 && originalRow == 7) {
            inStartingSpot = true;
        }

        // increment/decrement the row
        newRow += rowDirection;

        // Loop through 3 possible positions (to account for capturing)
        for (int i = -1; i <= 1; i++) {
            // Calculate new column value
            var newCol = originalCol + i;

            // Calculate new position
            var newPosition = new ChessPosition(newRow, newCol);

            // Check to see if the new coordinates are within the bounds
            if (0 < newRow && newRow <= 8 && 0 < newCol && newCol <= 8) {

                // for moving straight ahead only
                if (i == 0) {
                    moveAhead(rowDirection, newPosition, newRow, newCol, inStartingSpot);
                }

                // Diagonal moves for capturing other pieces
                if (i != 0 && board.getPiece(newPosition) != null && !board.getPiece(newPosition).getTeamColor().equals(pieceColor)) {
                    // Check for a promotion, if there's no promotion then add a regular movement with no promotion piece
                    if (!checkForPromotion(newRow, newCol, rowDirection)) {
                        pieceMoves.add(new ChessMove(myPosition, new ChessPosition(newRow, newCol), null));
                    }
                }
            }
        }
    }

    private void moveAhead(int rowDirection, ChessPosition newPosition, int newRow, int newCol, boolean inStartingSpot) {
        // if the piece is not blocked, you can move into the space
        if (board.getPiece(newPosition) == null) {

            // If there is no promotion, add a regular forward movement with no promotion piece
            if (!checkForPromotion(newRow, newCol, rowDirection)) {
                pieceMoves.add(new ChessMove(myPosition, new ChessPosition(newRow, newCol), null));

                // If the pawn is in the starting spot, add another possible move
                if (inStartingSpot) {
                    if (board.getPiece(new ChessPosition(newRow + rowDirection, newCol)) == null) {
                        pieceMoves.add(new ChessMove(myPosition, new ChessPosition(newRow + rowDirection, newCol), null));
                    }
                }
            }
        }
    }

    // Check for a promotion, add the possible promotion moves. Return true if there is one.
    private boolean checkForPromotion(int newRow, int newCol, int rowDirection) {
        // WHITE and at top of board
        if (rowDirection == 1 && newRow == 8) {
            PromotePawn(newRow, newCol);
            return true;
        }
        // BLACK and add bottom of board
        else if (rowDirection == -1 && newRow == 1) {
            PromotePawn(newRow, newCol);
            return true;
        }
        return false;
    }

    private void PromotePawn(int newRow, int newCol) {
        pieceMoves.add(new ChessMove(myPosition, new ChessPosition(newRow, newCol), ChessPiece.PieceType.QUEEN));
        pieceMoves.add(new ChessMove(myPosition, new ChessPosition(newRow, newCol), ChessPiece.PieceType.BISHOP));
        pieceMoves.add(new ChessMove(myPosition, new ChessPosition(newRow, newCol), ChessPiece.PieceType.ROOK));
        pieceMoves.add(new ChessMove(myPosition, new ChessPosition(newRow, newCol), ChessPiece.PieceType.KNIGHT));
    }
}
