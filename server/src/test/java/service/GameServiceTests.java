package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTests {

    MemoryUserDAO userDB;
    MemoryAuthDAO authDB;
    MemoryGameDAO gameDB;

    @BeforeEach
    void setUp() {
        userDB = new MemoryUserDAO();
        authDB = new MemoryAuthDAO();
        gameDB = new MemoryGameDAO();
    }

    @Test
    void listAllGames() {
        // create users, get their auth tokens, and
    }

    @Test
    void create() {
    }

    @Test
    void join() {
    }
}