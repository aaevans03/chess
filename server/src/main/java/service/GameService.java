package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import server.exceptions.InvalidAuthTokenException;
import server.exceptions.InvalidInputException;
import service.request.CreateRequest;
import service.request.ListRequest;
import service.result.CreateResult;
import service.result.ListResult;

public class GameService {
    MemoryUserDAO userDB = new MemoryUserDAO();
    MemoryAuthDAO authDB = new MemoryAuthDAO();
    MemoryGameDAO gameDB = new MemoryGameDAO();

    public ListResult list(ListRequest listRequest) {
        var authToken = listRequest.authToken();

        var authData = authDB.getAuthData(authToken);

        // 401, unauthorized
        if (authData == null) {
            throw new InvalidAuthTokenException();
        }

        return new ListResult(gameDB.listGames());
    }

    public CreateResult create(CreateRequest createRequest) {

        var authToken = createRequest.authToken();
        var gameName = createRequest.gameName();

        System.out.println(authToken);
        System.out.println(gameName);

        var authData = authDB.getAuthData(authToken);

        debug();
        System.out.println(authData);

        // 400, bad request
        if (gameName == null) {
            throw new InvalidInputException();
        }

        // 401, unauthorized
        if (authData == null) {
            throw new InvalidAuthTokenException();
        }

        var gameID = gameDB.createGame(gameName);

        return new CreateResult(gameID);
    }

    private void debug() {
        for (var yes : userDB.getMap().values()) {
            System.out.println("\nusername: " + yes.username());
            System.out.println("password: " + yes.password());
            System.out.println("email: " + yes.email());
        }

        for (var yes : authDB.getMap().values()) {
            System.out.println("authToken: " + yes.authToken());
            System.out.println("username: " + yes.username());
        }

        System.out.print(gameDB.listGames());
    }
}
