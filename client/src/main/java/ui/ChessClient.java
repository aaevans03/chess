package ui;

import server.ResponseException;
import server.ServerFacade;

import java.util.Arrays;

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

            return switch (cmd) {
                case "name" -> getUsername();
                case "exception" -> throw new ResponseException(400, "bad input");
                case "quit" -> "quit";
                default -> "invalid input, try again";
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String getUsername() {
        return username != null ? username : "No current user";
    }
}
