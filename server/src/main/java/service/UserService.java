package service;

import dataaccess.*;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.LogoutResult;
import result.RegisterResult;
import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;

public class UserService {
    private UserDAO users;
    private AuthDAO authTokens;

    public UserService(AuthDAO authTokens, UserDAO users) {
        this.users = users;
        this.authTokens = authTokens;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException, ResponseException {
        String username = registerRequest.username();
        String hashedPassword = BCrypt.hashpw(registerRequest.password(), BCrypt.gensalt());
        UserData user = null;
        user = users.getUser(username);
        if (user == null) {
            UserData newUser = new UserData(registerRequest.username(), hashedPassword, registerRequest.email());
            users.addUser(newUser);
            String authToken = UUID.randomUUID().toString();
            AuthData authData = new AuthData(authToken, username);
            authTokens.addAuthToken(authData);
            return new RegisterResult(username, authToken);
        }
        else {
            throw new ResponseException(403, "Error: already taken");
            //return new RegisterResult("Error: already taken");
        }
    }

    public LoginResult login(LoginRequest loginRequest) throws ResponseException, DataAccessException {
        UserData user = null;
            user = users.getUser(loginRequest.username());
        if (user == null) {
            throw new ResponseException(401, "Error: unauthorized");
            //return new LoginResult("Error: unauthorized");
        }

        else if (BCrypt.checkpw(loginRequest.password(), user.password())) {
            String authToken = UUID.randomUUID().toString();
            AuthData authData = new AuthData(authToken, user.username());
            authTokens.addAuthToken(authData);
            return new LoginResult(loginRequest.username(), authToken);
        }
        else {
            throw new ResponseException(401, "Error: unauthorized");
            //return new LoginResult("Error: unauthorized");
        }
    }

    public LogoutResult logout(LogoutRequest logoutRequest) throws ResponseException, DataAccessException {
        AuthData authData = null;
        authData = authTokens.getAuthToken(logoutRequest.authToken());
        if (authData == null) {
            throw new ResponseException(401, "Error: unauthorized");
            //return new LogoutResult("Error: unauthorized");
        }
        else {
            authTokens.removeAuthToken(authData);
            return new LogoutResult();
        }
    }

    public void clearAllUsersAndTokens() throws ResponseException, DataAccessException {
            users.clearAllUserData();
            authTokens.clearAllAuthData();
    }
}
