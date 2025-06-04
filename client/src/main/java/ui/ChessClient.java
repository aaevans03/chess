package ui;

import model.GameData;
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
            case "list", "l" -> list(params);
            case "logout", "log" -> logout(params);
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

    /**
     * Retrieve list of games from the database and number them in the client.
     *
     * @param params If any parameters are provided, it's invalid input
     * @return
     * @throws ResponseException
     */
    private String list(String... params) throws ResponseException {
        if (params.length == 0) {

            var response = server.listGames(currentAuthToken);

            if (response.isEmpty()) {
                return "No games active, create one of your own!";
            }

//            System.out.println(SET_TEXT_COLOR_RED + "got " + response.size() + " games from server" + RESET_TEXT_COLOR);

            // Map games previously not stored in client to new IDs
            var responseMap = new HashMap<Integer, GameData>();
            for (var responseItem : response) {
                responseMap.put(responseItem.gameID(), responseItem);

                // Assign new client IDs to the returned server IDs
                if (!gameMap.containsValue(responseItem.gameID())) {
//                    System.out.println(SET_TEXT_COLOR_RED + "put server game ID " + responseItem.gameID() + " in as " + gameIterator + RESET_TEXT_COLOR);
                    gameMap.put(gameIterator, responseItem.gameID());
                    gameIterator++;
                }
            }

            // Write output
            StringBuilder output = new StringBuilder();

            for (int i = 1; i <= gameMap.size(); i++) {
                var dbGameID = gameMap.get(i);

                output.append("   ID: ");
                output.append(SET_TEXT_COLOR_BLUE).append(i);

                output.append(RESET_TEXT_COLOR + ", Game Name: ");
                output.append(SET_TEXT_COLOR_BLUE).append(responseMap.get(dbGameID).gameName());

                var whiteUsername = responseMap.get(dbGameID).whiteUsername();
                output.append(RESET_TEXT_COLOR + ", White: ");
                output.append(SET_TEXT_COLOR_BLUE).append(whiteUsername == null ? "none" : whiteUsername);

                var blackUsername = responseMap.get(dbGameID).whiteUsername();
                output.append(RESET_TEXT_COLOR + ", Black: ");
                output.append(SET_TEXT_COLOR_BLUE).append(blackUsername == null ? "none" : blackUsername);

                output.append(RESET_TEXT_COLOR + "\n");
            }

            return output.toString();
        }
        throw new ResponseException(400, syntaxErrorFormatter(CommandSyntax.LIST));
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
