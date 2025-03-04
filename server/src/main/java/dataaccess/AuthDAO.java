package dataaccess;

import model.AuthData;

import java.util.HashMap;

public interface AuthDAO {
    void addAuthToken(AuthData authData);
    void removeAuthToken(AuthData authData);
    AuthData getAuthToken(String authToken);
    void clearAllAuthData();
    public HashMap<String, AuthData> getAuthTokens();

}
