package dataaccess;

import server.ObjectEncoderDecoder;
import spark.Request;
import spark.Response;

import java.util.Map;

/**
 * Indicates there was an error connecting to the database
 */
public class DataAccessException extends Exception {
    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(String message, Throwable ex) {
        super(message, ex);
    }

    public static void errorHandler(Exception e, Request ignoredReq, Response res) {
        var objectEncoderDecoder = new ObjectEncoderDecoder();
        var body = objectEncoderDecoder.encode(Map.of("message", String.format("Error: %s", e.getMessage())));
        res.type("application/json");
        res.status(500);
        res.body(body);
    }
}
