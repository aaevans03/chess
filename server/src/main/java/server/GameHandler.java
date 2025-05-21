package server;

import service.GameService;
import service.request.CreateRequest;
import spark.Request;
import spark.Response;

public class GameHandler {
    public static Object handleCreate(Request request, Response response) {
        // decode object, make new CreateRequest
        var objectEncoderDecoder = new ObjectEncoderDecoder();

        CreateRequest input = (CreateRequest) objectEncoderDecoder.decode(request.body(), CreateRequest.class);
        input = new CreateRequest(request.headers("authorization"), input.gameName());

        // send CreateRequest object to GameService and try registering
        var result = new GameService().create(input);

        // encode result and return
        return objectEncoderDecoder.encode(result);
    }
}
