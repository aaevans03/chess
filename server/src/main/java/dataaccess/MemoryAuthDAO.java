package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {
    // A map of auth data: find AuthData given an authToken
    HashMap<String, AuthData> memoryAuthData;

    MemoryAuthDAO() {
        memoryAuthData = new HashMap<>();
    }

    @Override
    public void clearAuthData() {
        memoryAuthData.clear();
    }

    @Override
    public void createAuthData(String username) {
        String newAuthToken = generateAuthToken();
        memoryAuthData.put(newAuthToken, new AuthData(newAuthToken, username));
    }

    @Override
    public AuthData getAuthData(String authToken) {
        return memoryAuthData.get(authToken);
    }

    @Override
    public void deleteAuthData(AuthData authData) {
        memoryAuthData.remove(authData.username(), authData);
    }

    private String generateAuthToken() {
        return UUID.randomUUID().toString();
    }
}
