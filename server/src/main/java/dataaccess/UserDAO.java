package dataaccess;

import java.util.HashMap;
import model.UserData;

public interface UserDAO {
    void addUser(UserData user) throws DataAccessException;
    UserData getUser(String username);
    String getPassword(String username);
    void clearAllUserData();
    public HashMap<String, UserData> getUsers();
}
