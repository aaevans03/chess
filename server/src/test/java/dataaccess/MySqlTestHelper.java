package dataaccess;

import dataaccess.mysql.MySqlUserDAO;
import model.UserData;

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

    public static int countTableEntries(String table) throws SQLException, DataAccessException {
        int rowCount;
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT COUNT(username) FROM " + table)) {
                try (var resultSet = preparedStatement.executeQuery()) {
                    resultSet.next();
                    rowCount = resultSet.getInt(1);
                }
            }
        }
        return rowCount;
    }
}
