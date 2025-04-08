package handler;

import com.google.gson.Gson;
import exception.ResponseException;
import request.UpdateGameRequest;
import result.UpdateGameResult;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;

public class UpdateGameHandler implements Route {
    private final GameService gameService;

    public UpdateGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public Object handle(Request req, Response res) throws Exception {
        String authToken = req.headers("authorization");
        UpdateGameRequest updateGameRequest = new Gson().fromJson(req.body(), UpdateGameRequest.class);
        updateGameRequest = new UpdateGameRequest(authToken, updateGameRequest.gameID(), updateGameRequest.gameData());
        if (updateGameRequest.authToken()==null || updateGameRequest.gameID()==null||updateGameRequest.gameData()==null) {
            throw new ResponseException(400, "Error: bad request");
        }
        UpdateGameResult updateGameResult = gameService.updateGame(updateGameRequest);
        if (updateGameResult.message()!=null) {
            throw new ResponseException(401, "Error: unauthorized");
        }
        else {
            res.status(200);
            return new Gson().toJson(updateGameResult);
        }
    }
}
