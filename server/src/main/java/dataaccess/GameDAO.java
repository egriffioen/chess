package dataaccess;

import java.util.List;
import java.util.Map;

public interface GameDAO {
    int createGame(String gameName);
    public List<Map<String, Object>> listGames();
    void clearAllGames();
    boolean joinGame(String playerColor, Integer gameID, String username);
}
