package dataaccess;

import model.UserData;

public interface UserDAO {
    void clearUserData() throws DataAccessException;

    void createUser(UserData userData);

    UserData getUser(String username);
}
