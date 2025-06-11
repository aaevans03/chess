package dataaccess.mysql;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.GameDAO;
import model.GameData;
import server.ObjectEncoderDecoder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

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

            System.out.println(gameDB.createGame("hi"));
            System.out.println(gameDB.createGame("yo"));
            System.out.println(gameDB.createGame("me"));

            System.out.println(gameDB.listGames());

            System.out.println(gameDB.getGame(1));
            System.out.println(gameDB.getGame(2));
            System.out.println(gameDB.getGame(3));

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
    public ArrayList<GameData> listGames() throws DataAccessException {

        var gameList = new ArrayList<GameData>();

        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT * from gameData;")) {
                try (var resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        gameList.add(parseGame(resultSet));
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
        return gameList;
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        var newGame = new ChessGame();
        var encodedGame = new ObjectEncoderDecoder().encode(newGame);

        if (Objects.equals(gameName, "")) {
            throw new DataAccessException("Game name cannot be empty");
        }

        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("INSERT INTO gameData (whiteUsername," +
                    "blackUsername, gameName, game) VALUES (?, ?, ?, ?)", RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, null);
                preparedStatement.setString(2, null);
                preparedStatement.setString(3, gameName);
                preparedStatement.setString(4, encodedGame);

                preparedStatement.executeUpdate();

                var resultSet = preparedStatement.getGeneratedKeys();
                var gameID = 0;
                if (resultSet.next()) {
                    gameID = resultSet.getInt(1);
                }
                return gameID;
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT * FROM gameData WHERE gameID=?;")) {
                preparedStatement.setInt(1, gameID);
                try (var resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return parseGame(resultSet);
                    }
                    throw new DataAccessException("No game found");
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

    @Override
    public void updateGame(int gameID, String username, ChessGame.TeamColor playerColor, ChessGame game) throws DataAccessException {
        GameData currentGame = getGame(gameID);

        // update the game
        if (game != null) {
            updateGameGameObj(currentGame, game);
        }
        // update the usernames
        else if (username != null) {
            updateGameUser(gameID, username, playerColor);
        } else {
            throw new DataAccessException("no input provided");
        }
    }

    private void updateGameGameObj(GameData gameData, ChessGame game) throws DataAccessException {
        var encodedGame = new ObjectEncoderDecoder().encode(game);
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("UPDATE gameData SET game=? WHERE gameID=?;")) {
                preparedStatement.setString(1, encodedGame);
                preparedStatement.setInt(2, gameData.gameID());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

    private void updateGameUser(int gameID, String username, ChessGame.TeamColor color) throws DataAccessException {
        boolean removeUsername = username.equals("SET NULL");
        String sql = setUpdateGameUserSql(color, removeUsername);

        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(sql)) {

                if (removeUsername) {
                    preparedStatement.setInt(1, gameID);
                } else {
                    preparedStatement.setString(1, username);
                    preparedStatement.setInt(2, gameID);
                }
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

    private static String setUpdateGameUserSql(ChessGame.TeamColor color, boolean removeUsername) {
        String sql;

        if (removeUsername) {
            sql = ("UPDATE gameData SET " +
                    (color == ChessGame.TeamColor.WHITE ? "whiteUsername" : "blackUsername") + "=NULL WHERE gameID=?");
        } else {
            sql = ("UPDATE gameData SET " +
                    (color == ChessGame.TeamColor.WHITE ? "whiteUsername" : "blackUsername") + "=? WHERE gameID=? AND " +
                    (color == ChessGame.TeamColor.WHITE ? "whiteUsername" : "blackUsername") + " IS NULL;");
        }
        return sql;
    }

    private GameData parseGame(ResultSet resultSet) throws SQLException {
        var returnedGameID = resultSet.getInt("gameID");
        var returnedWhiteUsername = resultSet.getString("whiteUsername");
        var returnedBlackUsername = resultSet.getString("blackUsername");
        var returnedGameName = resultSet.getString("gameName");
        var returnedGame = resultSet.getString("game");

        var convertedGame = new ObjectEncoderDecoder().decode(returnedGame, ChessGame.class);

        return new GameData(returnedGameID, returnedWhiteUsername,
                returnedBlackUsername, returnedGameName, (ChessGame) convertedGame);
    }
}
