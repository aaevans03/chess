package client;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.*;
import server.Server;
import serverfacade.ResponseException;
import serverfacade.ServerFacade;

import java.util.ArrayList;

public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    private String authToken1 = null;
    private String authToken2 = null;
    private String authToken3 = null;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void beforeEach() throws ResponseException {
        facade.clear();
    }

    private ArrayList<GameData> createDummyData() throws ResponseException {
        authToken1 = facade.register("user1", "password", "e@mail.com").authToken();
        authToken2 = facade.register("user2", "password", "e@mail.com").authToken();
        authToken3 = facade.register("user3", "password", "e@mail.com").authToken();

        facade.create(authToken1, "game1");
        facade.create(authToken2, "game2");
        facade.create(authToken3, "game3");

        facade.join(authToken1, ChessGame.TeamColor.WHITE, 1);
        facade.join(authToken2, ChessGame.TeamColor.BLACK, 2);
        facade.join(authToken3, ChessGame.TeamColor.WHITE, 3);

        var gameList = new ArrayList<GameData>();

        gameList.add(new GameData(1, "user1", null, "game1", new ChessGame()));
        gameList.add(new GameData(2, null, "user2", "game2", new ChessGame()));
        gameList.add(new GameData(3, "user3", null, "game3", new ChessGame()));

        return gameList;
    }

    @Test
    void register() throws ResponseException {
        var response = facade.register("name", "password", "email@mail.net");

        Assertions.assertEquals("name", response.username());
        Assertions.assertTrue(response.authToken().length() > 10);
        Assertions.assertDoesNotThrow(() -> facade.logout(response.authToken()));
    }

    @Test
    void registerFail() throws ResponseException {
        facade.register("player1", "password", "mail@address.com");

        Assertions.assertThrows(ResponseException.class, () -> {
            facade.register(null, null, null);
        });
        Assertions.assertThrows(ResponseException.class, () -> {
            facade.register("player1", "password", "mail@address.com");
        });

        try {
            facade.register(null, null, null);
        } catch (ResponseException ex) {
            Assertions.assertEquals(400, ex.getStatusCode());
        }
        try {
            facade.register("player1", "password", "mail@address.com");
        } catch (ResponseException ex) {
            Assertions.assertEquals(403, ex.getStatusCode());
        }
    }

    @Test
    void login() throws ResponseException {
        var regResponse = facade.register("player2", "password2", "mail@address.com");
        facade.logout(regResponse.authToken());

        var response = facade.login("player2", "password2");

        Assertions.assertTrue(response.authToken().length() > 10);
        Assertions.assertEquals("player2", response.username());

        Assertions.assertDoesNotThrow(() -> facade.logout(response.authToken()));
    }

    @Test
    void loginFail() {
        Assertions.assertThrows(ResponseException.class, () -> {
            facade.login("fake_user", "password");
        });

        try {
            facade.login("fake_user", "password");
        } catch (ResponseException ex) {
            Assertions.assertEquals(401, ex.getStatusCode());
        }

        Assertions.assertThrows(ResponseException.class, () -> facade.login("fake_user", null));
        Assertions.assertThrows(ResponseException.class, () -> facade.login(null, null));
    }

    @Test
    void logout() throws ResponseException {
        var regResponse = facade.register("new_user", "new_password", "new_email@mail.com");
        facade.logout(regResponse.authToken());

        var response = facade.login("new_user", "new_password");
        Assertions.assertDoesNotThrow(() -> facade.logout(response.authToken()));
    }

    @Test
    void logoutFail() {
        Assertions.assertThrows(ResponseException.class, () -> facade.logout(null));
        Assertions.assertThrows(ResponseException.class, () -> facade.logout("fake-auth-token"));
    }

    @Test
    void listGames() throws ResponseException {
        var expectedData = createDummyData();

        var response = facade.listGames(authToken1);

        Assertions.assertEquals(3, response.size());
        Assertions.assertEquals(expectedData, response);
    }

    @Test
    void listGamesFail() {
        Assertions.assertThrows(ResponseException.class, () -> facade.listGames("fake-auth-token"));
    }

    @Test
    void create() throws ResponseException {
        var expectedData = createDummyData();
        expectedData.add(new GameData(4, null, null,
                "myGame", new ChessGame()));

        var response = facade.create(authToken1, "myGame");

        Assertions.assertEquals(4, response);
        Assertions.assertEquals(expectedData, facade.listGames(authToken1));
    }

    @Test
    void createFail() {
        Assertions.assertThrows(ResponseException.class, () -> {
            facade.create("fake-auth-token", "fake-name");
        });
        Assertions.assertThrows(ResponseException.class, () -> {
            facade.create("fake-auth-token", null);
        });
    }

    @Test
    void join() throws ResponseException {
        createDummyData();

        Assertions.assertDoesNotThrow(() -> facade.join(authToken2, ChessGame.TeamColor.BLACK, 1));
        Assertions.assertDoesNotThrow(() -> facade.join(authToken3, ChessGame.TeamColor.WHITE, 2));
        Assertions.assertDoesNotThrow(() -> facade.join(authToken1, ChessGame.TeamColor.BLACK, 3));
    }

    @Test
    void joinFail() throws ResponseException {
        createDummyData();

        Assertions.assertThrows(ResponseException.class, () -> {
            facade.join("fake-auth-token", ChessGame.TeamColor.WHITE, 1);
        });
        Assertions.assertThrows(ResponseException.class, () -> {
            facade.join(authToken1, ChessGame.TeamColor.WHITE, 1);
        });
        Assertions.assertThrows(ResponseException.class, () -> {
            facade.join(authToken2, ChessGame.TeamColor.BLACK, 0);
        });
        Assertions.assertThrows(ResponseException.class, () -> {
            facade.join(authToken3, null, 0);
        });
    }

    @Test
    void clear() throws ResponseException {
        createDummyData();
        facade.clear();

        Assertions.assertThrows(ResponseException.class, () -> {
            facade.login("user1", "password");
        });

        var result = facade.register("only-user", "password", "mail@e.com");
        facade.create(result.authToken(), "game");

        Assertions.assertEquals(1, facade.listGames(result.authToken()).size());
    }
}
