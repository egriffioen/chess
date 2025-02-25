package dataaccess;

import model.AuthData;

public interface AuthDAO {
    void createAuthToken(AuthData authData);
    void removeAuthToken(AuthData authData);
    AuthData getAuthToken(AuthData authData);

}
