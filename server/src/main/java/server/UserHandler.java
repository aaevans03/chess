package server;

import com.google.gson.Gson;
import service.UserService;
import service.request.LoginRequest;
import service.request.LogoutRequest;
import service.request.RegisterRequest;
import spark.Request;
import spark.Response;

public class UserHandler {
    public static Object handleRegister(Request request, Response response) {
        // decode object, make new RegisterRequest
        Gson gson = new Gson();
        RegisterRequest input = gson.fromJson(request.body(), RegisterRequest.class);

        // send RegisterRequest object to UserService and try registering
        var result = new UserService().register(input);

        // encode result and return
        return gson.toJson(result);
    }

    public static Object handleLogin(Request request, Response response) {
        // decode object, make new LoginRequest
        Gson gson = new Gson();
        LoginRequest input = gson.fromJson(request.body(), LoginRequest.class);

        // send LoginRequest object to UserService and try logging in
        var result = new UserService().login(input);

        // encode result and return
        return gson.toJson(result);
    }

    public static Object handleLogout(Request request, Response response) {
        // decode object, make new LogoutRequest
        var input = request.headers("authorization");

        // send LogoutRequest object to UserService and try logging out
        var result = new UserService().logout(new LogoutRequest(input));

        // encode result and return
        Gson gson = new Gson();
        return gson.toJson(result);
    }
}
