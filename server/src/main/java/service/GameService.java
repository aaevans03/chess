package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import server.exceptions.AlreadyTakenException;
import server.exceptions.InvalidAuthTokenException;
import service.request.CreateRequest;
import service.request.JoinRequest;
import service.request.ListRequest;
import service.result.CreateResult;
import service.result.JoinResult;
import service.result.ListResult;

public class GameService {
    AuthDAO authDB;
    GameDAO gameDB;

    public GameService(AuthDAO authDB, GameDAO gameDB) {
        this.authDB = authDB;
        this.gameDB = gameDB;
    }

    public ListResult list(ListRequest listRequest) throws DataAccessException {
        var authToken = listRequest.authToken();

        var authData = authDB.getAuthData(authToken);

        // 401, unauthorized
        if (authData == null) {
            throw new InvalidAuthTokenException();
        }

        return new ListResult(gameDB.listGames());
    }

    public CreateResult create(CreateRequest createRequest) throws DataAccessException {

        var authToken = createRequest.authToken();
        var gameName = createRequest.gameName();

        var authData = authDB.getAuthData(authToken);

        // 400, bad request
        if (gameName == null) {
            throw new DataAccessException("no game name provided");
        }

        // 401, unauthorized
        if (authData == null) {
            throw new InvalidAuthTokenException();
        }

        var gameID = gameDB.createGame(gameName);

        return new CreateResult(gameID);
    }

    public JoinResult join(JoinRequest joinRequest) throws DataAccessException {

        var authToken = joinRequest.authToken();
        var playerColor = joinRequest.playerColor();
        var gameID = joinRequest.gameID();

        var authData = authDB.getAuthData(authToken);

        // 400, bad request
        if (playerColor == null) {
            throw new DataAccessException("no team color provided");
        }

        // 400, bad request
        if (gameID == 0) {
            throw new DataAccessException("invalid game ID entered");
        }

        // 401, unauthorized
        if (authData == null) {
            throw new InvalidAuthTokenException();
        }

        var game = gameDB.getGame(gameID);

        // 400, invalid game ID
        if (game == null) {
            throw new DataAccessException("invalid game ID");
        }

        switch (playerColor) {
            case WHITE -> {
                // 403, game already taken
                if (game.whiteUsername() != null) {
                    throw new AlreadyTakenException();
                }
                gameDB.updateGame(gameID, authData.username(), ChessGame.TeamColor.WHITE, null);
            }
            case BLACK -> {
                // 403, game already taken
                if (game.blackUsername() != null) {
                    throw new AlreadyTakenException();
                }
                gameDB.updateGame(gameID, authData.username(), ChessGame.TeamColor.BLACK, null);
            }
        }
        return new JoinResult();
    }
}
