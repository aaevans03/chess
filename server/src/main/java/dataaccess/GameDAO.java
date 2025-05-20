package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;

public interface GameDAO {
    void clearGameData();
    ArrayList<GameData> listGames();
    int createGame(String gameName);
    GameData getGame(int gameID);
    void updateGame(int gameID, String username, ChessGame.TeamColor playerColor);
}
