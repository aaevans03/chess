package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

public class ClearServiceTest {
    @Test
    void clear() {
        MemoryUserDAO userDB = new MemoryUserDAO();
        MemoryAuthDAO authDB = new MemoryAuthDAO();
        MemoryGameDAO gameDB = new MemoryGameDAO();

        // test: add values to the DBs, then clear and test to see if the results are actually empty
        userDB.createUser(new UserData("bob", "password", "e@mail.com"));
        userDB.createUser(new UserData("sally", "password", "f@mail.com"));
        userDB.createUser(new UserData("kim", "password", "g@mail.com"));
        authDB.createAuthData("bob");
        authDB.createAuthData("sally");
        authDB.createAuthData("kim");
        gameDB.createGame("Awesome Game");
        gameDB.createGame("The Second Game");
        gameDB.createGame("3rd Game");

        ClearService.clear();

        // Expected maps/arrays
        var expectedUserDB = new HashMap<>();
        var expectedAuthDB = new HashMap<>();
        var expectedGameDB = new ArrayList<>();

        Assertions.assertEquals(expectedUserDB, userDB.getMap());
        Assertions.assertEquals(expectedAuthDB, authDB.getMap());
        Assertions.assertEquals(expectedGameDB, gameDB.listGames());
    }
}
