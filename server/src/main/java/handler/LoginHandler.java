package handler;

import com.google.gson.Gson;
import exception.ResponseException;
import request.LoginRequest;
import result.LoginResult;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class LoginHandler implements Route {
    private final UserService userService;

    public LoginHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Object handle(Request req, Response res) throws Exception {
        LoginRequest loginRequest = new Gson().fromJson(req.body(), LoginRequest.class);
        if (loginRequest.username()==null || loginRequest.password()==null) {
            throw new ResponseException(500, "Error: bad request");
        }
        LoginResult loginResult = userService.login(loginRequest);
        if (loginResult.message()!=null) {
            res.status(401);
        }
        return new Gson().toJson(loginResult);
    }
}
