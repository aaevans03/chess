package server;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public AuthPair register(String username, String password, String email) {
        // return username and an authToken
        return null;
    }

    public AuthPair login(String username, String password) {
        // return username and an authToken
        return null;
    }

    public void logout(String authToken) {
        // logout
    }

    public ArrayList<GameData> listGames(String authToken) {
        // return list of games
        return null;
    }

    public int create(String authToken, String gameName) {
        // return game ID
        return 1;
    }

    public void join(String authToken, ChessGame.TeamColor playerColor, int gameID) {
        // join a game
    }
}
