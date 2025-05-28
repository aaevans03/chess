package dataaccess;

import model.AuthData;

public interface AuthDAO {
    void clearAuthData() throws DataAccessException;

    String createAuthData(String username) throws DataAccessException;

    AuthData getAuthData(String authToken) throws DataAccessException;

    void deleteAuthData(AuthData authData) throws DataAccessException;
}
