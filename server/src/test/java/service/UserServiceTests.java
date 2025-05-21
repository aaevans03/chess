package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.exceptions.AlreadyTakenException;
import server.exceptions.InvalidAuthTokenException;
import server.exceptions.InvalidCredentialsException;
import service.request.LoginRequest;
import service.request.LogoutRequest;
import service.request.RegisterRequest;

import java.util.HashMap;

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
    // successfully register a new user
    void registerNewUser() {
        // expected output
        UserData user = new UserData("bob", "password", "e@mail.com");
        var expected = new HashMap<String, UserData>();
        expected.put("bob", user);

        // use the service to register
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

    @Test
    // try to register an existing user
    void registerExistingUser() {
        // make a new registerRequest and register user twice
        var registerRequest = new RegisterRequest("bob", "password", "e@mail.com");
        var userService = new UserService();
        userService.register(registerRequest);

        // check and see if register throws an exception
        Assertions.assertThrows(AlreadyTakenException.class, () -> userService.register(registerRequest));
    }

    @Test
    // successfully login a user
    void loginUser() {
        // expected output
        UserData user = new UserData("bob", "password", "e@mail.com");
        userDB.createUser(user);

        // use the service to login
        var userService = new UserService();
        var loginResult = userService.login(new LoginRequest("bob", "password"));

        // check and see if the expected AuthData matches the LoginResult
        var expectedAuthData = authDB.getMap().values().toArray(new AuthData[0]);
        Assertions.assertEquals(1, expectedAuthData.length);
        Assertions.assertEquals(expectedAuthData[0].authToken(), loginResult.authToken());
        Assertions.assertEquals(expectedAuthData[0].username(), loginResult.username());
    }

    @Test
    // try to log in a user with a bad password
    void loginUserBadPassword() {
        // create user
        UserData user = new UserData("bob", "password", "e@mail.com");
        userDB.createUser(user);

        // use the service to login
        var userService = new UserService();
        var loginRequest = new LoginRequest("bob", "wrongPassword");
        Assertions.assertThrows(InvalidCredentialsException.class, () -> userService.login(loginRequest));
    }

    @Test
    // successfully log out a user
    void logoutUser() {
        // create user
        UserData user = new UserData("bob", "password", "e@mail.com");
        userDB.createUser(user);

        // log in the user
        var userService = new UserService();
        var loginResult = userService.login(new LoginRequest("bob", "password"));

        // use the AuthToken to log them out
        var logoutRequest = new LogoutRequest(loginResult.authToken());
        userService.logout(logoutRequest);

        // assert that the userDB is empty
        Assertions.assertEquals(new HashMap<>(), authDB.getMap());
    }

    @Test
    // try to log out a user with no authToken
    void badLogoutUser() {
        // create user
        UserData user = new UserData("bob", "password", "e@mail.com");
        userDB.createUser(user);

        // log in the user
        var userService = new UserService();
        userService.login(new LoginRequest("bob", "password"));

        // use a bad auth token to log them out
        var logoutRequest = new LogoutRequest("badAuthToken");
        Assertions.assertThrows(InvalidAuthTokenException.class, () -> userService.logout(logoutRequest));
    }

    @Test
    void nullTests() {
        // try all service functions with null inputs, expect different exceptions
    }
}