package server.exceptions;

import spark.Request;
import spark.Response;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("unauthorized");
    }

    public static void errorHandler(Exception e, Request req, Response res) {
        ServerException.errorHandler(e, req, res);
        res.status(401);
    }
}
