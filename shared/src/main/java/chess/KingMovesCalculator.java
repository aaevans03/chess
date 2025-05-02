package chess;

import java.util.ArrayList;
import java.util.Collection;

class KingMovesCalculator implements PieceMovesCalculator {
    private final Collection<ChessMove> pieceMoves;
    private final ChessPosition myPosition;
    private final ChessBoard board;
    private final ChessGame.TeamColor pieceColor;

    KingMovesCalculator(ChessPosition myPosition, ChessBoard board, ChessGame.TeamColor pieceColor) {
        pieceMoves = new ArrayList<>();
        this.myPosition = myPosition;
        this.board = board;
        this.pieceColor = pieceColor;
    }

    public Collection<ChessMove> pieceMoves() {
        var originalRow = myPosition.getRow();
        var originalCol = myPosition.getColumn();

        // Calculate moves all around the king.
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {

                var newRow = originalRow + i;
                var newCol = originalCol + j;
                var newPosition = new ChessPosition(newRow, newCol);
                boolean ownColor = false;

                // if new coordinates are within the bounds and the new position does not equal the old one
                if (0 < newRow && newRow <= 8 && 0 < newCol && newCol <= 8 && !newPosition.equals(myPosition)) {
                    // check to see if the piece in the new position is the same color as the king
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
        return pieceMoves;
    }
}
