package handler;

import com.google.gson.Gson;
import request.CreateGameRequest;
import result.CreateGameResult;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;

public class CreateGameHandler implements Route {
    private final GameService gameService;

    public CreateGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public Object handle(Request req, Response res) throws Exception {
        String authToken = req.headers("authorization");
        CreateGameRequest createGameRequest = new Gson().fromJson(req.body(), CreateGameRequest.class);
        createGameRequest = new CreateGameRequest(authToken, createGameRequest.gameName());
        if (createGameRequest.authToken()==null || createGameRequest.gameName()==null) {
            CreateGameResult createGameResult = new CreateGameResult("Error: bad request");
            res.status(400);
            return new Gson().toJson(createGameResult);
        }
        CreateGameResult createGameResult = gameService.createGame(createGameRequest);
        if (createGameResult.message()!=null) {
            res.status(401);
//            String message = createGameResult.message();
//            createGameResult = new CreateGameResult(message);
            return new Gson().toJson(createGameResult);
        }
        else {
            res.status(200);
            return new Gson().toJson(createGameResult);
        }
    }
}
