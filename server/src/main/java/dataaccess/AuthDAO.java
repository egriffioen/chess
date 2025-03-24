package dataaccess;

import exception.ResponseException;
import model.AuthData;

import java.util.HashMap;

public interface AuthDAO {
    void addAuthToken(AuthData authData) throws ResponseException;
    void removeAuthToken(AuthData authData) throws ResponseException;
    AuthData getAuthToken(String authToken) throws ResponseException;
    void clearAllAuthData() throws ResponseException;
    public HashMap<String, AuthData> getAuthTokens() throws ResponseException;

}
