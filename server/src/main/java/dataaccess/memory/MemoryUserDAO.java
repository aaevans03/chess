package dataaccess.memory;

import dataaccess.UserDAO;
import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    // A map of user data: find UserData given a username
    HashMap<String, UserData> memoryUserData = new HashMap<>();

    public MemoryUserDAO() {
        // add values for testing
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

    // for debugging
    public HashMap<String, UserData> getMap() {
        return memoryUserData;
    }
}
