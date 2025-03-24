package dataaccess;

import java.util.HashMap;

import exception.ResponseException;
import model.AuthData;

public class MemoryAuthDAO implements AuthDAO{
    private HashMap<String, AuthData> authTokens = new HashMap<>();
    @Override
    public void addAuthToken(AuthData authData) throws ResponseException {
        authTokens.put(authData.authToken(), authData);
    }

    @Override
    public void removeAuthToken(AuthData authData) throws ResponseException {
        authTokens.remove(authData.authToken());
    }

    @Override
    public AuthData getAuthToken(String authToken) throws ResponseException {
        return authTokens.get(authToken);
    }

    @Override
    public void clearAllAuthData() throws ResponseException {
        authTokens.clear();
    }

    public HashMap<String, AuthData> getAuthTokens() throws ResponseException {
        return authTokens;
    }
}
