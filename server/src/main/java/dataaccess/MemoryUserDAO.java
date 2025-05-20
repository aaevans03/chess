package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    // A map of user data: find UserData given a username
    HashMap<String, UserData> memoryUserData;

    MemoryUserDAO() {
        memoryUserData = new HashMap<>();
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
