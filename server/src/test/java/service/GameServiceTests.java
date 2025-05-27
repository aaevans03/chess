package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.memory.MemoryAuthDAO;
import dataaccess.memory.MemoryGameDAO;
import dataaccess.memory.MemoryUserDAO;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.exceptions.AlreadyTakenException;
import server.exceptions.InvalidAuthTokenException;
import service.request.CreateRequest;
import service.request.JoinRequest;
import service.request.ListRequest;

import java.util.ArrayList;

class GameServiceTests {

    GameService gameService;

    MemoryUserDAO userDB;
    MemoryAuthDAO authDB;
    MemoryGameDAO gameDB;
    String authToken;

    @BeforeEach
    void setUp() {
        userDB = new MemoryUserDAO();
        authDB = new MemoryAuthDAO();
        gameDB = new MemoryGameDAO();
        userDB.clearUserData();
        authDB.clearAuthData();
        gameDB.clearGameData();
        dummyGameList();
        authToken = authDB.createAuthData("bob");
        gameService = new GameService(authDB, gameDB);
    }

    void dummyGameList() {
        gameDB.createGame("game1");
        gameDB.updateGame(1, "player1", ChessGame.TeamColor.WHITE);
        gameDB.updateGame(1, "player2", ChessGame.TeamColor.BLACK);
        gameDB.createGame("game2");
        gameDB.updateGame(2, "player3", ChessGame.TeamColor.WHITE);
        gameDB.updateGame(2, "player4", ChessGame.TeamColor.BLACK);
        gameDB.createGame("game3");
        gameDB.updateGame(3, "player5", ChessGame.TeamColor.WHITE);
    }

    @Test
    void listAllGames() {
        // create a list of games, make a list manually
        var game1 = new GameData(1, "player1", "player2", "game1", new ChessGame());
        var game2 = new GameData(2, "player3", "player4", "game2", new ChessGame());
        var game3 = new GameData(3, "player5", null, "game3", new ChessGame());

        var expectedList = new ArrayList<GameData>();
        expectedList.add(game1);
        expectedList.add(game2);
        expectedList.add(game3);

        // register a user to get an authToken, get list of games and compare
        var gameList = gameService.list(new ListRequest(authToken)).games();

        Assertions.assertEquals(expectedList, gameList);
    }

    @Test
    void listAllGamesInvalidAuthToken() {
        // try to get list of games with a bad authToken
        var listRequest = new ListRequest("badAuthToken");
        Assertions.assertThrows(InvalidAuthTokenException.class, () -> gameService.list(listRequest));
    }

    @Test
    void createGame() {
        // successfully create a new game
        try {
            var createResult = gameService.create(new CreateRequest(authToken, "New Game"));

            Assertions.assertEquals(gameDB.getGame(4), gameDB.getGame(createResult.gameID()));

        } catch (DataAccessException e) {
            Assertions.fail("DataAccessException occurred");
        }
    }

    @Test
    void createGameFail() {
        // invalid authToken
        var createRequest = new CreateRequest("badAuthToken", authToken);
        Assertions.assertThrows(InvalidAuthTokenException.class, () -> gameService.create(createRequest));
    }

    @Test
    void join() {
        // try joining game 3
        var game3 = new GameData(3, "player5", "bob", "game3", new ChessGame());

        try {
            gameService.join(new JoinRequest(authToken, ChessGame.TeamColor.BLACK, 3));

            Assertions.assertEquals(game3, gameDB.getGame(3));

        } catch (DataAccessException e) {
            Assertions.fail("DataAccessException occurred");
        }
    }

    @Test
    void joinFail() {
        // try joining with different issues every time
        // no authToken
        var joinRequest1 = new JoinRequest("badAuthToken", ChessGame.TeamColor.BLACK, 3);
        Assertions.assertThrows(InvalidAuthTokenException.class, () -> gameService.join(joinRequest1));

        // missing field
        var joinRequest2 = new JoinRequest(authToken, null, 3);
        Assertions.assertThrows(DataAccessException.class, () -> gameService.join(joinRequest2));

        // invalid game ID
        var joinRequest3 = new JoinRequest(authToken, ChessGame.TeamColor.BLACK, 123);
        Assertions.assertThrows(DataAccessException.class, () -> gameService.join(joinRequest3));

        // color already taken
        var joinRequest4 = new JoinRequest(authToken, ChessGame.TeamColor.WHITE, 3);
        Assertions.assertThrows(AlreadyTakenException.class, () -> gameService.join(joinRequest4));
    }
}