package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import model.UserData;
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

    public RegisterResult register(RegisterRequest registerRequest) {

        var username = registerRequest.username();
        var password = registerRequest.password();
        var email = registerRequest.email();

        if (userDB.getUser(username) != null) {
            // TODO: add AlreadyTakenException
        }

        userDB.createUser(new UserData(username, password, email));
        var authToken = authDB.createAuthData(username);

        return new RegisterResult(username, authToken);
    }

    public LoginResult login(LoginRequest loginRequest) {

        var username = loginRequest.username();
        var password = loginRequest.password();

        var dbData = userDB.getUser(username);

        if (dbData == null) {
            // TODO: add InvalidCredentialsException
        }

        if (!Objects.equals(password, dbData.password())) {
            // TODO: add InvalidCredentialsException
        }

        var authToken = authDB.createAuthData(username);

        return new LoginResult(username, authToken);
    }

    public LogoutResult logout(LogoutRequest logoutRequest) {

        var authToken = logoutRequest.authToken();

        var dbData = authDB.getAuthData(authToken);

        if (dbData == null) {
            // TODO: Add InvalidAuthTokenException
        }

        authDB.deleteAuthData(dbData);

        return new LogoutResult();
    }
}
