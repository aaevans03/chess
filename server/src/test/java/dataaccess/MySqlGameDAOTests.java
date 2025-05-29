package dataaccess;

import chess.ChessGame;
import dataaccess.mysql.MySqlAuthDAO;
import dataaccess.mysql.MySqlGameDAO;
import dataaccess.mysql.MySqlUserDAO;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;

class MySqlGameDAOTests {

    MySqlUserDAO userDB;
    MySqlAuthDAO authDB;
    MySqlGameDAO gameDB;

    @BeforeEach
    void setUp() throws DataAccessException {
        gameDB = new MySqlGameDAO();
        gameDB.clearGameData();

        authDB = new MySqlAuthDAO();
        authDB.clearAuthData();

        userDB = new MySqlUserDAO();
        userDB.clearUserData();

        MySqlTestHelper.createDummyUsers();
    }

    @Test
    void clearGameData() throws DataAccessException {
        try {
            gameDB.createGame("game1");
            gameDB.createGame("game2");
            gameDB.createGame("game3");
            gameDB.createGame("game4");
            gameDB.createGame("game5");

            gameDB.clearGameData();

            for (int i = 1; i <= 5; i++) {
                int finalI = i;
                Assertions.assertThrows(DataAccessException.class, () -> {
                    gameDB.getGame(finalI);
                });
            }

            Assertions.assertEquals(0, MySqlTestHelper.countTableEntries("gameData", "gameID"));

        } catch (SQLException e) {
            throw new DataAccessException("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    void listGames() throws DataAccessException {
        try {
            ArrayList<GameData> expectedList = createDummyGames();

            ArrayList<GameData> actualList = gameDB.listGames();

            Assertions.assertEquals(expectedList, actualList);
            Assertions.assertEquals(3, MySqlTestHelper.countTableEntries("gameData", "gameID"));

        } catch (SQLException e) {
            throw new DataAccessException("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    void listGamesFail() throws DataAccessException {
        createDummyGames();
        gameDB.clearGameData();

        Assertions.assertEquals(new ArrayList<>(), gameDB.listGames());
    }

    @Test
    void createGame() throws DataAccessException {
        var expectedData1 = new GameData(1, null, null, "testGame1", new ChessGame());
        var expectedData2 = new GameData(2, null, null, "testGame2", new ChessGame());
        var expectedData3 = new GameData(3, null, null, "testGame3", new ChessGame());

        gameDB.createGame("testGame1");
        gameDB.createGame("testGame2");
        gameDB.createGame("testGame3");

        Assertions.assertEquals(expectedData1, gameDB.getGame(1));
        Assertions.assertEquals(expectedData2, gameDB.getGame(2));
        Assertions.assertEquals(expectedData3, gameDB.getGame(3));
    }

    @Test
    void createGameFail() {
        Assertions.assertThrows(DataAccessException.class, () -> gameDB.createGame(""));
    }

    @Test
    void getGame() throws DataAccessException {
        var expectedList = createDummyGames();

        for (var expectedGame : expectedList) {
            Assertions.assertEquals(expectedGame, gameDB.getGame(expectedGame.gameID()));
        }
    }

    @Test
    void getGameFail() {
        Assertions.assertThrows(DataAccessException.class, () -> System.out.println(gameDB.getGame(1)));
    }

    @Test
    void updateGame() throws DataAccessException {
        createDummyGames();
        var expectedGame = new GameData(2, "user3", "user5", "game2", new ChessGame());

        gameDB.updateGame(2, "user3", ChessGame.TeamColor.WHITE, null);

        Assertions.assertEquals(expectedGame, gameDB.getGame(2));
    }

    @Test
    void updateGameFail() throws DataAccessException {
        createDummyGames();

        // invalid game ID
        Assertions.assertThrows(DataAccessException.class, () -> {
            gameDB.updateGame(0, "fakePlayer", ChessGame.TeamColor.WHITE, null);
        });

        // no ChessGame/username provided
        Assertions.assertThrows(DataAccessException.class, () -> {
            gameDB.updateGame(0, null, null, null);
        });

        // one game is already full
        gameDB.updateGame(1, "user1", ChessGame.TeamColor.WHITE, null);

        var expected = new GameData(1, "user4", "user2", "game1", new ChessGame());
        Assertions.assertEquals(expected, gameDB.getGame(1));
    }

    ArrayList<GameData> createDummyGames() throws DataAccessException {
        gameDB.createGame("game1");
        gameDB.createGame("game2");
        gameDB.createGame("game3");

        gameDB.updateGame(1, "user2", ChessGame.TeamColor.BLACK, null);
        gameDB.updateGame(1, "user4", ChessGame.TeamColor.WHITE, null);
        gameDB.updateGame(2, "user5", ChessGame.TeamColor.BLACK, null);
        gameDB.updateGame(3, "user1", ChessGame.TeamColor.WHITE, null);

        ArrayList<GameData> gameList = new ArrayList<>();

        gameList.add(new GameData(1, "user4", "user2", "game1", new ChessGame()));
        gameList.add(new GameData(2, null, "user5", "game2", new ChessGame()));
        gameList.add(new GameData(3, "user1", null, "game3", new ChessGame()));

        return gameList;
    }
}