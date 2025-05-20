package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    // A map of user data: find UserData given a username
    static HashMap<String, UserData> memoryUserData = new HashMap<>();

    public MemoryUserDAO() {
        // add values for testing
        memoryUserData.put("alex", new UserData("alex", "123", "xd"));
        memoryUserData.put("bob", new UserData("bob", "123", "xd"));
    }

    @Override
    public void clearUserData() {
        memoryUserData.clear();
    }

    @Override
    public void createUser(UserData userData) {
        memoryUserData.put(userData.username(), userData);
    }

    @Override
    public UserData getUser(String username) {
        return memoryUserData.get(username);
    }
}
