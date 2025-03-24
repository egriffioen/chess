package dataaccess;

import java.util.HashMap;

import exception.ResponseException;
import model.UserData;

public interface UserDAO {
    void addUser(UserData user) throws ResponseException, DataAccessException;
    UserData getUser(String username) throws ResponseException;
    String getPassword(String username) throws ResponseException;
    void clearAllUserData() throws ResponseException, DataAccessException;
    public HashMap<String, UserData> getUsers() throws ResponseException;
}
