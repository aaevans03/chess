package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;

public interface GameDAO {
    void clearGameData() throws DataAccessException;

    ArrayList<GameData> listGames() throws DataAccessException;

    int createGame(String gameName) throws DataAccessException;

    GameData getGame(int gameID);

    void updateGame(int gameID, String username, ChessGame.TeamColor playerColor, ChessGame game);
}
