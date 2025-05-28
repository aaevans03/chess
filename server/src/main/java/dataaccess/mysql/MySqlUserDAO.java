package dataaccess.mysql;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.UserDAO;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

public class MySqlUserDAO implements UserDAO {

    /**
     * Constructor that creates a table in the MySQL database.
     *
     * @throws DataAccessException Throws an exception when there's an error performing database operations.
     */
    public MySqlUserDAO() throws DataAccessException {
        String[] createStatements = {
                """
            CREATE TABLE IF NOT EXISTS userData (
            username VARCHAR(255) NOT NULL,
            password VARCHAR(255) NOT NULL,
            email VARCHAR(255) NOT NULL,
            PRIMARY KEY (username)
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
            var userDB = new MySqlUserDAO();
            userDB.clearUserData();

            var newUser = new UserData("alex", "password", "a@xd.com");
            userDB.createUser(newUser);

            System.out.println(userDB.getUser("alex").toString());

        } catch (Throwable e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Override
    public void clearUserData() throws DataAccessException {

        final String[] clearStatements = new String[3];

        clearStatements[0] = "SET FOREIGN_KEY_CHECKS = 0;";
        clearStatements[1] = "TRUNCATE TABLE userData;";
        clearStatements[2] = "SET FOREIGN_KEY_CHECKS = 1;";

        // create connection
        try (var conn = DatabaseManager.getConnection()) {
            // prepare the statements
            for (String clearStatement : clearStatements) {
                try (var preparedStatement = conn.prepareStatement(clearStatement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException | DataAccessException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(
                    "INSERT INTO userData (username, password, email) VALUES (?, ?, ?)")) {
                preparedStatement.setString(1, userData.username());
                preparedStatement.setString(2, hashPassword(userData.password()));
                preparedStatement.setString(3, userData.email());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {

        UserData userData = null;

        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(
                    "SELECT username, password, email FROM userData WHERE username=?")) {
                preparedStatement.setString(1, username);
                try (var resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        var returnedUsername = resultSet.getString("username");
                        var returnedPassword = resultSet.getString("password");
                        var returnedEmail = resultSet.getString("email");

                        userData = new UserData(returnedUsername, returnedPassword, returnedEmail);
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to find user: %s", ex.getMessage()));
        }

        return userData;
    }

    private String hashPassword(String clearTextPassword) {
        return BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());
    }
}
