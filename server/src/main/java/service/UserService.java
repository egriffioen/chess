package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.LogoutResult;
import result.RegisterResult;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Objects;
import java.util.UUID;

public class UserService {
    private UserDAO users;
    private AuthDAO authTokens;

    public UserService(AuthDAO authTokens, UserDAO users) {
        this.users = users;
        this.authTokens = authTokens;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        String username = registerRequest.username();
        String hashedPassword = BCrypt.hashpw(registerRequest.password(), BCrypt.gensalt());
        UserData user = users.getUser(username);
        if (user == null) {
            UserData newUser = new UserData(registerRequest.username(), hashedPassword, registerRequest.email());
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

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        UserData user = users.getUser(loginRequest.username());
        if (user == null) {
            return new LoginResult("Error: unauthorized");
        }

        else if (BCrypt.checkpw(loginRequest.password(), user.password())) {
            String authToken = UUID.randomUUID().toString();
            AuthData authData = new AuthData(authToken, user.username());
            authTokens.addAuthToken(authData);
            return new LoginResult(loginRequest.username(), authToken);
        }
        else {
            return new LoginResult("Error: unauthorized");
        }
    }

    public LogoutResult logout(LogoutRequest logoutRequest) {
        AuthData authData = authTokens.getAuthToken(logoutRequest.authToken());
        if (authData == null) {
            return new LogoutResult("Error: unauthorized");
        }
        else {
            authTokens.removeAuthToken(authData);
            return new LogoutResult();
        }
    }

    public void clearAllUsersAndTokens() throws DataAccessException {
        users.clearAllUserData();
        authTokens.clearAllAuthData();
    }
}
