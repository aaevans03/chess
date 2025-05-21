package server;

import com.google.gson.Gson;
import server.exceptions.*;
import spark.*;

import java.util.Map;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        // USER
        Spark.post("/user", UserHandler::handleRegister);
        Spark.post("/session", UserHandler::handleLogin);
        Spark.delete("/session", UserHandler::handleLogout);

        // CLEAR
        Spark.delete("/db", ClearHandler::handleClear);

        // HANDLE EXCEPTIONS
        Spark.exception(InvalidInputException.class, InvalidInputException::errorHandler);
        Spark.exception(AlreadyTakenException.class, AlreadyTakenException::errorHandler);
        Spark.exception(InvalidCredentialsException.class, InvalidCredentialsException::errorHandler);
        Spark.exception(AlreadyAuthorizedException.class, AlreadyAuthorizedException::errorHandler);
        Spark.exception(InvalidAuthTokenException.class, InvalidAuthTokenException::errorHandler);
        Spark.exception(Exception.class, ServerException::errorHandler);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object errorHandler (Exception e, Request req, Response res) {
        var body = new Gson().toJson(Map.of("message", String.format("%s", e.getMessage()), "success", false));
        res.type("application/json");
        res.status(500);
        res.body(body);
        return body;
    }
}
