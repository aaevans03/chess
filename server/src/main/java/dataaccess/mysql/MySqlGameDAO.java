package dataaccess.mysql;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.GameDAO;
import model.GameData;

import java.sql.SQLException;
import java.util.ArrayList;

public class MySqlGameDAO implements GameDAO {

    /**
     * Constructor that creates a table in the MySQL database.
     *
     * @throws DataAccessException Throws an exception when there's an error performing database operations.
     */
    public MySqlGameDAO() throws DataAccessException {
        String[] createStatements = {
                """
                CREATE TABLE IF NOT EXISTS gameData (
                    gameID INT NOT NULL AUTO_INCREMENT,
                    whiteUsername VARCHAR(255) DEFAULT NULL,
                    blackUsername VARCHAR(255) DEFAULT NULL,
                    gameName VARCHAR(255) NOT NULL,
                    game TEXT NOT NULL,
                    PRIMARY KEY (gameID),
                    FOREIGN KEY (whiteUsername) REFERENCES userData(username) ON UPDATE CASCADE ON DELETE SET NULL,
                    FOREIGN KEY (blackUsername) REFERENCES userData(username) ON UPDATE CASCADE ON DELETE SET NULL
                );
                """
        };
        DatabaseManager.configureDatabase(createStatements);
    }

    /**
     * Main function for testing.
     *
     * @param args Any arguments that need to be provided
     */
    public static void main(String[] args) {
        try {
            var gameDB = new MySqlGameDAO();
            gameDB.clearGameData();

        } catch (Throwable e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Override
    public void clearGameData() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("TRUNCATE TABLE gameData;")) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
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
    public void updateGame(int gameID, String username, ChessGame.TeamColor playerColor, ChessGame game) {

    }
}
