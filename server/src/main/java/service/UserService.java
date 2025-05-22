package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import model.UserData;
import server.exceptions.*;
import service.request.LoginRequest;
import service.request.LogoutRequest;
import service.request.RegisterRequest;
import service.result.LoginResult;
import service.result.LogoutResult;
import service.result.RegisterResult;

import java.util.Objects;

public class UserService {
    MemoryUserDAO userDB = new MemoryUserDAO();
    MemoryAuthDAO authDB = new MemoryAuthDAO();

    public RegisterResult register(RegisterRequest registerRequest) throws AlreadyTakenException {

        var username = registerRequest.username();
        var password = registerRequest.password();
        var email = registerRequest.email();

        // 400, bad request
        if (username == null || password == null) {
            throw new InvalidInputException();
        }

        // 403, already taken
        if (userDB.getUser(username) != null) {
            throw new AlreadyTakenException();
        }

        userDB.createUser(new UserData(username, password, email));
        var authToken = authDB.createAuthData(username);

        return new RegisterResult(username, authToken);
    }

    public LoginResult login(LoginRequest loginRequest) throws InvalidCredentialsException {

        var username = loginRequest.username();
        var password = loginRequest.password();

        var dbData = userDB.getUser(username);

        // 400, bad request
        if (username == null || password == null) {
            throw new InvalidInputException();
        }

        // 401, unauthorized
        if (dbData == null) {
            throw new InvalidCredentialsException();
        }

        // 401, unauthorized
        if (!Objects.equals(password, dbData.password())) {
            throw new InvalidCredentialsException();
        }

        var authToken = authDB.createAuthData(username);

        return new LoginResult(username, authToken);
    }

    public LogoutResult logout(LogoutRequest logoutRequest) {

        var authToken = logoutRequest.authToken();

        var dbData = authDB.getAuthData(authToken);

        // 401, unauthorized
        if (dbData == null) {
            throw new InvalidAuthTokenException();
        }

        authDB.deleteAuthData(dbData);

        return new LogoutResult();
    }
}
