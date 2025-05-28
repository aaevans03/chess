package server;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import service.UserService;
import service.request.LoginRequest;
import service.request.LogoutRequest;
import service.request.RegisterRequest;
import spark.Request;
import spark.Response;

public class UserHandler {

    UserService userService;

    UserHandler(UserDAO userDB, AuthDAO authDB) {
        userService = new UserService(userDB, authDB);
    }

    public Object handleRegister(Request request, Response response) throws DataAccessException {
        // decode object, make new RegisterRequest
        var objectEncoderDecoder = new ObjectEncoderDecoder();
        RegisterRequest input = (RegisterRequest) objectEncoderDecoder.decode(request.body(), RegisterRequest.class);

        // send RegisterRequest object to UserService and try registering
        var result = userService.register(input);

        // encode result and return
        return objectEncoderDecoder.encode(result);
    }

    public Object handleLogin(Request request, Response response) throws DataAccessException {
        // decode object, make new LoginRequest
        var objectEncoderDecoder = new ObjectEncoderDecoder();
        LoginRequest input = (LoginRequest) objectEncoderDecoder.decode(request.body(), LoginRequest.class);

        // send LoginRequest object to UserService and try logging in
        var result = userService.login(input);

        // encode result and return
        return objectEncoderDecoder.encode(result);
    }

    public Object handleLogout(Request request, Response response) throws DataAccessException {
        // get header
        var input = request.headers("authorization");

        // send new LogoutRequest object to UserService and try logging out
        var result = userService.logout(new LogoutRequest(input));

        // encode result and return
        var objectEncoderDecoder = new ObjectEncoderDecoder();
        return objectEncoderDecoder.encode(result);
    }
}
