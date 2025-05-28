package dataaccess;

import dataaccess.mysql.MySqlAuthDAO;
import dataaccess.mysql.MySqlGameDAO;
import dataaccess.mysql.MySqlUserDAO;
import model.AuthData;
import model.UserData;
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

        MySqlTestHelper.createDummyUsers();
    }

    @AfterAll
    static void tearDown() throws DataAccessException {
        new MySqlAuthDAO().clearAuthData();
        new MySqlUserDAO().clearUserData();
    }

    @Test
    void clearAuthData() throws DataAccessException {
        try {
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
            var authTokens = new String[5];

            for (int i = 1; i <= 5; i++) {
                authTokens[i - 1] = (authDB.createAuthData("user" + i));
            }

            Assertions.assertEquals(5, MySqlTestHelper.countTableEntries("authData"));

            for (int i = 1; i <= 5; i++) {
                var authData = authDB.getAuthDataWithUsername("user" + i);
                Assertions.assertNotNull(authData);
                Assertions.assertEquals(authTokens[i - 1], authData.authToken());
                Assertions.assertEquals("user" + i, authData.username());
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
    void getAuthData() throws DataAccessException {
        userDB.createUser(new UserData("newUSER", "myPASSWORD", "e@mail.net"));
        var authToken = authDB.createAuthData("newUSER");

        var authData = authDB.getAuthDataWithUsername("newUSER");
        Assertions.assertNotNull(authData);
        Assertions.assertEquals(authToken, authData.authToken());
        Assertions.assertEquals("newUSER", authData.username());
    }

    @Test
    void getAuthDataFail() throws DataAccessException {
        Assertions.assertNull(authDB.getAuthData("fake-auth-token1"));
        Assertions.assertNull(authDB.getAuthData("fake-auth-token2"));
        Assertions.assertNull(authDB.getAuthData("fake-auth-token3"));
    }

    @Test
    void deleteAuthData() throws DataAccessException {
        userDB.createUser(new UserData("mynameisbob", "thisismyp@ssword", "my@mail.org"));
        var authToken = authDB.createAuthData("mynameisbob");

        Assertions.assertDoesNotThrow(() -> authDB.deleteAuthData(new AuthData(authToken, "mynameisbob")));
        Assertions.assertNull(authDB.getAuthDataWithUsername("mynameisbob"));
    }

    @Test
    void deleteAuthDataFail() {
        var fakeUser = new AuthData("fake-auth-token-xd", "myname");
        Assertions.assertThrows(DataAccessException.class, () -> authDB.deleteAuthData(fakeUser));
    }
}