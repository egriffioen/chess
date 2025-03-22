package client;

import exception.ResponseException;
import facade.ServerFacade;
import org.junit.jupiter.api.*;
import request.RegisterRequest;
import result.RegisterResult;
import server.Server;

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
    public void sampleTest() {
        Assertions.assertTrue(true);
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



}
