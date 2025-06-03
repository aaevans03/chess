package ui;

import server.ServerFacade;

public class ChessClient {
    private final ServerFacade server;
    private final String serverUrl;
    private String username = null;

    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public String getUsername() {
        return username;
    }
}
