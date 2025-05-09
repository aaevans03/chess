package chess.movescalculator;

import chess.*;

import java.util.Collection;

public class PawnMovesCalculator extends MovesCalculator {

    final ChessGame.TeamColor color;
    final ChessGame.TeamColor white = ChessGame.TeamColor.WHITE;
    final ChessGame.TeamColor black = ChessGame.TeamColor.BLACK;

    PawnMovesCalculator(ChessBoard board, ChessPosition myPosition, ChessPiece piece) {
        super(board, myPosition, piece);
        this.color = piece.getTeamColor();
    }

    public Collection<ChessMove> pieceMoves() {
        var initialRow = initialPos.getRow();
        var initialCol = initialPos.getColumn();

        switch (color) {
            case WHITE -> {
                // move pawn one forward
                calculateOneSquare(initialRow, initialCol, 1, 0);

                // calculate possible diagonal moves
                calculateOneSquare(initialRow, initialCol, 1, -1);
                calculateOneSquare(initialRow, initialCol, 1, 1);

            }
            case BLACK -> {
                // move pawn one forward
                calculateOneSquare(initialRow, initialCol, -1, 0);

                // calculate possible diagonal moves
                calculateOneSquare(initialRow, initialCol, -1, -1);
                calculateOneSquare(initialRow, initialCol, -1, 1);
            }
        }

        return newMoves;
    }

    public void calculateOneSquare(int row, int col, int rowDirection, int colDirection) {
        int initialRow = row;
        row += rowDirection;
        col += colDirection;

        if (positionInBounds(row, col)) {

            ChessPosition newPos = new ChessPosition(row, col);

            // if you're looking to move diagonally and there's no piece there, simply return
            if (colDirection != 0 && checkIfEmpty(newPos)) {
                return;
            }

            // if you're looking to move straight forward and there's a piece there, simply return
            if (colDirection == 0 && !checkIfEmpty(newPos)) {
                return;
            }

            // if there's a piece there that's the same color, simply return
            if (!checkIfEmpty(newPos) && board.getPiece(newPos).getTeamColor() == color) {
                return;
            }

            // if piece is WHITE and at end of board, promote
            if (color == white && row == 8) {
                addPromotionMoves(newPos);
                return;
            }

            // if piece is BLACK and at end of board, promote
            else if (color == black && row == 1) {
                addPromotionMoves(newPos);
                return;
            }

            // if you are at the start, move two spaces if there's not a piece there.
            if (color == white && initialRow == 2) {
                newMoves.add(new ChessMove(initialPos, newPos, null));

                // check second space
                ChessPosition secondMove = new ChessPosition(row + 1, col);
                if (checkIfEmpty(secondMove)) {
                    newMoves.add(new ChessMove(initialPos, secondMove, null));
                }
            }
            else if (color == black && initialRow == 7) {
                newMoves.add(new ChessMove(initialPos, newPos, null));

                // check second space
                ChessPosition secondMove = new ChessPosition(row - 1, col);
                if (checkIfEmpty(secondMove)) {
                    newMoves.add(new ChessMove(initialPos, secondMove, null));
                }
            }

            // if all else fails, simply add a move
            else {
                newMoves.add(new ChessMove(initialPos, newPos, null));
            }
        }
    }

    public void addPromotionMoves(ChessPosition newPos) {
        newMoves.add(new ChessMove(initialPos, newPos, ChessPiece.PieceType.ROOK));
        newMoves.add(new ChessMove(initialPos, newPos, ChessPiece.PieceType.KNIGHT));
        newMoves.add(new ChessMove(initialPos, newPos, ChessPiece.PieceType.BISHOP));
        newMoves.add(new ChessMove(initialPos, newPos, ChessPiece.PieceType.QUEEN));
    }

    public boolean checkIfEmpty(ChessPosition newPos) {
        return board.getPiece(newPos) == null;
    }
}

