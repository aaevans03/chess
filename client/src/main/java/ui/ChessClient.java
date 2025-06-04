package ui;

import chess.ChessBoard;
import chess.ChessGame;
import model.GameData;
import server.ResponseException;
import server.ServerFacade;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

import static ui.EscapeSequences.*;

public class ChessClient {
    private final ServerFacade server;
    private final String serverUrl;
    private String currentUsername = null;
    private String currentAuthToken = null;
    private ClientState clientState = ClientState.PRE_LOGIN;
    private int gameIterator = 1;
    private final HashMap<Integer, Integer> gameMap;

    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.gameMap = new HashMap<>();
    }

    public String evaluateCommand(String input) {
        try {
            var tokens = input.split(" ");
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
            case "help", "h" -> CommandSyntax.help(ClientState.PRE_LOGIN);
            default -> SET_TEXT_COLOR_RED + "  Unknown command. List of valid commands:\n"
                    + CommandSyntax.help(ClientState.PRE_LOGIN);
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
            return String.format(SET_TEXT_COLOR_BLUE + "  Welcome to chess, %s!", currentUsername);
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
            return String.format(SET_TEXT_COLOR_BLUE + "  Welcome back to chess, %s!", currentUsername);
        }
        throw new ResponseException(400, syntaxErrorFormatter(CommandSyntax.LOGIN));
    }

    private String postLogin(String cmd, String[] params) throws ResponseException {
        return switch (cmd) {
            case "create", "c" -> create(params);
            case "list", "l" -> list(params);
            case "join", "j" -> join(params);
            case "observe", "o" -> observe(params);
            case "logout", "log" -> logout(params);
            case "quit", "q" -> "quit";
            case "help", "h" -> CommandSyntax.help(ClientState.POST_LOGIN);
            default -> SET_TEXT_COLOR_RED + "  Unknown command. List of valid commands:\n"
                    + CommandSyntax.help(ClientState.POST_LOGIN);
        };
    }

    private String logout(String... params) throws ResponseException {
        if (params.length == 0) {
            server.logout(currentAuthToken);

            currentAuthToken = null;
            currentUsername = null;

            clientState = ClientState.PRE_LOGIN;

            return SET_TEXT_COLOR_BLUE + "  Successfully logged out";
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

            return String.format(SET_TEXT_COLOR_BLUE + "  Game successfully created with ID %s", gameId);
        }
        throw new ResponseException(400, syntaxErrorFormatter(CommandSyntax.CREATE));
    }

    /**
     * Retrieve list of games from the database and number them in the client.
     *
     * @param params If any parameters are provided, it's invalid input
     * @return List of games
     */
    private String list(String... params) throws ResponseException {
        if (params.length == 0) {

            var response = server.listGames(currentAuthToken);

            if (response.isEmpty()) {
                gameMap.clear();
                gameIterator = 1;
                return SET_TEXT_COLOR_BLUE + "  No games active, create one of your own!";
            }

            // Map games previously not stored in client to new IDs
            var responseMap = new HashMap<Integer, GameData>();
            for (var responseItem : response) {
                responseMap.put(responseItem.gameID(), responseItem);

                // Assign new client IDs to the returned server IDs
                if (!gameMap.containsValue(responseItem.gameID())) {
                    gameMap.put(gameIterator, responseItem.gameID());
                    gameIterator++;
                }
            }

            // Write output
            StringBuilder output = new StringBuilder();

            for (int i = 1; i <= gameMap.size(); i++) {
                var dbGameID = gameMap.get(i);

                output.append(SET_TEXT_BOLD + "   ID: ");
                output.append(RESET_TEXT_BOLD_FAINT + SET_TEXT_COLOR_BLUE).append(i);

                output.append(RESET_TEXT_COLOR + SET_TEXT_BOLD + ", Game Name: ");
                output.append(RESET_TEXT_BOLD_FAINT + SET_TEXT_COLOR_BLUE)
                        .append(responseMap.get(dbGameID).gameName());

                var whiteUsername = responseMap.get(dbGameID).whiteUsername();
                output.append(RESET_TEXT_COLOR + SET_TEXT_BOLD + ", White: ");
                output.append(RESET_TEXT_BOLD_FAINT + SET_TEXT_COLOR_BLUE)
                        .append(whiteUsername == null ? "none" : whiteUsername);

                var blackUsername = responseMap.get(dbGameID).blackUsername();
                output.append(RESET_TEXT_COLOR + SET_TEXT_BOLD + ", Black: ");
                output.append(RESET_TEXT_BOLD_FAINT + SET_TEXT_COLOR_BLUE)
                        .append(blackUsername == null ? "none" : blackUsername);

                output.append(RESET_TEXT_COLOR + "\n");
            }

            return output.toString();
        }
        throw new ResponseException(400, syntaxErrorFormatter(CommandSyntax.LIST));
    }

    private String join(String... params) throws ResponseException {
        if (params.length == 2) {

            int id;
            int serverGameId;

            // User enters an invalid game ID, or joins a game that doesn't exist
            try {
                id = Integer.parseInt(params[0]);
                serverGameId = gameMap.get(id);
            } catch (NumberFormatException | NullPointerException ex) {
                throw new ResponseException(400, "Invalid game ID entered, try again.");
            }

            var color = params[1];

            ChessGame.TeamColor teamColor;

            // Join the game
            if (Objects.equals(color, "white") || Objects.equals(color, "w")
                    || Objects.equals(color, "WHITE")) {
                teamColor = ChessGame.TeamColor.WHITE;
            } else if (Objects.equals(color, "black") || Objects.equals(color, "b")
                    || Objects.equals(color, "BLACK")) {
                teamColor = ChessGame.TeamColor.BLACK;
            } else {
                throw new ResponseException(400, syntaxErrorFormatter(CommandSyntax.JOIN));
            }

            server.join(currentAuthToken, teamColor, serverGameId);
            // PHASE 6: change client state to gameplay. clientState = ClientState.GAMEPLAY;

            var board = new ChessBoard();
            board.resetBoard();

            var drawnBoard = new BoardDrawer().drawBoard(teamColor, board);

            return String.format(SET_TEXT_COLOR_BLUE + "  Joined game %d.\n\n%s", id, drawnBoard);
        }

        throw new ResponseException(400, syntaxErrorFormatter(CommandSyntax.JOIN));
    }

    private String observe(String... params) throws ResponseException {
        if (params.length == 1) {
            int id;
            int serverGameId;

            // User enters an invalid game ID, or joins a game that doesn't exist
            try {
                id = Integer.parseInt(params[0]);
                serverGameId = gameMap.get(id);
            } catch (NumberFormatException | NullPointerException ex) {
                throw new ResponseException(400, "Invalid game ID entered, try again.");
            }

            // PHASE 6: change client state to gameplay. clientState = ClientState.GAMEPLAY;

            var board = new ChessBoard();
            board.resetBoard();

            var drawnBoard = new BoardDrawer().drawBoard(ChessGame.TeamColor.WHITE, board);

            return String.format(SET_TEXT_COLOR_BLUE + "  Observing game %d.\n\n%s", id, drawnBoard);
        }
        throw new ResponseException(400, syntaxErrorFormatter(CommandSyntax.OBSERVE));
    }

    private String gameplay(String cmd, String[] params) throws ResponseException {
        return switch (cmd) {
            case "exit", "e" -> exit(params);
            case "help", "h" -> CommandSyntax.help(ClientState.GAMEPLAY);
            default -> SET_TEXT_COLOR_RED + "  Unknown command. List of valid commands:\n"
                    + CommandSyntax.help(ClientState.GAMEPLAY);
        };
    }

    private String exit(String... params) throws ResponseException {
        if (params.length == 0) {
            clientState = ClientState.POST_LOGIN;
            return SET_TEXT_COLOR_BLUE + "  Exited game.";
        }
        throw new ResponseException(400, syntaxErrorFormatter(CommandSyntax.EXIT));
    }

    private String syntaxErrorFormatter(String message) {
        return SET_TEXT_UNDERLINE + "Expected" + RESET_TEXT_UNDERLINE + ": " + message;
    }

    public String getCurrentUsername() {
        return currentUsername != null ? currentUsername : "Not logged in";
    }
}
