package ui;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    private final ChessClient client;

    public Repl(String serverUrl) {
        client = new ChessClient(serverUrl);
    }

    public void run() {
        System.out.println(SET_TEXT_BOLD + SET_TEXT_COLOR_BLUE + "Welcome to Alex's Chess Client. Type \"help\" to get started.");

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.evaluateCommand(line);
                System.out.print(SET_TEXT_COLOR_RED + "   " + result);
            } catch (Throwable e) {
                System.out.print(e.toString());
            }
        }
    }

    private void printPrompt() {
        resetTextFormatting();
        var username = client.getUsername();
        System.out.print("\n[" + (username == null ? "UNKNOWN_USER" : username) + "] ");
        System.out.print(">>> ");
    }

    private void resetTextFormatting() {
        System.out.print(RESET_TEXT_BOLD_FAINT + RESET_TEXT_BLINKING);
        System.out.print(RESET_TEXT_ITALIC + RESET_TEXT_UNDERLINE);
        System.out.print(RESET_TEXT_COLOR + RESET_BG_COLOR);
    }
}
