package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.request.RegisterRequest;
import service.result.RegisterResult;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTests {

    MemoryUserDAO userDB;
    MemoryAuthDAO authDB;
    MemoryGameDAO gameDB;

    @BeforeEach
    void setUp() {
        userDB = new MemoryUserDAO();
        authDB = new MemoryAuthDAO();
        gameDB = new MemoryGameDAO();
    }

    @Test
    void registerNewUser() {
        // successfully register a new user
        UserData user = new UserData("bob", "password", "e@mail.com");
        var expected = new HashMap<String, UserData>();
        expected.put("bob", user);

        var userService = new UserService();
        var registerResult = userService.register(new RegisterRequest("bob", "password", "e@mail.com"));

        // check and see if the expected DB matches
        Assertions.assertEquals(expected, userDB.getMap());

        // check and see if the expected AuthData matches the RegisterResult
        var expectedAuthData = authDB.getMap().values().toArray(new AuthData[0]);

        Assertions.assertEquals(1, expectedAuthData.length);
        Assertions.assertEquals(expectedAuthData[0].authToken(), registerResult.authToken());
        Assertions.assertEquals(expectedAuthData[0].username(), registerResult.username());
    }

    void registerExistingUser() {
        // try to register an existing user

        // expect AlreadyTakenException
    }

    @Test
    void loginUser() {
        // successfully login a user
    }

    void loginUserBadPassword() {
        // try to log in a user with a bad password
    }

    @Test
    void logoutUser() {
        // successfully log out a user
    }

    void badLogoutUser() {
        // try to log out a user with no authToken
    }

    @Test
    void nullTests() {
        // try all service functions with null inputs, expect different exceptions
    }
}