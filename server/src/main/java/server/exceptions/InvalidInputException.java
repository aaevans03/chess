package server.exceptions;

import spark.Request;
import spark.Response;

public class InvalidInputException extends ServerException {
    public InvalidInputException() {
        super("bad request");
    }

    public static void errorHandler(Exception e, Request req, Response res) {
        ServerException.errorHandler(e, req, res);
        res.status(400);
    }
}
