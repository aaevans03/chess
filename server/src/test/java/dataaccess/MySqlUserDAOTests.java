package dataaccess;

import dataaccess.mysql.MySqlAuthDAO;
import dataaccess.mysql.MySqlGameDAO;
import dataaccess.mysql.MySqlUserDAO;
import model.UserData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

class MySqlUserDAOTests {

    MySqlUserDAO userDB;

    @BeforeEach
    void setUp() throws DataAccessException {
        new MySqlAuthDAO().clearAuthData();
        new MySqlGameDAO().clearGameData();
        userDB = new MySqlUserDAO();
        userDB.clearUserData();
    }

    @AfterAll
    static void tearDown() throws DataAccessException {
        new MySqlUserDAO().clearUserData();
    }

    @Test
    void clearUserData() throws DataAccessException {
        try {
            MySqlTestHelper.createDummyUsers();

            userDB.clearUserData();

            Assertions.assertNull(userDB.getUser("user1"));
            Assertions.assertNull(userDB.getUser("user2"));
            Assertions.assertNull(userDB.getUser("user3"));
            Assertions.assertNull(userDB.getUser("user4"));
            Assertions.assertNull(userDB.getUser("user5"));

            Assertions.assertEquals(0, MySqlTestHelper.countTableEntries("userData"));

        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    void createUser() throws DataAccessException {
        try {
            var userData1 = new UserData("joe", "tooSECRET", "joe@mail.com");
            var userData2 = new UserData("jill", "mySecretPassword", "jill@mail.com");
            var userData3 = new UserData("james", "EXTRAsecret", "james@mail.com");

            userDB.createUser(userData1);
            userDB.createUser(userData2);
            userDB.createUser(userData3);

            checkUserData(userData1, userDB.getUser("joe"));
            checkUserData(userData2, userDB.getUser("jill"));
            checkUserData(userData3, userDB.getUser("james"));

            Assertions.assertEquals(3, MySqlTestHelper.countTableEntries("userData"));

        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    void createExistingUser() {
        Assertions.assertThrows(DataAccessException.class, () -> {
            var userData = new UserData("aaron", "securePASSWORD", "aaron@mail.net");
            userDB.createUser(userData);
            userDB.createUser(userData);
        });
    }

    @Test
    void getUser() throws DataAccessException {
        try {
            var userData1 = new UserData("user1", "password", "e@mail.com");
            var userData2 = new UserData("user2", "password", "f@mail.com");
            var userData3 = new UserData("user3", "password", "g@mail.com");
            var userData4 = new UserData("user4", "password", "h@mail.com");
            var userData5 = new UserData("user5", "password", "i@mail.com");

            MySqlTestHelper.createDummyUsers();

            checkUserData(userData1, userDB.getUser("user1"));
            checkUserData(userData2, userDB.getUser("user2"));
            checkUserData(userData3, userDB.getUser("user3"));
            checkUserData(userData4, userDB.getUser("user4"));
            checkUserData(userData5, userDB.getUser("user5"));

        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    void getNonExistentUser() throws DataAccessException {
        Assertions.assertNull(userDB.getUser("fakeUser"));
    }

    private void checkUserData(UserData expectedUserData, UserData retrievedUserData) throws SQLException, DataAccessException {
        Assertions.assertEquals(expectedUserData.username(), retrievedUserData.username());
        Assertions.assertTrue(BCrypt.checkpw(expectedUserData.password(), retrievedUserData.password()));
        Assertions.assertEquals(expectedUserData.email(), retrievedUserData.email());
    }
}