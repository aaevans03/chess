package server.exceptions;

import spark.Request;
import spark.Response;

public class AlreadyTakenException extends ServerException {
    public AlreadyTakenException() {
        super("username already taken.");
    }

    public AlreadyTakenException(String msg) {
        super(msg);
    }

    public static void errorHandler(Exception e, Request req, Response res) {
        ServerException.errorHandler(e, req, res);
        res.status(403);
    }
}
