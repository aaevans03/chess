package service;

import dataaccess.*;
import service.result.ClearResult;

public class ClearService {

    public static ClearResult clear() {
        new MemoryGameDAO().clearGameData();
        new MemoryAuthDAO().clearAuthData();
        new MemoryUserDAO().clearUserData();
        return new ClearResult();
    }
}
