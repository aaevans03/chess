package server.exceptions;

import spark.Request;
import spark.Response;

public class AlreadyAuthorizedException extends RuntimeException {
  public AlreadyAuthorizedException() {
    super("bad request");
  }

  public static void errorHandler(Exception e, Request req, Response res) {
    ServerException.errorHandler(e, req, res);
    res.status(400);
  }
}
