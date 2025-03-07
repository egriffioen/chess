package dataaccess;

import model.GameData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLGameDAO implements GameDAO{
    @Override
    public int createGame(String gameName) {
        return 0;
    }

    @Override
    public List<Map<String, Object>> listGames() {
        return List.of();
    }

    @Override
    public void clearAllGames() {

    }

    @Override
    public boolean joinGame(String playerColor, Integer gameID, String username) {
        return false;
    }

    @Override
    public HashMap<Integer, GameData> getGames() {
        return null;
    }

    @Override
    public GameData getGame(Integer gameID) {
        return null;
    }
}
