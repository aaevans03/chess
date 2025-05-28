package dataaccess;

import model.UserData;

public interface UserDAO {
    void clearUserData() throws DataAccessException;

    void createUser(UserData userData) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;
}
