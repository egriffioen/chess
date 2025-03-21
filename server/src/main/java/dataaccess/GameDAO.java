package dataaccess;

import model.GameData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface GameDAO {
    int createGame(String gameName) throws DataAccessException;
    public List<Map<String, Object>> listGames() throws DataAccessException;
    void clearAllGames() throws DataAccessException;
    boolean joinGame(String playerColor, Integer gameID, String username) throws DataAccessException;
    public HashMap<Integer, GameData> getGames() throws DataAccessException;
    public GameData getGame(Integer gameID) throws DataAccessException;
}
