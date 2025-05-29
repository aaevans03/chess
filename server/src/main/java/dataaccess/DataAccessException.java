package dataaccess;

import server.exceptions.ServerException;

/**
 * Indicates there was an error connecting to the database
 */
public class DataAccessException extends ServerException {
    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(String message, Throwable ex) {
        super(message);
    }
}
