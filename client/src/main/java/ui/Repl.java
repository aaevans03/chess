package ui;

import chess.ChessBoard;
import chess.ChessGame;
import websocket.messages.ErrorMessage;
import websocket.messages.NotificationMessage;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl implements NotificationHandler {
    private final ChessClient client;

    public Repl(String serverUrl) {
        client = new ChessClient(serverUrl, this);
    }

    public void run() {
        System.out.println(SET_TEXT_BOLD + SET_TEXT_COLOR_BLUE + "Welcome to Alex's Chess Client. Type \"help\" to get started.");

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals(SET_TEXT_COLOR_MAGENTA + "  Have a nice day!")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.evaluateCommand(line);
                if (!result.isEmpty()) {
                    System.out.print(result + "\n");
                }
            } catch (Throwable e) {
                System.out.print(SET_TEXT_COLOR_RED + "  Error: " + e);
            }
        }
    }

    private void printPrompt() {
        resetTextFormatting();
        var username = client.getCurrentUsername();
        System.out.print("[" + (username == null ? "UNKNOWN_USER" : username) + "] ");
        System.out.print(">>> ");
    }

    private void resetTextFormatting() {
        System.out.print(RESET_TEXT_BOLD_FAINT + RESET_TEXT_BLINKING);
        System.out.print(RESET_TEXT_ITALIC + RESET_TEXT_UNDERLINE);
        System.out.print(RESET_TEXT_COLOR + RESET_BG_COLOR);
    }

    @Override
    public void notify(NotificationMessage notification) {
        System.out.println("\r   " + SET_TEXT_COLOR_BLUE + notification.getMessage());
        printPrompt();
    }

    @Override
    public void printBoard(ChessGame.TeamColor teamColor, ChessBoard chessBoard) {
        System.out.println("\r" + new BoardDrawer().drawBoard(teamColor, chessBoard));
        printPrompt();
    }

    @Override
    public void notifyError(ErrorMessage errorMessage) {
        System.out.println("\r   " + SET_TEXT_COLOR_RED + errorMessage.getErrorMessage());
        printPrompt();
    }
}
