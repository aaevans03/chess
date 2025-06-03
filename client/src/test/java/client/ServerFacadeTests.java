package client;

import dataaccess.MySqlTestHelper;
import dataaccess.mysql.MySqlAuthDAO;
import dataaccess.mysql.MySqlGameDAO;
import dataaccess.mysql.MySqlUserDAO;
import model.UserData;
import org.junit.jupiter.api.*;
import server.ResponseException;
import server.Server;
import server.ServerFacade;

import java.sql.SQLException;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    MySqlUserDAO userDB;
    MySqlAuthDAO authDB;
    MySqlGameDAO gameDB;

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
    void beforeEach() {
        gameDB = new MySqlGameDAO();
        gameDB.clearGameData();

        authDB = new MySqlAuthDAO();
        authDB.clearAuthData();

        userDB = new MySqlUserDAO();
        userDB.clearUserData();
    }

    @Test
    void register() throws ResponseException, SQLException {
        var response = facade.register("name", "password", "email@mail.net");

        Assertions.assertEquals("name", response.username());
        Assertions.assertEquals(authDB.getAuthDataWithUsername("name").authToken(), response.authToken());

        var expected = new UserData("name", "password", "email@mail.net");
        var actual = userDB.getUser("name");

        MySqlTestHelper.checkUserData(expected, actual);
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
        Assertions.assertEquals(authDB.getAuthDataWithUsername("player2").authToken(), response.authToken());
    }

    @Test
    void loginFail() {
        
    }

    @Test
    void logout() {

    }

    @Test
    void logoutFail() {

    }

    @Test
    void listGames() {

    }

    @Test
    void listGamesFail() {

    }

    @Test
    void create() {

    }

    @Test
    void createFail() {

    }

    @Test
    void join() {

    }

    @Test
    void joinFail() {

    }
}
