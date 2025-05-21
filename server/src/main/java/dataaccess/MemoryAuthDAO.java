package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {
    // A map of auth data: find AuthData given an authToken
    static HashMap<String, AuthData> memoryAuthData = new HashMap<>();

    public MemoryAuthDAO() {
        // add values for testing
    }

    @Override
    public void clearAuthData() {
        memoryAuthData.clear();
    }

    @Override
    public String createAuthData(String username) {
        String newAuthToken = generateAuthToken();
        memoryAuthData.put(newAuthToken, new AuthData(newAuthToken, username));
        return newAuthToken;
    }

    @Override
    public AuthData getAuthData(String authToken) {
        return memoryAuthData.get(authToken);
    }

    @Override
    public void deleteAuthData(AuthData authData) {
        memoryAuthData.remove(authData.authToken(), authData);
    }

    private String generateAuthToken() {
        return UUID.randomUUID().toString();
    }

    // for debugging
    public HashMap<String, AuthData> getMap() {
        return memoryAuthData;
    }
}
