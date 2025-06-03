package ui;

import server.ResponseException;
import server.ServerFacade;

import java.util.Arrays;

import static ui.EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY;
import static ui.EscapeSequences.SET_TEXT_COLOR_MAGENTA;

public class ChessClient {
    private final ServerFacade server;
    private final String serverUrl;
    private String username = null;
    private ClientState clientState = ClientState.PRE_LOGIN;

    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public String evaluateCommand(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);

            return switch (clientState) {
                case PRE_LOGIN -> preLogin(cmd, params);
                case POST_LOGIN -> postLogin(cmd, params);
                case GAMEPLAY -> gameplay(cmd, params);
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    private String preLogin(String cmd, String[] params) throws ResponseException {
        return switch (cmd) {
            case "register", "r" -> register(params);
            case "login", "l" -> login(params);
            case "quit" -> "quit";
            default -> help();
        };
    }

    private String postLogin(String cmd, String[] params) {
        return switch (cmd) {
            default -> help();
        };
    }

    private String gameplay(String cmd, String[] params) {
        return switch (cmd) {
            case "game" -> "game";
            default -> help();
        };
    }

    private String register(String... params) {
        return "register method";
    }

    private String login(String... params) {
        return "login method";
    }

    public String help() {
        var output = "";

        switch (clientState) {
            case PRE_LOGIN -> {
                output += SET_TEXT_COLOR_MAGENTA + "  register <USERNAME> <PASSWORD> <EMAIL>";
                output += SET_TEXT_COLOR_LIGHT_GREY + "  - create a new account\n";
                output += SET_TEXT_COLOR_MAGENTA + "  login <USERNAME> <PASSWORD>";
                output += SET_TEXT_COLOR_LIGHT_GREY + "  - login with an existing account\n";
                output += SET_TEXT_COLOR_MAGENTA + "  quit";
                output += SET_TEXT_COLOR_LIGHT_GREY + "  - quit the program\n";
                output += SET_TEXT_COLOR_MAGENTA + "  help";
                output += SET_TEXT_COLOR_LIGHT_GREY + "  - display list of commands\n";
            }
            case POST_LOGIN -> {

            }
            case GAMEPLAY -> {

            }
        }

        return output;
    }

    public String getUsername() {
        return username != null ? username : "No current user";
    }
}
