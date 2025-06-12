package ui;

import chess.*;

import java.util.Collection;

import static ui.EscapeSequences.*;

public class BoardDrawer {

    final String borderColor = SET_BG_COLOR_LIGHT_GREY;
    final String whiteSquare = SET_BG_COLOR_WHITE;
    final String highlightedWhiteSquare = SET_BG_COLOR_GREEN;
    final String blackSquare = SET_BG_COLOR_BLACK;
    final String highlightedBlackSquare = SET_BG_COLOR_DARK_GREEN;
    final String whitePiece = SET_TEXT_COLOR_RED;
    final String blackPiece = SET_TEXT_COLOR_BLUE;

    public String drawBoard(ChessGame.TeamColor color, ChessBoard gameBoard, Collection<ChessMove> moves) {
        return RESET_TEXT_COLOR +
                SET_TEXT_BOLD +
                drawLetterRow(color) +
                drawBoardRows(color, gameBoard, moves) +
                drawLetterRow(color) +
                RESET_TEXT_BOLD_FAINT;
    }

    private String drawBoardRows(ChessGame.TeamColor color, ChessBoard gameBoard, Collection<ChessMove> moves) {

        var boardRow = new StringBuilder();

        if (color.equals(ChessGame.TeamColor.WHITE)) {
            for (int row = 8; row >= 1; row--) {
                boardRow.append(borderColor).append(" ").append(row).append(" ");

                for (int col = 1; col <= 8; col++) {
                    boolean validMoveSquare = isValidMoveSquare(moves, row, col);
                    boolean pieceToBeMoved = isPieceToBeMoved(moves, row, col);

                    boardRow.append(drawBoardSquare(gameBoard, row, col, validMoveSquare, pieceToBeMoved));
                }

                boardRow.append(borderColor).append(" ").append(row).append(" ");
                boardRow.append(RESET_BG_COLOR).append("\n");
            }
        } else if (color.equals(ChessGame.TeamColor.BLACK)) {
            for (int row = 1; row <= 8; row++) {
                boardRow.append(borderColor).append(" ").append(row).append(" ");

                for (int col = 8; col >= 1; col--) {

                    boolean validMoveSquare = isValidMoveSquare(moves, row, col);
                    boolean pieceToBeMoved = isPieceToBeMoved(moves, row, col);

                    boardRow.append(drawBoardSquare(gameBoard, row, col, validMoveSquare, pieceToBeMoved));
                }

                boardRow.append(borderColor).append(" ").append(row).append(" ");
                boardRow.append(RESET_BG_COLOR).append("\n");
            }
        }
        return boardRow.toString();
    }

    private boolean isValidMoveSquare(Collection<ChessMove> moves, int row, int col) {
        boolean validMoveSquare = false;
        if (moves != null) {
            for (var move : moves) {
                if (move.getEndPosition().getRow() == row && move.getEndPosition().getColumn() == col) {
                    validMoveSquare = true;
                    break;
                }
            }
        }
        return validMoveSquare;
    }

    private boolean isPieceToBeMoved(Collection<ChessMove> moves, int row, int col) {
        boolean pieceToBeMoved = false;
        if (moves != null && !moves.isEmpty()) {
            // Grab any move from the list and get the start position
            var move = moves.iterator().next();

            if (move.getStartPosition().equals(new ChessPosition(row, col))) {
                pieceToBeMoved = true;
            }
        }
        return pieceToBeMoved;
    }

    private String drawBoardSquare(ChessBoard gameBoard, int row, int col, boolean validMoveSquare, boolean pieceToBeMoved) {

        var square = new StringBuilder();

        square.append(colorSquare(row, col, validMoveSquare, pieceToBeMoved)).append(" ");

        var piece = gameBoard.getPiece(new ChessPosition(row, col));

        if (piece != null) {
            square.append(drawPiece(piece, validMoveSquare, pieceToBeMoved));
        } else {
            square.append(" ");
        }

        square.append(" ").append(RESET_TEXT_COLOR);
        return square.toString();
    }

    private String drawPiece(ChessPiece piece, boolean validMoveSquare, boolean pieceToBeMoved) {
        var square = new StringBuilder();

        var pieceColor = piece.getTeamColor();

        if (pieceToBeMoved) {
            square.append(SET_TEXT_COLOR_BLACK);
        } else if (validMoveSquare) {
            square.append(SET_TEXT_COLOR_BLACK);
        } else {
            square.append(pieceColor.equals(ChessGame.TeamColor.WHITE) ?
                    whitePiece : blackPiece);
        }

        var type = piece.getPieceType();

        if (piece.getPieceType().equals(ChessPiece.PieceType.KNIGHT)) {
            square.append("N");
        } else {
            square.append(type.toString().charAt(0));
        }
        return square.toString();
    }

    private String drawLetterRow(ChessGame.TeamColor color) {
        String[] letterList = new String[]{
                "a", "b", "c", "d", "e", "f", "g", "h"
        };

        var row = new StringBuilder();
        row.append(borderColor);
        row.append("   ");

        if (color.equals(ChessGame.TeamColor.WHITE)) {
            for (int i = 0; i < 8; i++) {
                row.append(" ").append(letterList[i]).append(" ");
            }
        } else {
            for (int i = 7; i >= 0; i--) {
                row.append(" ").append(letterList[i]).append(" ");
            }
        }

        row.append("   ").append(RESET_BG_COLOR).append("\n");
        return row.toString();
    }

    private String colorSquare(int row, int col, boolean validMoveSquare, boolean pieceToBeMoved) {
        if (pieceToBeMoved) {
            return SET_BG_COLOR_YELLOW;
        } else if (validMoveSquare) {
            return ((row + col) % 2 == 1) ? highlightedWhiteSquare : highlightedBlackSquare;
        }

        return ((row + col) % 2 == 1) ? whiteSquare : blackSquare;
    }
}
