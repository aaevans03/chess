package server;

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

        // CLEAR
        Spark.delete("/db", ClearHandler::handleClear);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
