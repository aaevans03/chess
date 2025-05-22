package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.exceptions.InvalidAuthTokenException;
import service.request.CreateRequest;
import service.request.ListRequest;

import java.util.ArrayList;

class GameServiceTests {

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
        authToken = registerUser1();
    }

    String registerUser1() {
        return authDB.createAuthData("bob");
    }

    void dummyGameList() {
        gameDB.resetIterator();
        gameDB.createGame("game1");
        gameDB.updateGame(1, "player1", ChessGame.TeamColor.WHITE);
        gameDB.updateGame(1, "player2", ChessGame.TeamColor.BLACK);
        gameDB.createGame("game2");
        gameDB.updateGame(2, "player3", ChessGame.TeamColor.WHITE);
        gameDB.updateGame(2, "player4", ChessGame.TeamColor.BLACK);
        gameDB.createGame("game3");
        gameDB.updateGame(3, "player5", ChessGame.TeamColor.WHITE);
        gameDB.updateGame(3, "player6", ChessGame.TeamColor.BLACK);
    }

    @Test
    void listAllGames() {
        // create a list of games, make a list manually
        var game1 = new GameData(1, "player1", "player2", "game1", new ChessGame());
        var game2 = new GameData(2, "player3", "player4", "game2", new ChessGame());
        var game3 = new GameData(3, "player5", "player6", "game3", new ChessGame());

        var expectedList = new ArrayList<GameData>();
        expectedList.add(game1);
        expectedList.add(game2);
        expectedList.add(game3);

        // register a user to get an authToken, get list of games and compare
        var gameService = new GameService();
        var gameList = gameService.list(new ListRequest(authToken)).games();

        Assertions.assertEquals(expectedList, gameList);
    }

    @Test
    void listAllGamesInvalidAuthToken() {
        // try to get list of games with a bad authToken
        var gameService = new GameService();
        var listRequest = new ListRequest("badAuthToken");
        Assertions.assertThrows(InvalidAuthTokenException.class, () -> gameService.list(listRequest));
    }

    @Test
    void createGame() {
        // successfully create a new game
        var gameService = new GameService();
        try {
            var createResult = gameService.create(new CreateRequest(authToken, "New Game"));

            Assertions.assertEquals(gameDB.getGame(4), gameDB.getGame(createResult.gameID()));

        } catch (DataAccessException e) {
            Assertions.fail("DataAccessException occurred");
        }
    }

    @Test
    void createGameFail() {
        var authToken = registerUser1();

        // invalid authToken
        var gameService = new GameService();
        var createRequest = new CreateRequest("badAuthToken", authToken);
        Assertions.assertThrows(InvalidAuthTokenException.class, () -> gameService.create(createRequest));
    }

    @Test
    void join() {
    }
}