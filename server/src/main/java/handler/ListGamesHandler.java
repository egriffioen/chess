package handler;

import com.google.gson.Gson;
import exception.ResponseException;
import request.ListGamesRequest;
import result.ListGamesResult;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;

public class ListGamesHandler implements Route {
    private final GameService gameService;

    public ListGamesHandler(GameService gameService) {
        this.gameService = gameService;
    }


    @Override
    public Object handle(Request req, Response res) throws Exception {
        String authToken = req.headers("authorization");
        ListGamesRequest listGamesRequest = new ListGamesRequest(authToken);
        if (listGamesRequest.authToken()==null) {
//            ListGamesResult listGamesResult = new ListGamesResult("Error: bad request");
//            res.status(400);
//            return new Gson().toJson(listGamesResult);
            throw new ResponseException(400, "Error: bad request");
        }
        ListGamesResult listGamesResult = gameService.listGames(listGamesRequest);
        if (listGamesResult.message()!=null) {
            res.status(401);;
            return new Gson().toJson(listGamesResult);
        }
        else {
            res.status(200);
            return new Gson().toJson(listGamesResult);
        }
    }
}
