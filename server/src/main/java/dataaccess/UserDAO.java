package dataaccess;

import java.util.HashMap;
import model.UserData;

public interface UserDAO {
    void addUser(UserData user) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    String getPassword(String username) throws DataAccessException;
    void clearAllUserData() throws DataAccessException;
    public HashMap<String, UserData> getUsers() throws DataAccessException;
}
