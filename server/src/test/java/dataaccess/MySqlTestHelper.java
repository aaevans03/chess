package dataaccess;

import dataaccess.mysql.MySqlUserDAO;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

public class MySqlTestHelper {
    public static void createDummyUsers() throws DataAccessException {
        var userDB = new MySqlUserDAO();
        userDB.createUser(new UserData("user1", "password", "e@mail.com"));
        userDB.createUser(new UserData("user2", "password", "f@mail.com"));
        userDB.createUser(new UserData("user3", "password", "g@mail.com"));
        userDB.createUser(new UserData("user4", "password", "h@mail.com"));
        userDB.createUser(new UserData("user5", "password", "i@mail.com"));
    }

    public static int countTableEntries(String table, String columnToCount) throws SQLException, DataAccessException {
        int rowCount;
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT COUNT(" + columnToCount + ") FROM " + table)) {
                try (var resultSet = preparedStatement.executeQuery()) {
                    resultSet.next();
                    rowCount = resultSet.getInt(1);
                }
            }
        }
        return rowCount;
    }

    public static void checkUserData(UserData expectedUserData, UserData retrievedUserData) throws SQLException, DataAccessException {
        Assertions.assertEquals(expectedUserData.username(), retrievedUserData.username());
        Assertions.assertTrue(BCrypt.checkpw(expectedUserData.password(), retrievedUserData.password()));
        Assertions.assertEquals(expectedUserData.email(), retrievedUserData.email());
    }
}
