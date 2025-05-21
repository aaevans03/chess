package server;

import dataaccess.DataAccessException;
import server.exceptions.*;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        // USER
        Spark.post("/user", UserHandler::handleRegister);
        Spark.post("/session", UserHandler::handleLogin);
        Spark.delete("/session", UserHandler::handleLogout);

        // GAME
        Spark.get("/game", GameHandler::handleList);
        Spark.post("/game", GameHandler::handleCreate);
        Spark.put("/game", GameHandler::handleJoin);

        // CLEAR
        Spark.delete("/db", ClearHandler::handleClear);

        // HANDLE EXCEPTIONS
        Spark.exception(InvalidInputException.class, InvalidInputException::errorHandler);
        Spark.exception(AlreadyTakenException.class, AlreadyTakenException::errorHandler);
        Spark.exception(InvalidCredentialsException.class, InvalidCredentialsException::errorHandler);
        Spark.exception(AlreadyAuthorizedException.class, AlreadyAuthorizedException::errorHandler);
        Spark.exception(InvalidAuthTokenException.class, InvalidAuthTokenException::errorHandler);
        Spark.exception(DataAccessException.class, DataAccessException::errorHandler);
        Spark.exception(Exception.class, ServerException::errorHandler);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
