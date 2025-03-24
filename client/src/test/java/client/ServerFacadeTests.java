package client;

import exception.ResponseException;
import facade.ServerFacade;
import org.junit.jupiter.api.*;
import request.*;
import result.*;
import server.Server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        var serverUrl = "http://localhost:" + port;
        //System.out.println(serverUrl);
        facade = new ServerFacade(serverUrl);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void clearDatabase() throws ResponseException {
        facade.clear();
    }

    @Test
    void validRegister() throws Exception {
        RegisterRequest request = new RegisterRequest("player1", "password", "p1@email.com");
        RegisterResult actualResult = facade.register(request);
        assertEquals("player1", actualResult.username());
    }
    @Test
    void invalidRegister() throws Exception {
        RegisterRequest request = new RegisterRequest("player1", null, "p1@email.com");
        assertThrows(ResponseException.class, () -> facade.register(request));
    }

    @Test
    void validLogin() throws Exception {
        RegisterRequest request = new RegisterRequest("player1", "password", "p1@email.com");
        RegisterResult registerResult = facade.register(request);
        LoginRequest loginRequest = new LoginRequest("player1", "password");
        LoginResult loginResult = facade.login(loginRequest);
        assertEquals("player1", loginResult.username());
    }

    @Test
    void invalidLogin() throws Exception {
        LoginRequest loginRequest = new LoginRequest("player1", "password");
        assertThrows(ResponseException.class, () -> facade.login(loginRequest));
    }

    @Test
    void validLogout() throws Exception {
        RegisterRequest request = new RegisterRequest("player1", "password", "p1@email.com");
        RegisterResult registerResult = facade.register(request);
        LogoutRequest logoutRequest = new LogoutRequest(registerResult.authToken());
        LogoutResult logoutResult = facade.logout(logoutRequest);
        assertNull(logoutResult);
    }

    @Test
    void invalidLogout() throws Exception {
        RegisterRequest request = new RegisterRequest("player1", "password", "p1@email.com");
        RegisterResult registerResult = facade.register(request);
        LogoutRequest logoutRequest = new LogoutRequest(registerResult.authToken());
        LogoutResult logoutResult = facade.logout(logoutRequest);
        assertThrows(ResponseException.class, () -> facade.logout(logoutRequest));
    }

    @Test
    void validCreateGame() throws Exception {
        RegisterRequest request = new RegisterRequest("player1", "password", "p1@email.com");
        RegisterResult registerResult = facade.register(request);
        CreateGameRequest createGameRequest = new CreateGameRequest(registerResult.authToken(), "game1");
        CreateGameResult createGameResult = facade.createGame(createGameRequest);
        assertNull(createGameResult.message());
        assertEquals(1, createGameResult.gameID());
    }

    @Test
    void invalidCreateGame() throws Exception {
        RegisterRequest request = new RegisterRequest("player1", "password", "p1@email.com");
        RegisterResult registerResult = facade.register(request);
        CreateGameRequest createGameRequest = new CreateGameRequest(registerResult.authToken(), null);
        assertThrows(ResponseException.class, () -> facade.createGame(createGameRequest));
    }

    @Test
    void validListGames() throws Exception {
        RegisterRequest request = new RegisterRequest("player1", "password", "p1@email.com");
        RegisterResult registerResult = facade.register(request);
        CreateGameRequest createGameRequest = new CreateGameRequest(registerResult.authToken(), "game1");
        CreateGameResult createGameResult = facade.createGame(createGameRequest);
        ListGamesRequest listGamesRequest = new ListGamesRequest(registerResult.authToken());
        ListGamesResult listGamesResult = facade.listGames(listGamesRequest);

        assertEquals(1, listGamesResult.games().size());
    }

    @Test
    void invalidListGames() throws Exception {
        RegisterRequest request = new RegisterRequest("player1", "password", "p1@email.com");
        RegisterResult registerResult = facade.register(request);
        CreateGameRequest createGameRequest = new CreateGameRequest(registerResult.authToken(), "game1");
        CreateGameResult createGameResult = facade.createGame(createGameRequest);
        ListGamesRequest listGamesRequest = new ListGamesRequest(null);
        assertThrows(ResponseException.class, () -> facade.listGames(listGamesRequest));
    }


    @Test
    void validJoinGame() throws Exception {
        RegisterRequest request = new RegisterRequest("player1", "password", "p1@email.com");
        RegisterResult registerResult = facade.register(request);
        CreateGameRequest createGameRequest = new CreateGameRequest(registerResult.authToken(), "game1");
        CreateGameResult createGameResult = facade.createGame(createGameRequest);
        JoinGameRequest joinGameRequest = new JoinGameRequest(registerResult.authToken(), "WHITE", createGameResult.gameID());
        JoinGameResult joinGameResult = facade.joinGame(joinGameRequest);
        assertNull(joinGameResult.message());
    }

    @Test
    void invalidJoinGame() throws Exception {
        RegisterRequest request = new RegisterRequest("player1", "password", "p1@email.com");
        RegisterResult registerResult = facade.register(request);
        CreateGameRequest createGameRequest = new CreateGameRequest(registerResult.authToken(), "game1");
        CreateGameResult createGameResult = facade.createGame(createGameRequest);
        JoinGameRequest joinGameRequest = new JoinGameRequest(registerResult.authToken(), "green", createGameResult.gameID());
        assertThrows(ResponseException.class, () -> facade.joinGame(joinGameRequest));
    }

    @Test
    void validClear() throws Exception {
        RegisterRequest request = new RegisterRequest("player1", "password", "p1@email.com");
        RegisterResult registerResult = facade.register(request);
        CreateGameRequest createGameRequest = new CreateGameRequest(registerResult.authToken(), "game1");
        CreateGameResult createGameResult = facade.createGame(createGameRequest);

        facade.clear();
        ListGamesRequest listGamesRequest = new ListGamesRequest(registerResult.authToken());
        //ListGamesResult listGamesResult = facade.listGames(listGamesRequest);
        LoginRequest loginRequest = new LoginRequest("player1", "password");
        assertThrows(ResponseException.class, () -> facade.login(loginRequest));
        assertThrows(ResponseException.class, () -> facade.listGames(listGamesRequest));
    }
}
