package server;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import dataaccess.memory.MemoryAuthDAO;
import dataaccess.memory.MemoryGameDAO;
import dataaccess.memory.MemoryUserDAO;
import dataaccess.mysql.MySqlAuthDAO;
import dataaccess.mysql.MySqlGameDAO;
import dataaccess.mysql.MySqlUserDAO;
import server.exceptions.*;
import server.websocket.WebsocketHandler;
import spark.Spark;

public class Server {
    UserHandler userHandler;
    GameHandler gameHandler;
    ClearHandler clearHandler;
    WebsocketHandler websocketHandler;

    /**
     * Constructor: determine if we are storing application data in memory or in MySQL
     */
    public Server() {
        UserDAO userDB;
        AuthDAO authDB;
        GameDAO gameDB;

        // Change this line to determine whether to use MySQL or not
        var usesMySQL = true;

        if (usesMySQL) {
            try {
                userDB = new MySqlUserDAO();
                authDB = new MySqlAuthDAO();
                gameDB = new MySqlGameDAO();
            } catch (DataAccessException ex) {
                userDB = null;
                authDB = null;
                gameDB = null;
                System.out.println("Unable to initialize database with MySQL: " + ex.getMessage());
            }
        } else {
            userDB = new MemoryUserDAO();
            authDB = new MemoryAuthDAO();
            gameDB = new MemoryGameDAO();
        }

        userHandler = new UserHandler(userDB, authDB);
        gameHandler = new GameHandler(authDB, gameDB);
        clearHandler = new ClearHandler(userDB, authDB, gameDB);
        websocketHandler = new WebsocketHandler();
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        // WEBSOCKET
        Spark.webSocket("/ws", websocketHandler);

        // USER
        Spark.post("/user", userHandler::handleRegister);
        Spark.post("/session", userHandler::handleLogin);
        Spark.delete("/session", userHandler::handleLogout);

        // GAME
        Spark.get("/game", gameHandler::handleList);
        Spark.post("/game", gameHandler::handleCreate);
        Spark.put("/game", gameHandler::handleJoin);

        // CLEAR
        Spark.delete("/db", clearHandler::handleClear);

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
