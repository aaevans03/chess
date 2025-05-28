package dataaccess.mysql;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import model.AuthData;

import java.sql.SQLException;
import java.util.UUID;

public class MySqlAuthDAO implements AuthDAO {

    /**
     * Constructor that creates a table in the MySQL database.
     *
     * @throws DataAccessException Throws an exception when there's an error performing database operations.
     */
    public MySqlAuthDAO() throws DataAccessException {
        String[] createStatements = {
                """
            CREATE TABLE IF NOT EXISTS authData (
            authToken VARCHAR(255) NOT NULL,
            username VARCHAR(255) NOT NULL,
            INDEX (authToken),
            FOREIGN KEY (username) REFERENCES userData(username) ON UPDATE CASCADE ON DELETE RESTRICT
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
            var authDB = new MySqlAuthDAO();
            authDB.clearAuthData();

        } catch (Throwable e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Override
    public void clearAuthData() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("TRUNCATE TABLE authData;")) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

    @Override
    public String createAuthData(String username) throws DataAccessException {
        String newAuthToken = generateAuthToken();

        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("INSERT INTO authData VALUES (?, ?)")) {
                preparedStatement.setString(1, newAuthToken);
                preparedStatement.setString(2, username);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
        return newAuthToken;
    }

    @Override
    public AuthData getAuthData(String authToken) throws DataAccessException {

        AuthData authData;

        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT * FROM authData WHERE authToken=?")) {
                preparedStatement.setString(1, authToken);
                try (var resultSet = preparedStatement.executeQuery()) {
                    resultSet.next();
                    var returnedAuthToken = resultSet.getString("authToken");
                    var returnedUsername = resultSet.getString("username");

                    authData = new AuthData(returnedAuthToken, returnedUsername);
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
        return authData;
    }

    @Override
    public void deleteAuthData(AuthData authData) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("DELETE FROM authData WHERE authToken=?")) {
                preparedStatement.setString(1, authData.authToken());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

    public AuthData getAuthDataWithUsername(String username) throws DataAccessException {
        AuthData authData = null;
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT * FROM authData WHERE username=?")) {
                preparedStatement.setString(1, username);
                try (var resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        var returnedAuthToken = resultSet.getString("authToken");
                        var returnedUsername = resultSet.getString("username");
                        authData = new AuthData(returnedAuthToken, returnedUsername);
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
        return authData;
    }

    private String generateAuthToken() {
        return UUID.randomUUID().toString();
    }
}
