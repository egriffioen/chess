package handler;

import com.google.gson.Gson;
import request.LoginRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;
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
            LoginResult loginResult = new LoginResult("Error: bad request");
            res.status(500);
            return new Gson().toJson(loginResult);
        }
        LoginResult loginResult = userService.login(loginRequest);
        if (loginResult.message()!=null) {
            res.status(403);
        }
        return new Gson().toJson(loginResult);
    }
}
