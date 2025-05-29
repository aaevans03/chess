package dataaccess;

import dataaccess.mysql.MySqlAuthDAO;
import dataaccess.mysql.MySqlGameDAO;
import dataaccess.mysql.MySqlUserDAO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

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

            Assertions.assertNull(gameDB.getGame(1));
            Assertions.assertNull(gameDB.getGame(2));
            Assertions.assertNull(gameDB.getGame(3));
            Assertions.assertNull(gameDB.getGame(4));
            Assertions.assertNull(gameDB.getGame(5));

            Assertions.assertEquals(0, MySqlTestHelper.countTableEntries("gameData", "gameID"));

        } catch (SQLException e) {
            throw new DataAccessException("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    void listGames() {
    }

    @Test
    void createGame() {
    }

    @Test
    void getGame() {
    }

    @Test
    void updateGame() {
    }
}