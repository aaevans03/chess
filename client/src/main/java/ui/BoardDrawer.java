package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import static ui.EscapeSequences.*;

public class BoardDrawer {

    final String borderColor = SET_BG_COLOR_LIGHT_GREY;
    final String whiteSquare = SET_BG_COLOR_WHITE;
    final String blackSquare = SET_BG_COLOR_BLACK;
    final String whitePiece = SET_TEXT_COLOR_RED;
    final String blackPiece = SET_TEXT_COLOR_BLUE;

    public String drawBoard(ChessGame.TeamColor color, ChessBoard gameBoard) {
        return RESET_TEXT_COLOR +
                SET_TEXT_BOLD +
                drawLetterRow(color) +
                drawBoardRow(color, gameBoard) +
                drawLetterRow(color);
    }

    private String drawBoardRow(ChessGame.TeamColor color, ChessBoard gameBoard) {

        var boardRow = new StringBuilder();

        if (color.equals(ChessGame.TeamColor.WHITE)) {
            for (int row = 8; row >= 1; row--) {
                boardRow.append(borderColor);
                boardRow.append(" ").append(row).append(" ");

                for (int col = 1; col <= 8; col++) {
                    boardRow.append(drawBoardSquare(gameBoard, row, col));
                }

                boardRow.append(borderColor);
                boardRow.append(" ").append(row).append(" ");
                boardRow.append(RESET_BG_COLOR).append("\n");
            }
        } else if (color.equals(ChessGame.TeamColor.BLACK)) {
            for (int row = 1; row <= 8; row++) {
                boardRow.append(borderColor);
                boardRow.append(" ").append(row).append(" ");

                for (int col = 8; col >= 1; col--) {
                    boardRow.append(drawBoardSquare(gameBoard, row, col));
                }

                boardRow.append(borderColor);
                boardRow.append(" ").append(row).append(" ");
                boardRow.append(RESET_BG_COLOR).append("\n");
            }
        }
        return boardRow.toString();
    }

    private String drawBoardSquare(ChessBoard gameBoard, int row, int col) {

        var square = new StringBuilder();

        square.append(colorSquare(row, col)).append(" ");

        var piece = gameBoard.getPiece(new ChessPosition(row, col));

        if (piece != null) {
            square.append(drawPiece(piece));
        } else {
            square.append(" ");
        }

        square.append(" ").append(RESET_TEXT_COLOR);
        return square.toString();
    }

    private String drawPiece(ChessPiece piece) {
        var square = new StringBuilder();

        var pieceColor = piece.getTeamColor();
        square.append(pieceColor.equals(ChessGame.TeamColor.WHITE) ?
                whitePiece : blackPiece);

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

    private String colorSquare(int row, int col) {
        return ((row + col) % 2 == 1) ? whiteSquare : blackSquare;
    }
}
