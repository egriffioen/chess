package dataaccess;

import model.AuthData;

public interface AuthDAO {
    void addAuthToken(AuthData authData);
    void removeAuthToken(AuthData authData);
    AuthData getAuthToken(String authToken);
    void clearAllAuthData();

}
