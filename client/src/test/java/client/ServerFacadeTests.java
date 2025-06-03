package client;

import dataaccess.mysql.MySqlAuthDAO;
import dataaccess.mysql.MySqlGameDAO;
import dataaccess.mysql.MySqlUserDAO;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

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
        new MySqlGameDAO().clearGameData();
        new MySqlAuthDAO().clearAuthData();
        new MySqlUserDAO().clearUserData();
    }

    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    void register() {

    }

    @Test
    void registerFail() {

    }

    @Test
    void login() {

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
