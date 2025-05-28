package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import service.result.ClearResult;

public class ClearService {

    UserDAO userDB;
    AuthDAO authDB;
    GameDAO gameDB;

    public ClearService(UserDAO userDB, AuthDAO authDB, GameDAO gameDB) {
        this.userDB = userDB;
        this.authDB = authDB;
        this.gameDB = gameDB;
    }

    public ClearResult clear() throws DataAccessException {
        authDB.clearAuthData();
        gameDB.clearGameData();
        userDB.clearUserData();
        return new ClearResult();
    }
}
