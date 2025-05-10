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
     * Gets a valid moves for a piece at the given location
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

        /*
        Process:
        1. For every one of these possible moves, create a copy of the chess board with that move made.
            a. Loop through all pieces on that board to see if they can capture the king.
                i. If any piece can capture the king, it's automatically an invalid move. Return to step 1.
                ii. If a move is ok, add it to a Collection that stores valid moves.
            b. Finish looping through the moves.
        2. Return the valid moves.
        */

        Collection<ChessMove> validMoves = new ArrayList<>();

        for (ChessMove move : allMoves) {
            // clone the board
            ChessBoard newBoard = gameBoard.cloneBoard();

            // make the move
            ChessPosition startPos = move.getStartPosition();
            ChessPosition endPos = move.getEndPosition();
            ChessPiece targetPiece = newBoard.getPiece(startPos);

            newBoard.addPiece(startPos, null);
            newBoard.addPiece(endPos, targetPiece);

            // Get the 2 kings' positions
            var newWhiteKingPos = newBoard.locateKing(TeamColor.WHITE);
            var newBlackKingPos = newBoard.locateKing(TeamColor.BLACK);

            boolean badMove = false;

            // loop through all pieces on that board
            for (int row = 1; row <= 8; row++) {
                for (int col = 1; col <= 8; col++) {
                    // target piece
                    ChessPosition newPosition = new ChessPosition(row, col);
                    ChessPiece newPiece = newBoard.getPiece(newPosition);

                    // get the moves of that new piece
                    if (newPiece != null && !newPosition.equals(endPos)) {
                        var newPieceMoves = newPiece.pieceMoves(newBoard, newPosition);

                        // iterate through this new list, see if it matches the square the king is on
                        for (ChessMove newMove : newPieceMoves) {
                            ChessPosition newEndPos = newMove.getEndPosition();

                            switch (curColor) {
                                case WHITE -> {
                                    if (newEndPos.equals(newWhiteKingPos)) {
                                        badMove = true;
                                    }
                                }
                                case BLACK -> {
                                    if (newEndPos.equals(newBlackKingPos)) {
                                        badMove = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (!badMove) {
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
        // Get the 2 kings' positions
        var whiteKingPos = gameBoard.locateKing(TeamColor.WHITE);
        var blackKingPos = gameBoard.locateKing(TeamColor.BLACK);

        boolean inCheck = false;

        // loop through all pieces on that board
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                // target piece
                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece newPiece = gameBoard.getPiece(newPosition);

                // get the moves of that new piece
                if (newPiece != null) {
                    var newPieceMoves = newPiece.pieceMoves(gameBoard, newPosition);

                    // iterate through this new list, see if it matches the square the king is on
                    for (ChessMove newMove : newPieceMoves) {
                        ChessPosition newEndPos = newMove.getEndPosition();

                        switch (teamColor) {
                            case WHITE -> {
                                if (newEndPos.equals(whiteKingPos)) {
                                    inCheck = true;
                                }
                            }
                            case BLACK -> {
                                if (newEndPos.equals(blackKingPos)) {
                                    inCheck = true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return inCheck;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {

        /*
        Process:
        1. Loop through all the pieces of the color in the board.
            a. Run validMoves to calculate the moves of each piece
        2. If there are no valid moves, then CHECKMATE
         */

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
}
