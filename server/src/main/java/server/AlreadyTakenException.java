package server;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;

import java.util.Map;

public class AlreadyTakenException extends RuntimeException {
    public AlreadyTakenException() {
        super("username already taken");
    }

    public static Object errorHandler(Exception e, Request req, Response res) {
        var body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage())));
        res.type("application/json");
        res.status(403);
        res.body(body);
        return body;
    }
}


