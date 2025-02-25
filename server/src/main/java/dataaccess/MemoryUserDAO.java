package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO{
    private HashMap<String, UserData> users = new HashMap<>();
    @Override
    public void addUser(UserData user) {
        String username = user.username();
        users.put(username, user);
    }

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }

    @Override
    public String getPassword(String username) {
        UserData user = getUser(username);
        return user.password();
    }

    @Override
    public void clearAllUserData() {
        users.clear();
    }


}
