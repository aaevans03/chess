package dataaccess;

import model.AuthData;

public interface AuthDAO {
    void clearAuthData();

    String createAuthData(String username);

    AuthData getAuthData(String authToken);

    void deleteAuthData(AuthData authData);
}
