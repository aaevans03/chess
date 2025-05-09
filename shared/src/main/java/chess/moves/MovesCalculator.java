package chess.moves;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public abstract class MovesCalculator {

    final protected ChessBoard board;
    final protected ChessPosition initialPos;
    final protected ChessPiece piece;
    final protected Collection<ChessMove> newMoves;

    public MovesCalculator(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        this.board = board;
        this.initialPos = myPosition;
        this.piece = piece;
        newMoves = new ArrayList<>();
    }

    // all subclasses must implement pieceMoves
    public abstract Collection<ChessMove> pieceMoves();

    // Basic calculate moves method called from ChessPiece class
    public static Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition initialPos, ChessPiece piece) {
        switch (piece.getPieceType()) {
            case BISHOP -> {
                return new BishopMovesCalculator(board, initialPos, piece).pieceMoves();
            }
            case KING -> {
                return new KingMovesCalculator(board, initialPos, piece).pieceMoves();
            }
            case KNIGHT -> {
                return new KnightMovesCalculator(board, initialPos, piece).pieceMoves();
            }
            case PAWN -> {
                return new PawnMovesCalculator(board, initialPos, piece).pieceMoves();
            }
            case QUEEN -> {
                return new QueenMovesCalculator(board, initialPos, piece).pieceMoves();
            }
            case ROOK -> {
                return new RookMovesCalculator(board, initialPos, piece).pieceMoves();
            }
        }
        return null;
    }

    // Calculate a move in one straight direction (for bishops, queens, and rooks)
    public void calculateOneDirection(int row, int col, int rowDirection, int colDirection) {
        boolean blocked = false;
        boolean sameColor = false;

        for (int i = 1; i <=8; i++) {
            row += rowDirection;
            col += colDirection;

            if (!blocked && positionInBounds(row, col)) {

                ChessPosition newPos = new ChessPosition(row, col);

                // set piece as blocked in order to stop further movement
                if (!checkIfEmpty(newPos)) {
                    blocked = true;

                    // if the piece blocking is the same color, you can't move onto it
                    if (board.getPiece(newPos).getTeamColor() == piece.getTeamColor()) {
                        sameColor = true;
                    }
                }
                // add a piece
                if (!sameColor) {
                    newMoves.add(new ChessMove(initialPos, newPos, null));
                }
            }
        }
    }

    // Calculate one square move
    public void calculateOneSquare(int row, int col, int rowDirection, int colDirection) {
        row += rowDirection;
        col += colDirection;

        if (positionInBounds(row, col)) {

            ChessPosition newPos = new ChessPosition(row, col);

            // if the piece blocking is the same color, you can't move onto it
            if (!checkIfEmpty(newPos) && board.getPiece(newPos).getTeamColor() == piece.getTeamColor()) {
                return;
            }
            // add a piece
            newMoves.add(new ChessMove(initialPos, newPos, null));
        }
    }

    // Check to see if the given row and column are in bounds in the chess board
    public boolean positionInBounds(int row, int col) {
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }

    public boolean checkIfEmpty(ChessPosition newPos) {
        return board.getPiece(newPos) == null;
    }
}