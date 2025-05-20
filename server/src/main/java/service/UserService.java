package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import model.UserData;
import service.request.RegisterRequest;
import service.result.RegisterResult;

public class UserService {
    public RegisterResult register(RegisterRequest registerRequest) {
        var userDB = new MemoryUserDAO();
        var authDB = new MemoryAuthDAO();

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
}
