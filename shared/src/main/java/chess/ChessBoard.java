package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    // My chess board is stored as a 8x8 array of chess pieces. At first, it is filled with null spaces.
    private final ChessPiece[][] board = new ChessPiece[8][8];

    public ChessBoard() {
        // nothing
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow() - 1][position.getColumn() - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        // add white pieces
        var white = ChessGame.TeamColor.WHITE;
        addPiece(new ChessPosition(1,1), new ChessPiece(white, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(1,2), new ChessPiece(white, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(1,3), new ChessPiece(white, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(1,4), new ChessPiece(white, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(1,5), new ChessPiece(white, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(1,6), new ChessPiece(white, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(1,7), new ChessPiece(white, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(1,8), new ChessPiece(white, ChessPiece.PieceType.ROOK));
        for (int i = 1; i <=8; i++) {
            addPiece(new ChessPosition(2, i), new ChessPiece(white, ChessPiece.PieceType.PAWN));
        }

        // add black pieces
        var black = ChessGame.TeamColor.BLACK;
        for (int i = 1; i <=8; i++) {
            addPiece(new ChessPosition(7, i), new ChessPiece(black, ChessPiece.PieceType.PAWN));
        }
        addPiece(new ChessPosition(8,1), new ChessPiece(black, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(8,2), new ChessPiece(black, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8,3), new ChessPiece(black, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8,4), new ChessPiece(black, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(8,5), new ChessPiece(black, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(8,6), new ChessPiece(black, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8,7), new ChessPiece(black, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8,8), new ChessPiece(black, ChessPiece.PieceType.ROOK));
    }

    /**
     * Clones the chess board.
     * @return a deep copy of the chess board.
     */
    public ChessBoard cloneBoard() {
        ChessBoard clone = new ChessBoard();

        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition curPos = new ChessPosition(i, j);
                ChessPiece curPiece = board[i-1][j-1];

                if (curPiece != null) {
                    clone.addPiece(curPos, new ChessPiece(curPiece));
                }
            }
        }
        return clone;
    }

    /**
     * Locates the king on the board.
     * @param color the color of the king to locate.
     * @return the position of the king
     */
    public ChessPosition locateKing(ChessGame.TeamColor color) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                var targetPiece = board[row][col];
                if (targetPiece != null && targetPiece.getPieceType() == ChessPiece.PieceType.KING && targetPiece.getTeamColor() == color) {
                    return new ChessPosition(row + 1, col + 1);
                }
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "board=" + Arrays.toString(board) +
                '}';
    }
}
