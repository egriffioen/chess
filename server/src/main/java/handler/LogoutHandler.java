package handler;

import com.google.gson.Gson;
import exception.ResponseException;
import request.LogoutRequest;
import result.LogoutResult;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class LogoutHandler implements Route {
    private final UserService userService;

    public LogoutHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Object handle(Request req, Response res) throws Exception {
        String authToken = req.headers("authorization");
        LogoutRequest logoutRequest = new LogoutRequest(authToken);
        if (logoutRequest.authToken()==null) {
            throw new ResponseException(500, "Error: bad request");
//            LogoutResult logoutResult = new LogoutResult("Error: bad request");
//            res.status(500);
//            return new Gson().toJson(logoutResult);
        }
        LogoutResult logoutResult = userService.logout(logoutRequest);
        if (logoutResult.message()!=null) {
            res.status(401);
            return new Gson().toJson(logoutResult);
        }
        else {
            res.status(200);
            return "";
        }
    }
}
