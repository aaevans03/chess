package chess.movesCalculator;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMovesCalculator implements PieceMovesCalculator {

    private final Collection<ChessMove> pieceMoves;
    private final ChessPosition myPosition;
    private final ChessBoard board;
    private final ChessGame.TeamColor pieceColor;

    public KnightMovesCalculator(ChessPosition myPosition, ChessBoard board, ChessGame.TeamColor pieceColor) {
        pieceMoves = new ArrayList<>();
        this.myPosition = myPosition;
        this.board = board;
        this.pieceColor = pieceColor;
    }

    // calculate the moves of a knight
    public Collection<ChessMove> pieceMoves() {
        var originalRow = myPosition.getRow();
        var originalCol = myPosition.getColumn();

        // 8 total possible moves
        calculateOneMove(originalRow, originalCol, 2, 1);
        calculateOneMove(originalRow, originalCol, 1, 2);
        calculateOneMove(originalRow, originalCol, -1, 2);
        calculateOneMove(originalRow, originalCol, -2, 1);
        calculateOneMove(originalRow, originalCol, -2, -1);
        calculateOneMove(originalRow, originalCol, -1, -2);
        calculateOneMove(originalRow, originalCol, 1, -2);
        calculateOneMove(originalRow, originalCol, 2, -1);

        return pieceMoves;
    }

    private void calculateOneMove(int originalRow, int originalCol, int rowDirection, int colDirection) {
        var newRow = originalRow + rowDirection;
        var newCol = originalCol + colDirection;

        boolean ownColor = false;

        // calculate new position
        var newPosition = new ChessPosition(newRow, newCol);

        // Check to see if the new coordinates are within the bounds
        if (0 < newRow && newRow <= 8 && 0 < newCol && newCol <= 8) {
            // if there's a piece there, see if it's from its own team
            if (board.getPiece(newPosition) != null) {
                ownColor = (board.getPiece(newPosition).getTeamColor() == pieceColor);
            }
            // if the piece there is not of its own color, you can move into the space
            if (!ownColor) {
                pieceMoves.add(new ChessMove(myPosition, new ChessPosition(newRow, newCol), null));
            }
        }
    }
}
