package dataaccess;

import dataaccess.mysql.MySqlAuthDAO;
import dataaccess.mysql.MySqlGameDAO;
import dataaccess.mysql.MySqlUserDAO;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

class MySqlAuthDAOTests {

    MySqlUserDAO userDB;
    MySqlAuthDAO authDB;

    @BeforeEach
    void setUp() throws DataAccessException {
        new MySqlGameDAO().clearGameData();

        authDB = new MySqlAuthDAO();
        authDB.clearAuthData();

        userDB = new MySqlUserDAO();
        userDB.clearUserData();
    }

    @AfterAll
    static void tearDown() throws DataAccessException {
        new MySqlAuthDAO().clearAuthData();
        new MySqlUserDAO().clearUserData();
    }

    @Test
    void clearAuthData() throws DataAccessException {
        try {
            MySqlTestHelper.createDummyUsers();

            authDB.createAuthData("user1");
            authDB.createAuthData("user2");
            authDB.createAuthData("user3");
            authDB.createAuthData("user4");
            authDB.createAuthData("user5");

            authDB.clearAuthData();

            Assertions.assertNull(authDB.getAuthDataWithUsername("user1"));
            Assertions.assertNull(authDB.getAuthDataWithUsername("user2"));
            Assertions.assertNull(authDB.getAuthDataWithUsername("user3"));
            Assertions.assertNull(authDB.getAuthDataWithUsername("user4"));
            Assertions.assertNull(authDB.getAuthDataWithUsername("user5"));

            Assertions.assertEquals(0, MySqlTestHelper.countTableEntries("authData"));

        } catch (SQLException e) {
            throw new DataAccessException("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    void createAuthData() throws DataAccessException {
        try {
            MySqlTestHelper.createDummyUsers();

            for (int i = 1; i <= 5; i++) {
                authDB.createAuthData("user" + i);
            }

            Assertions.assertEquals(5, MySqlTestHelper.countTableEntries("authData"));

            for (int i = 1; i <= 5; i++) {
                Assertions.assertNotNull(authDB.getAuthDataWithUsername("user" + i));
            }

        } catch (SQLException e) {
            throw new DataAccessException("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    void createAuthDataFail() {
        // provide non-existent users
        Assertions.assertThrows(DataAccessException.class, () -> authDB.createAuthData("nonexistentUSER1"));
        Assertions.assertThrows(DataAccessException.class, () -> authDB.createAuthData("nonexistentUSER2"));
        Assertions.assertThrows(DataAccessException.class, () -> authDB.createAuthData("nonexistentUSER3"));
    }

    @Test
    void getAuthData() {
        
    }

    @Test
    void getAuthDataFail() {

    }

    @Test
    void deleteAuthData() {
    }
}