package server;

import dataaccess.*;
import exception.ResponseException;
import handler.*;
import service.GameService;
import service.UserService;
import spark.*;
import java.sql.SQLException;

public class Server {

//    private final GameService gameService;
//    private final UserService userService;
    //private final WebSocketHandler webSocketHandler;

    public Server() {
//        this.gameService = gameService;
//        this.userService = userService;
        //webSocketHandler = new WebSocketHandler();
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");


        UserDAO dbUsers = null;
        AuthDAO authTokens = null;
        GameDAO games = null;
        try {
            dbUsers = new SQLUserDAO();
        } catch (DataAccessException | SQLException e) {
            e.printStackTrace();  // Prints the error stack trace
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        try {
            authTokens = new SQLAuthDAO();
        } catch (DataAccessException | SQLException | ResponseException e) {
            e.printStackTrace();  // Prints the error stack trace
        }

        try {
            games = new SQLGameDAO();
        } catch (DataAccessException | SQLException e) {
            e.printStackTrace();  // Prints the error stack trace
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }

        UserService userService = new UserService(authTokens, dbUsers);
        GameService gameService = new GameService(authTokens, games);

        RegisterHandler registerHandler = new RegisterHandler(userService);
        LoginHandler loginHandler = new LoginHandler(userService);
        LogoutHandler logoutHandler = new LogoutHandler(userService);
        ClearHandler clearHandler = new ClearHandler(userService, gameService);
        CreateGameHandler createGameHandler = new CreateGameHandler(gameService);
        ListGamesHandler listGamesHandler = new ListGamesHandler(gameService);
        JoinGameHandler joinGameHandler = new JoinGameHandler(gameService);
        // Register your endpoints and handle exceptions here.
        Spark.post("/user", registerHandler);
        Spark.post("/session", loginHandler);
        Spark.delete("/session", logoutHandler);
        Spark.delete("/db", clearHandler);
        Spark.post("/game", createGameHandler);
        Spark.get("/game", listGamesHandler);
        Spark.put("/game", joinGameHandler);
        Spark.exception(ResponseException.class, this::exceptionHandler);
        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private void exceptionHandler(ResponseException ex, Request req, Response res) {
        res.status(ex.getStatusCode());
        res.body(ex.toJson());
    }

}
