package dataaccess;

import model.AuthData;

import java.util.HashMap;

public interface AuthDAO {
    void addAuthToken(AuthData authData) throws DataAccessException;
    void removeAuthToken(AuthData authData) throws DataAccessException;
    AuthData getAuthToken(String authToken);
    void clearAllAuthData() throws DataAccessException;
    public HashMap<String, AuthData> getAuthTokens();

}
