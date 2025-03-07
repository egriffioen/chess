package dataaccess;

import model.UserData;

import java.util.HashMap;

public class SQLUserDAO implements UserDAO {
    @Override
    public void addUser(UserData user) {

    }

    @Override
    public UserData getUser(String username) {
        return null;
    }

    @Override
    public String getPassword(String username) {
        return "";
    }

    @Override
    public void clearAllUserData() {

    }

    @Override
    public HashMap<String, UserData> getUsers() {
        return null;
    }
}
