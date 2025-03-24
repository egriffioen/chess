package dataaccess;

import exception.ResponseException;
import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO{
    private HashMap<String, UserData> users = new HashMap<>();
    @Override
    public void addUser(UserData user) throws ResponseException {
        String username = user.username();
        users.put(username, user);
    }

    @Override
    public UserData getUser(String username) throws ResponseException {
        return users.get(username);
    }

    @Override
    public String getPassword(String username) throws ResponseException {
        UserData user = null;
        try {
            user = getUser(username);
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        return user.password();
    }

    @Override
    public void clearAllUserData() throws ResponseException {
        users.clear();
    }

    public HashMap<String, UserData> getUsers() throws ResponseException {
        return users;
    }
}
