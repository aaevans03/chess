package dataaccess.memory;

import chess.ChessGame;
import dataaccess.GameDAO;
import model.GameData;

import java.util.ArrayList;
import java.util.TreeMap;

public class MemoryGameDAO implements GameDAO {
    // A map of game data: find GameData given a game ID
    TreeMap<Integer, GameData> memoryGameData = new TreeMap<>();
    int gameIterator = 1;

    public MemoryGameDAO() {
        // add values for testing
    }

    @Override
    public void clearGameData() {
        memoryGameData.clear();
    }

    @Override
    public ArrayList<GameData> listGames() {
        return new ArrayList<>(memoryGameData.values());
    }

    @Override
    public int createGame(String gameName) {
        int gameId = gameIterator;
        memoryGameData.put(gameId, new GameData(gameId, null, null, gameName, new ChessGame()));
        gameIterator++;
        return gameId;
    }

    @Override
    public GameData getGame(int gameID) {
        return memoryGameData.get(gameID);
    }

    @Override
    public void updateGame(int gameID, String username, ChessGame.TeamColor playerColor) {
        GameData currentGame = memoryGameData.get(gameID);
        // remove the current game state from memory
        memoryGameData.remove(gameID);

        if (playerColor == ChessGame.TeamColor.WHITE) {
            GameData newGame = new GameData(gameID, username, currentGame.blackUsername(), currentGame.gameName(), currentGame.game());
            memoryGameData.put(gameID, newGame);
        } else if (playerColor == ChessGame.TeamColor.BLACK) {
            GameData newGame = new GameData(gameID, currentGame.whiteUsername(), username, currentGame.gameName(), currentGame.game());
            memoryGameData.put(gameID, newGame);
        }
    }
}
