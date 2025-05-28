package dataaccess;

import model.UserData;

public interface UserDAO {
    void clearUserData();

    void createUser(UserData userData);

    UserData getUser(String username);
}
