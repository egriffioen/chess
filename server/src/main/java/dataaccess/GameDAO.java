package dataaccess;

import model.GameData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface GameDAO {
    int createGame(String gameName);
    public List<Map<String, Object>> listGames();
    void clearAllGames();
    boolean joinGame(String playerColor, Integer gameID, String username);
    public HashMap<Integer, GameData> getGames();
    public GameData getGame(Integer gameID);
}
