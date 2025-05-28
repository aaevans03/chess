package service;

import dataaccess.DataAccessException;
import dataaccess.memory.MemoryAuthDAO;
import dataaccess.memory.MemoryGameDAO;
import dataaccess.memory.MemoryUserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import server.exceptions.AlreadyTakenException;
import server.exceptions.InvalidAuthTokenException;
import server.exceptions.InvalidCredentialsException;
import service.request.LoginRequest;
import service.request.LogoutRequest;
import service.request.RegisterRequest;
import service.result.LoginResult;
import service.result.RegisterResult;

import java.util.HashMap;

class UserServiceTests {

    UserService userService;

    MemoryUserDAO userDB;
    MemoryAuthDAO authDB;
    MemoryGameDAO gameDB;

    @BeforeEach
    void setUp() {
        userDB = new MemoryUserDAO();
        authDB = new MemoryAuthDAO();
        gameDB = new MemoryGameDAO();
        userDB.clearUserData();
        authDB.clearAuthData();
        gameDB.clearGameData();
        userService = new UserService(userDB, authDB);
    }

    @Test
        // successfully register a new user
    void registerNewUser() {
        // expected output
        UserData user = new UserData("bob", "password", "e@mail.com");
        var expected = new HashMap<String, UserData>();
        expected.put("bob", user);

        // use the service to register
        RegisterResult registerResult = null;
        try {
            registerResult = userService.register(new RegisterRequest("bob", "password", "e@mail.com"));
        } catch (DataAccessException e) {
            System.out.println("Exception occurred: " + e.getMessage());
        }

        // check and see if the expected DB matches
        var expectedUserData = expected.values().toArray(new UserData[0]);
        var actualAuthData = userDB.getMap().values().toArray(new UserData[0]);
        Assertions.assertEquals(1, actualAuthData.length);
        Assertions.assertEquals(expectedUserData[0].username(), actualAuthData[0].username());
        Assertions.assertTrue(BCrypt.checkpw("password", actualAuthData[0].password()));
        Assertions.assertEquals(expectedUserData[0].email(), actualAuthData[0].email());

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
        try {
            userService.register(registerRequest);
        } catch (DataAccessException e) {
            System.out.println("Exception occurred: " + e.getMessage());
        }

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
        LoginResult loginResult = null;
        try {
            loginResult = userService.login(new LoginRequest("bob", "password"));
        } catch (DataAccessException e) {
            System.out.println("Exception occurred: " + e.getMessage());
        }

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
        LoginResult loginResult = null;
        try {
            loginResult = userService.login(new LoginRequest("bob", "password"));
        } catch (DataAccessException e) {
            System.out.println("Exception occurred: " + e.getMessage());
        }

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
        try {
            userService.login(new LoginRequest("bob", "password"));
        } catch (DataAccessException e) {
            System.out.println("Exception occurred: " + e.getMessage());
        }

        // use a bad auth token to log them out
        var logoutRequest = new LogoutRequest("badAuthToken");
        Assertions.assertThrows(InvalidAuthTokenException.class, () -> userService.logout(logoutRequest));
    }
}