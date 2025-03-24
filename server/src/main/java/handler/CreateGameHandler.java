package handler;

import com.google.gson.Gson;
import exception.ResponseException;
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
            throw new ResponseException(400, "Error: bad request");
        }
        CreateGameResult createGameResult = gameService.createGame(createGameRequest);
        if (createGameResult.message()!=null) {
            throw new ResponseException(401, "Error: unauthorized");
        }
        else {
            res.status(200);
            return new Gson().toJson(createGameResult);
        }
    }
}
