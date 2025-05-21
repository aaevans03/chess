package server.exceptions;

import server.ObjectEncoderDecoder;
import spark.Request;
import spark.Response;

import java.util.Map;

public abstract class ServerException extends RuntimeException {

    public ServerException(String errorMessage) {
        super(errorMessage);
    }

    public static void errorHandler(Exception e, Request req, Response res) {
        var objectEncoderDecoder = new ObjectEncoderDecoder();
        var body = objectEncoderDecoder.encode(Map.of("message", String.format("Error: %s", e.getMessage())));
        res.type("application/json");
        res.status(500);
        res.body(body);
    }
}
