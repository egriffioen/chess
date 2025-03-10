package server;

import dataaccess.*;
import handler.*;
import service.GameService;
import service.UserService;
import spark.*;
import java.sql.SQLException;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        AuthDAO authTokens = new MemoryAuthDAO();
        UserDAO users = new MemoryUserDAO();
        GameDAO games = new MemoryGameDAO();
        try {
            UserDAO dbUsers = new SQLUserDAO();
        }
        catch (DataAccessException | SQLException e) {
            e.printStackTrace();  // Prints the error stack trace
        }
        UserService userService = new UserService(authTokens, users);
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
        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

}
