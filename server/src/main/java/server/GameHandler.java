package server;

import service.GameService;
import service.request.CreateRequest;
import service.request.JoinRequest;
import service.request.ListRequest;
import spark.Request;
import spark.Response;

public class GameHandler {
    public static Object handleList(Request request, Response response) {
        // get header
        var input = request.headers("authorization");

        // send new ListRequest object to GameService and try logging out
        var result = new GameService().list(new ListRequest(input));

        // encode result and return
        var objectEncoderDecoder = new ObjectEncoderDecoder();
        return objectEncoderDecoder.encode(result);
    }

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

    public static Object handleJoin(Request request, Response response) {
        // decode object, make new JoinRequest
        var objectEncoderDecoder = new ObjectEncoderDecoder();

        JoinRequest input = (JoinRequest) objectEncoderDecoder.decode(request.body(), JoinRequest.class);
        input = new JoinRequest(request.headers("authorization"), input.playerColor(), input.gameID());

        // send JoinRequest object to GameService and try joining
        var result = new GameService().join(input);

        // encode result and return
        return objectEncoderDecoder.encode(result);
    }
}
