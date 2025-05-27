package service;

import dataaccess.AuthDAO;
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

    public ClearResult clear() {
        userDB.clearUserData();
        authDB.clearAuthData();
        gameDB.clearGameData();
        return new ClearResult();
    }
}
