package chess.movesCalculator;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

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

        switch (pieceColor) {
            // case WHITE: move up
            case WHITE -> calculatePawnMovement(originalRow, originalCol, 1);
            // case BLACK: move down
            case BLACK -> calculatePawnMovement(originalRow, originalCol, -1);
        }
        return pieceMoves;
    }

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

        // Loop through 3 possible positions
        for (int i = -1; i <= 1; i++) {
            var newCol = originalCol + i;

            // calculate new position
            var newPosition = new ChessPosition(newRow, newCol);

            // Check to see if the new coordinates are within the bounds
            if (0 < newRow && newRow <= 8 && 0 < newCol && newCol <= 8) {

                // for moving straight ahead only
                if (i == 0) {
                    // if the piece is not blocked, you can move into the space
                    if (board.getPiece(newPosition) == null) {
                        pieceMoves.add(new ChessMove(myPosition, new ChessPosition(newRow, newCol), null));

                        if (inStartingSpot) {
                            if (board.getPiece(new ChessPosition(newRow + rowDirection, newCol)) == null) {
                                pieceMoves.add(new ChessMove(myPosition, new ChessPosition(newRow + rowDirection, newCol), null));
                            }
                        }
                        // check for promotion
                        // WHITE


                    }
                }

                // moving diagonally
                if (board.getPiece(newPosition) != null && !board.getPiece(newPosition).getTeamColor().equals(pieceColor)) {
                    pieceMoves.add(new ChessMove(myPosition, new ChessPosition(newRow, newCol), null));
                }


                if (rowDirection == 1 && newRow == 8) {
                    pieceMoves.add(new ChessMove(myPosition, new ChessPosition(newRow, newCol), null));
                }

            }

        }



        // Capturing another piece


    }

    private void checkForPromotion(int newRow, int newCol, int rowDirection) {

    }
}
