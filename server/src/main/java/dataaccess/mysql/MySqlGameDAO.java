package dataaccess.mysql;

import chess.ChessGame;
import dataaccess.GameDAO;
import model.GameData;

import java.util.ArrayList;

public class MySqlGameDAO implements GameDAO {
    @Override
    public void clearGameData() {

    }

    @Override
    public ArrayList<GameData> listGames() {
        return null;
    }

    @Override
    public int createGame(String gameName) {
        return 0;
    }

    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    @Override
    public void updateGame(int gameID, String username, ChessGame.TeamColor playerColor) {

    }
}
