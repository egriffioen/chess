package handler;

import com.google.gson.Gson;
import exception.ResponseException;
import request.JoinGameRequest;
import result.JoinGameResult;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Objects;

public class JoinGameHandler implements Route {
    private final GameService gameService;

    public JoinGameHandler(GameService gameService) {
        this.gameService = gameService;
    }
    @Override
    public Object handle(Request req, Response res) throws Exception {
        String authToken = req.headers("authorization");
        JoinGameRequest joinGameRequest = new Gson().fromJson(req.body(), JoinGameRequest.class);
        joinGameRequest = new JoinGameRequest(authToken, joinGameRequest.playerColor(), joinGameRequest.gameID());
        if (joinGameRequest.authToken()==null || joinGameRequest.gameID()==null) {
            throw new ResponseException(400, "Error: bad request");
//            JoinGameResult joinGameResult = new JoinGameResult("Error: bad request");
//            res.status(400);
//            return new Gson().toJson(joinGameResult);
        }
        if (!Objects.equals(joinGameRequest.playerColor(), "BLACK") && !Objects.equals(joinGameRequest.playerColor(), "WHITE")) {
//            JoinGameResult joinGameResult = new JoinGameResult("Error: bad request");
//            res.status(400);
//            return new Gson().toJson(joinGameResult);
            throw new ResponseException(400, "Error: bad request --> choose WHITE or BLACK");
        }
        JoinGameResult joinGameResult = gameService.joinGame(joinGameRequest);
        if (Objects.equals(joinGameResult.message(), "Error: unauthorized")) {
            res.status(401);
            return new Gson().toJson(joinGameResult);
        }
        else if (Objects.equals(joinGameResult.message(), "Error: already taken")) {
            res.status(403);
            return new Gson().toJson(joinGameResult);
        }
        else {
            res.status(200);
            return new Gson().toJson(joinGameResult);
        }
    }
}
