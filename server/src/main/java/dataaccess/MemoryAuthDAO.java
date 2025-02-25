package dataaccess;

import java.util.HashMap;
import java.util.UUID;
import model.AuthData;
import model.UserData;

public class MemoryAuthDAO implements AuthDAO{
    private HashMap<String, AuthData> authTokens = new HashMap<>();
    @Override
    public void addAuthToken(AuthData authData) {
        authTokens.put(authData.authToken(), authData);
    }

    @Override
    public void removeAuthToken(AuthData authData) {
        authTokens.remove(authData.authToken());
    }

    @Override
    public AuthData getAuthToken(AuthData authData) {
        return authTokens.get(authData.authToken());
    }
}
