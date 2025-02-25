package handler;

import com.google.gson.Gson;
import request.RegisterRequest;
import result.RegisterResult;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class RegisterHandler implements Route {
    @Override
    public Object handle(Request req, Response res) throws Exception {
        RegisterRequest registerRequest = new Gson().fromJson(req.body(), RegisterRequest.class);
        UserService userService = new UserService();
        RegisterResult registerResult = userService.register(registerRequest);
        return new Gson().toJson(registerResult);
    }
}
