package service;

import dataaccess.AuthDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import request.RegisterRequest;
import result.RegisterResult;

import java.util.UUID;

public class UserService {
    private UserDAO users = new MemoryUserDAO();
    private AuthDAO authTokens = new MemoryAuthDAO();
    public RegisterResult register(RegisterRequest registerRequest) {
        String username = registerRequest.username();
        UserData user = users.getUser(username);
        if (user == null) {
            UserData newUser = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
            users.addUser(newUser);
            String authToken = UUID.randomUUID().toString();
            AuthData authData = new AuthData(authToken, username);
            authTokens.addAuthToken(authData);
            return new RegisterResult(username, authToken);
        }
        else {
            return new RegisterResult("Error: already taken");
        }
    }
}
