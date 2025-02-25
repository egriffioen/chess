package dataaccess;

import java.util.HashMap;
import model.UserData;

public interface UserDAO {
    void addUser(UserData user);
    UserData getUser(String username);
    String getPassword(String username);
    void clearAllUserData();
}
