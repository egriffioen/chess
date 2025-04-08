package handler;

import com.google.gson.Gson;
import exception.ResponseException;
import request.LeaveGameRequest;
import result.LeaveGameResult;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Objects;

public class LeaveGameHandler implements Route {
    private final GameService gameService;

    public LeaveGameHandler(GameService gameService) {
        this.gameService = gameService;
    }
    @Override
    public Object handle(Request req, Response res) throws Exception {
        String authToken = req.headers("authorization");
        LeaveGameRequest leaveGameRequest = new Gson().fromJson(req.body(), LeaveGameRequest.class);
        leaveGameRequest = new LeaveGameRequest(authToken, leaveGameRequest.playerColor(), leaveGameRequest.gameID());
        if (leaveGameRequest.authToken()==null || leaveGameRequest.gameID()==null) {
            throw new ResponseException(400, "Error: bad request");
        }
        if (!Objects.equals(leaveGameRequest.playerColor(), "BLACK") && !Objects.equals(leaveGameRequest.playerColor(), "WHITE")) {
            throw new ResponseException(400, "Error: bad request --> choose WHITE or BLACK");
        }
        LeaveGameResult leaveGameResult = gameService.leaveGame(leaveGameRequest);
        if (Objects.equals(leaveGameResult.message(), "Error: unauthorized")) {
            res.status(401);
            return new Gson().toJson(leaveGameResult);
        }
        else if (Objects.equals(leaveGameResult.message(), "Error: already taken")) {
            res.status(403);
            return new Gson().toJson(leaveGameResult);
        }
        else {
            res.status(200);
            return new Gson().toJson(leaveGameResult);
        }
    }
}
