package server;

import service.UserService;
import service.request.LoginRequest;
import service.request.LogoutRequest;
import service.request.RegisterRequest;
import spark.Request;
import spark.Response;

public class UserHandler {
    public static Object handleRegister(Request request, Response response) {
        // decode object, make new RegisterRequest
        var objectEncoderDecoder = new ObjectEncoderDecoder();
        RegisterRequest input = (RegisterRequest) objectEncoderDecoder.decode(request.body(), RegisterRequest.class);

        // send RegisterRequest object to UserService and try registering
        var result = new UserService().register(input);

        // encode result and return
        return objectEncoderDecoder.encode(result);
    }

    public static Object handleLogin(Request request, Response response) {
        // decode object, make new LoginRequest
        var objectEncoderDecoder = new ObjectEncoderDecoder();
        LoginRequest input = (LoginRequest) objectEncoderDecoder.decode(request.body(), LoginRequest.class);

        // send LoginRequest object to UserService and try logging in
        var result = new UserService().login(input);

        // encode result and return
        return objectEncoderDecoder.encode(result);
    }

    public static Object handleLogout(Request request, Response response) {
        // get header
        var input = request.headers("authorization");

        // send new LogoutRequest object to UserService and try logging out
        var result = new UserService().logout(new LogoutRequest(input));

        // encode result and return
        var objectEncoderDecoder = new ObjectEncoderDecoder();
        return objectEncoderDecoder.encode(result);
    }
}
