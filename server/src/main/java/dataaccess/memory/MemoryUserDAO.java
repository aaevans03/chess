package dataaccess.memory;

import dataaccess.UserDAO;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

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
        // hash password, then add to DB
        var newUserData = new UserData(userData.username(), hashPassword(userData.password()), userData.email());
        memoryUserData.put(userData.username(), newUserData);
    }

    @Override
    public UserData getUser(String username) {
        return memoryUserData.get(username);
    }

    private String hashPassword(String clearTextPassword) {
        return BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());
    }

    // for debugging
    public HashMap<String, UserData> getMap() {
        return memoryUserData;
    }
}
