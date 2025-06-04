package server;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import serverfacade.request.CreateRequest;
import serverfacade.request.JoinRequest;
import serverfacade.request.ListRequest;
import service.GameService;
import spark.Request;
import spark.Response;

public class GameHandler {

    GameService gameService;

    GameHandler(AuthDAO authDB, GameDAO gameDB) {
        gameService = new GameService(authDB, gameDB);
    }

    public Object handleList(Request request, Response ignoredResponse) throws DataAccessException {
        // get header
        var input = request.headers("authorization");

        // send new ListRequest object to GameService and try logging out
        var result = gameService.list(new ListRequest(input));

        // encode result and return
        var objectEncoderDecoder = new ObjectEncoderDecoder();
        return objectEncoderDecoder.encode(result);
    }

    public Object handleCreate(Request request, Response ignoredResponse) throws DataAccessException {
        // decode object, make new CreateRequest
        var objectEncoderDecoder = new ObjectEncoderDecoder();

        CreateRequest input = (CreateRequest) objectEncoderDecoder.decode(request.body(), CreateRequest.class);
        input = new CreateRequest(request.headers("authorization"), input.gameName());

        // send CreateRequest object to GameService and try registering
        var result = gameService.create(input);

        // encode result and return
        return objectEncoderDecoder.encode(result);
    }

    public Object handleJoin(Request request, Response ignoredResponse) throws DataAccessException {
        // decode object, make new JoinRequest
        var objectEncoderDecoder = new ObjectEncoderDecoder();

        JoinRequest input = (JoinRequest) objectEncoderDecoder.decode(request.body(), JoinRequest.class);
        input = new JoinRequest(request.headers("authorization"), input.playerColor(), input.gameID());

        // send JoinRequest object to GameService and try joining
        var result = gameService.join(input);

        // encode result and return
        return objectEncoderDecoder.encode(result);
    }
}
