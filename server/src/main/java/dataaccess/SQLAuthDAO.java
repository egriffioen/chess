package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class SQLAuthDAO implements AuthDAO{
    @Override
    public void addAuthToken(AuthData authData) {

    }

    @Override
    public void removeAuthToken(AuthData authData) {

    }

    @Override
    public AuthData getAuthToken(String authToken) {
        return null;
    }

    @Override
    public void clearAllAuthData() {

    }

    @Override
    public HashMap<String, AuthData> getAuthTokens() {
        return null;
    }
}
