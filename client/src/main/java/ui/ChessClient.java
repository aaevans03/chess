package ui;

import server.ResponseException;
import server.ServerFacade;

import java.util.Arrays;
import java.util.HashMap;

import static ui.EscapeSequences.*;

public class ChessClient {
    private final ServerFacade server;
    private final String serverUrl;
    private String currentUsername = null;
    private String currentAuthToken = null;
    private ClientState clientState = ClientState.PRE_LOGIN;
    private int gameIterator = 1;
    private HashMap<Integer, Integer> gameMap;

    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.gameMap = new HashMap<>();
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
            return SET_TEXT_COLOR_RED + "  " + ex.getMessage();
        }
    }

    private String preLogin(String cmd, String[] params) throws ResponseException {
        return switch (cmd) {
            case "register", "r" -> register(params);
            case "login", "l" -> login(params);
            case "quit", "q" -> "quit";
            default -> help();
        };
    }

    private String register(String... params) throws ResponseException {
        if (params.length == 3) {

            var username = params[0];
            var password = params[1];
            var email = params[2];

            if (!email.contains("@") && !email.contains(".")) {
                throw new ResponseException(400, "Please enter a valid email address.");
            }

            var result = server.register(username, password, email);

            currentUsername = result.username();
            currentAuthToken = result.authToken();

            clientState = ClientState.POST_LOGIN;
            return String.format("Welcome to chess, %s!", currentUsername);
        }
        throw new ResponseException(400, syntaxErrorFormatter(CommandSyntax.REGISTER));
    }

    private String login(String... params) throws ResponseException {
        if (params.length == 2) {
            var username = params[0];
            var password = params[1];

            var result = server.login(username, password);

            currentUsername = result.username();
            currentAuthToken = result.authToken();

            clientState = ClientState.POST_LOGIN;
            return String.format("Welcome back to chess, %s!", currentUsername);
        }
        throw new ResponseException(400, syntaxErrorFormatter(CommandSyntax.LOGIN));
    }

    private String postLogin(String cmd, String[] params) throws ResponseException {
        return switch (cmd) {
            case "create", "c" -> create(params);
            case "logout", "l" -> logout(params);
            case "quit", "q" -> "quit";
            default -> help();
        };
    }

    private String logout(String... params) throws ResponseException {
        if (params.length == 0) {
            server.logout(currentAuthToken);

            currentAuthToken = null;
            currentUsername = null;

            clientState = ClientState.PRE_LOGIN;

            return "Successfully logged out";
        }
        throw new ResponseException(400, syntaxErrorFormatter(CommandSyntax.LOGOUT));
    }

    private String create(String... params) throws ResponseException {
        if (params.length == 1) {
            var gameName = params[0];

            var response = server.create(currentAuthToken, gameName);

            gameMap.put(gameIterator, response);
            var gameId = gameIterator;
            gameIterator++;

            return String.format("Game successfully created with ID %s", gameId);
        }
        throw new ResponseException(400, syntaxErrorFormatter(CommandSyntax.CREATE));
    }

    private String gameplay(String cmd, String[] params) {
        return switch (cmd) {
            case "exit" -> "exit";
            default -> help();
        };
    }

    private String help() {
        var output = "";

        switch (clientState) {
            case PRE_LOGIN -> {
                output += SET_TEXT_COLOR_MAGENTA + "  " + CommandSyntax.REGISTER;
                output += SET_TEXT_COLOR_LIGHT_GREY + " - create a new account\n";
                output += SET_TEXT_COLOR_MAGENTA + "  " + CommandSyntax.LOGIN;
                output += SET_TEXT_COLOR_LIGHT_GREY + " - login with an existing account\n";
                output += SET_TEXT_COLOR_MAGENTA + "  " + CommandSyntax.QUIT;
                output += SET_TEXT_COLOR_LIGHT_GREY + " - quit the program\n";
                output += SET_TEXT_COLOR_MAGENTA + "  " + CommandSyntax.HELP;
                output += SET_TEXT_COLOR_LIGHT_GREY + " - display list of commands\n";
            }
            case POST_LOGIN -> {
                output += SET_TEXT_COLOR_MAGENTA + "  " + CommandSyntax.CREATE;
                output += SET_TEXT_COLOR_LIGHT_GREY + " - create a new chess game with specified name\n";
                output += SET_TEXT_COLOR_MAGENTA + "  " + CommandSyntax.LIST;
                output += SET_TEXT_COLOR_LIGHT_GREY + " - view list of games\n";
                output += SET_TEXT_COLOR_MAGENTA + "  " + CommandSyntax.JOIN;
                output += SET_TEXT_COLOR_LIGHT_GREY + " - join a game as specified color\n";
                output += SET_TEXT_COLOR_MAGENTA + "  " + CommandSyntax.OBSERVE;
                output += SET_TEXT_COLOR_LIGHT_GREY + " - observe a game\n";
                output += SET_TEXT_COLOR_MAGENTA + "  " + CommandSyntax.LOGOUT;
                output += SET_TEXT_COLOR_LIGHT_GREY + " - log out of chess\n";
                output += SET_TEXT_COLOR_MAGENTA + "  " + CommandSyntax.QUIT;
                output += SET_TEXT_COLOR_LIGHT_GREY + " - quit the program\n";
                output += SET_TEXT_COLOR_MAGENTA + "  " + CommandSyntax.HELP;
                output += SET_TEXT_COLOR_LIGHT_GREY + " - display list of commands\n";
            }
            case GAMEPLAY -> {
                output += SET_TEXT_COLOR_MAGENTA + "  " + CommandSyntax.EXIT;
                output += SET_TEXT_COLOR_LIGHT_GREY + " - exit current game\n";
            }
        }

        return output;
    }

    private String syntaxErrorFormatter(String message) {
        return SET_TEXT_UNDERLINE + "Expected" + RESET_TEXT_UNDERLINE + ": " + message;
    }

    public String getCurrentUsername() {
        return currentUsername != null ? currentUsername : "Not logged in";
    }
}
