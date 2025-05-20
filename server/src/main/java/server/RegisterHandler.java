package server;

import com.google.gson.Gson;
import service.UserService;
import service.request.RegisterRequest;
import spark.Request;
import spark.Response;

public class RegisterHandler {
    public static Object handleRegister(Request request, Response response) {
        // decode object, make new RegisterRequest
        Gson gson = new Gson();
        RegisterRequest input = gson.fromJson(request.body(), RegisterRequest.class);

        // send RegisterRequest object to UserService and try registering
        var result = new UserService().register(input);

        // encode result and return
        return gson.toJson(result);
    }
}
