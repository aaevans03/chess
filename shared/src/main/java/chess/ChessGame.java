package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard gameBoard;
    private TeamColor currentTeamTurn;

    public ChessGame() {
        gameBoard = new ChessBoard();
        gameBoard.resetBoard();
        setTeamTurn(TeamColor.WHITE);
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTeamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentTeamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location.
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        // First, get the complete list of moves
        ChessPiece currentPiece = gameBoard.getPiece(startPosition);
        if (currentPiece == null) {
            return null;
        }
        var allMoves = currentPiece.pieceMoves(gameBoard, startPosition);

        // Get the piece color
        TeamColor curColor = currentPiece.getTeamColor();

        // Loop through all possible moves and determine if they do not put the king in check
        Collection<ChessMove> validMoves = new ArrayList<>();
        for (ChessMove move : allMoves) {
            // Clone the board
            ChessBoard newBoard = gameBoard.cloneBoard();

            // Make the move
            ChessPosition startPos = move.getStartPosition();
            ChessPosition endPos = move.getEndPosition();
            ChessPiece targetPiece = newBoard.getPiece(startPos);

            newBoard.addPiece(startPos, null);
            newBoard.addPiece(endPos, targetPiece);

            // Check to see if the king is in check after that move
            if (!isKingInCheck(newBoard, curColor)) {
                validMoves.add(move);
            }
        }
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPos = move.getStartPosition();
        ChessPosition endPos = move.getEndPosition();
        ChessPiece targetPiece = gameBoard.getPiece(startPos);

        // Check to see if it's an invalid move. If it's invalid, throw InvalidMoveException
        var validMoves = validMoves(startPos);

        if (targetPiece == null || !validMoves.contains(move) || targetPiece.getTeamColor() != currentTeamTurn) {
            throw new InvalidMoveException();
        }

        // Make move
        gameBoard.addPiece(startPos, null);
        gameBoard.addPiece(endPos, targetPiece);

        // Pawn promotion
        if (move.getPromotionPiece() != null) {
            var promotionPiece = new ChessPiece(targetPiece.getTeamColor(), move.getPromotionPiece());
            gameBoard.addPiece(endPos, promotionPiece);
        }

        // Team color changed after move is made
        setTeamTurn(currentTeamTurn == TeamColor.BLACK ? TeamColor.WHITE : TeamColor.BLACK);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return isKingInCheck(gameBoard, teamColor);
    }

    /**
     * Helper function to determine if king is in check given a board
     *
     * @param board     the chess board to run on
     * @param teamColor the team color to run with
     * @return true if king is in check
     */
    private boolean isKingInCheck(ChessBoard board, TeamColor teamColor) {
        // loop through all pieces on that board
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                // target piece
                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece newPiece = board.getPiece(newPosition);

                // get the moves of that new piece
                if (newPiece != null) {
                    var newPieceMoves = newPiece.pieceMoves(board, newPosition);
                    // if that piece captures the king, then it is in check
                    if (doesPieceCaptureKing(newPieceMoves, board, teamColor)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean doesPieceCaptureKing(Collection<ChessMove> moves, ChessBoard board, TeamColor teamColor) {
        // Get the 2 kings' positions
        var whiteKingPos = board.locateKing(TeamColor.WHITE);
        var blackKingPos = board.locateKing(TeamColor.BLACK);

        // iterate through this new list, see if it matches the square the king is on
        for (ChessMove newMove : moves) {
            ChessPosition newEndPos = newMove.getEndPosition();

            switch (teamColor) {
                case WHITE -> {
                    if (newEndPos.equals(whiteKingPos)) {
                        return true;
                    }
                }
                case BLACK -> {
                    if (newEndPos.equals(blackKingPos)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        boolean inCheckmate = true;

        // loop through all pieces on that board
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                // target piece
                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece newPiece = gameBoard.getPiece(newPosition);

                // get the moves of that new piece
                if (newPiece != null && newPiece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = validMoves(newPosition);

                    if (!moves.isEmpty()) {
                        inCheckmate = false;
                    }
                }
            }
        }
        return inCheckmate;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        // if the given team has no legal moves, but the king is not in immediate danger.
        return isInCheckmate(teamColor) && !isInCheck(teamColor);
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        gameBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return gameBoard;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(gameBoard, chessGame.gameBoard) && currentTeamTurn == chessGame.currentTeamTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameBoard, currentTeamTurn);
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "gameBoard=" + gameBoard +
                ", currentTeamTurn=" + currentTeamTurn +
                '}';
    }
}
