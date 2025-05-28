package dataaccess.mysql;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.UserDAO;
import model.UserData;

import java.sql.SQLException;

public class MySqlUserDAO implements UserDAO {

    /**
     * Constructor that creates a table in the MySQL database.
     *
     * @throws DataAccessException Throws an exception when there's an error performing database operations.
     */
    public MySqlUserDAO() throws DataAccessException {
        configureDatabase();
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
        } catch (Throwable e) {
            System.out.println("something went wrong: " + e.getMessage());
        }
    }

    @Override
    public void clearUserData() throws DataAccessException {

        // create connection
        try (var conn = DatabaseManager.getConnection()) {

            // prepare the statement
            try (var ps = conn.prepareStatement("DROP DATABASE CHESS;")) {
                ps.executeUpdate();
            }
            // reset the DB
            configureDatabase();
        } catch (SQLException | DataAccessException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

    @Override
    public void createUser(UserData userData) {

    }

    @Override
    public UserData getUser(String username) {
        return null;
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS userData (
            username VARCHAR(255) NOT NULL,
            password VARCHAR(255) NOT NULL,
            email VARCHAR(255) NOT NULL,
            PRIMARY KEY (username)
            );
            """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();

        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
