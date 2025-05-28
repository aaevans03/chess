package dataaccess;

import dataaccess.mysql.MySqlUserDAO;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

class MySqlUserDAOTests {

    MySqlUserDAO userDB;

    @BeforeEach
    void setUp() throws DataAccessException {
        userDB = new MySqlUserDAO();
        userDB.clearUserData();
    }

    @Test
    void clearUserData() {
        try {
            userDB.createUser(new UserData("user1", "password", "e@mail.com"));
            userDB.createUser(new UserData("user2", "password", "f@mail.com"));
            userDB.createUser(new UserData("user3", "password", "g@mail.com"));
            userDB.createUser(new UserData("user4", "password", "h@mail.com"));
            userDB.createUser(new UserData("user5", "password", "i@mail.com"));

            userDB.clearUserData();

            Assertions.assertNull(userDB.getUser("user1"));
            Assertions.assertNull(userDB.getUser("user2"));
            Assertions.assertNull(userDB.getUser("user3"));
            Assertions.assertNull(userDB.getUser("user4"));
            Assertions.assertNull(userDB.getUser("user5"));

            Assertions.assertEquals(0, countTableEntries());

        } catch (DataAccessException | SQLException e) {
            System.out.println("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    void createUser() {
        try {
            var userData = new UserData("jill", "mySecretPassword", "jill@mail.com");
            userDB.createUser(userData);

            var retrievedUserData = userDB.getUser("jill");

            Assertions.assertEquals(userData.username(), retrievedUserData.username());
            Assertions.assertTrue(BCrypt.checkpw("mySecretPassword", retrievedUserData.password()));
            Assertions.assertEquals(userData.email(), retrievedUserData.email());
            Assertions.assertEquals(1, countTableEntries());

        } catch (DataAccessException | SQLException e) {
            System.out.println("Exception occurred: " + e.getMessage());
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
    void getUser() {

    }

    @Test
    void getNonExistentUser() {

    }

    private int countTableEntries() throws SQLException, DataAccessException {
        int rowCount;
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT COUNT(username) FROM userData")) {
                try (var resultSet = preparedStatement.executeQuery()) {
                    resultSet.next();
                    rowCount = resultSet.getInt(1);
                }
            }
        }
        return rowCount;
    }
}