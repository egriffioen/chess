package dataaccess;

import exception.ResponseException;
import model.GameData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface GameDAO {
    int createGame(String gameName) throws ResponseException;
    public List<Map<String, Object>> listGames() throws ResponseException;
    void clearAllGames() throws ResponseException;
    boolean joinGame(String playerColor, Integer gameID, String username) throws ResponseException;
    public HashMap<Integer, GameData> getGames() throws ResponseException;
    public GameData getGame(Integer gameID) throws ResponseException;
}
