package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import server.exceptions.AlreadyTakenException;
import server.exceptions.InvalidAuthTokenException;
import server.exceptions.InvalidCredentialsException;
import server.exceptions.InvalidInputException;
import serverfacade.request.LoginRequest;
import serverfacade.request.LogoutRequest;
import serverfacade.request.RegisterRequest;
import serverfacade.result.LoginResult;
import serverfacade.result.LogoutResult;
import serverfacade.result.RegisterResult;

public class UserService {
    UserDAO userDB;
    AuthDAO authDB;

    public UserService(UserDAO userDB, AuthDAO authDB) {
        this.userDB = userDB;
        this.authDB = authDB;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws AlreadyTakenException, DataAccessException {

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

    public LoginResult login(LoginRequest loginRequest) throws InvalidCredentialsException, DataAccessException {

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

        // Check entered password against hashed database password
        boolean verifyUser = BCrypt.checkpw(password, dbData.password());
        // 401, unauthorized
        if (!verifyUser) {
            throw new InvalidCredentialsException();
        }

        var authToken = authDB.createAuthData(username);
        return new LoginResult(username, authToken);
    }

    public LogoutResult logout(LogoutRequest logoutRequest) throws DataAccessException {

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
