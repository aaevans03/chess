package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {
    // A map of auth data: find AuthData given an authToken
    static HashMap<String, AuthData> memoryAuthData = new HashMap<>();

    public MemoryAuthDAO() {
        // add values for testing
        memoryAuthData.put("12345", new AuthData("12345", "alex"));
        memoryAuthData.put("54321", new AuthData("54321", "bob"));
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
        memoryAuthData.remove(authData.username(), authData);
    }

    private String generateAuthToken() {
        return UUID.randomUUID().toString();
    }
}
