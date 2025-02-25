package handler;

import com.google.gson.Gson;
import request.RegisterRequest;
import result.RegisterResult;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class RegisterHandler implements Route {
    private final UserService userService;

    public RegisterHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Object handle(Request req, Response res) throws Exception {
        RegisterRequest registerRequest = new Gson().fromJson(req.body(), RegisterRequest.class);
        if (registerRequest.username()==null || registerRequest.password()==null || registerRequest.email() == null) {
            RegisterResult registerResult = new RegisterResult("Error: bad request");
            res.status(400);
            return new Gson().toJson(registerResult);
        }
        RegisterResult registerResult = userService.register(registerRequest);
        if (registerResult.message()!=null) {
            res.status(403);
        }
        return new Gson().toJson(registerResult);
    }
}
