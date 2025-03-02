package handler;

import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class ClearHandler implements Route {
    private final UserService userService;
    private final GameService gameService;

    public ClearHandler(UserService userService, GameService gameService) {
        this.userService = userService;
        this.gameService = gameService;;
    }

    @Override
    public Object handle(Request req, Response res) throws Exception {
        userService.clearAllUsersAndTokens();
        gameService.clearAllGames();
        return "";
    }
}
