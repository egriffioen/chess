package dataaccess;

import chess.ChessGame;
import exception.ResponseException;
import model.GameData;

import java.lang.reflect.Array;
import java.util.*;

public class MemoryGameDAO implements GameDAO{
    private HashMap<Integer, GameData> games = new HashMap<>();
    private static int idCounter = 0;

    @Override
    public int createGame(String gameName) throws ResponseException {
        int gameID = ++idCounter;
        ChessGame chessGame = new ChessGame();
        GameData gameData = new GameData(gameID, null, null, gameName, chessGame);
        games.put(gameID, gameData);
        return gameID;
    }

//    public List<Map<String, Object>> listGames() throws ResponseException {
//        List<Map<String, Object>> gamesList = new ArrayList<>();
//
//        for (GameData gameData : games.values()) {
//            Map<String, Object> gameInfo = new HashMap<>();
//            gameInfo.put("gameID", gameData.gameID());
//            gameInfo.put("whiteUsername", gameData.whiteUsername());
//            gameInfo.put("blackUsername", gameData.blackUsername());
//            gameInfo.put("gameName", gameData.gameName());
//
//            gamesList.add(gameInfo);
//        }
//
//        return gamesList;
//    }

    public ArrayList<GameData> listGames() throws ResponseException {
        ArrayList<GameData> gamesList = new ArrayList<>();

        for (GameData gameData : games.values()) {
            gamesList.add(gameData);
        }

        return gamesList;
    }

    public void clearAllGames() throws ResponseException {
        games.clear();
    }

    @Override
    public boolean joinGame(String playerColor, Integer gameID, String username) throws ResponseException {
        GameData gameData = games.get(gameID);
        if (Objects.equals(playerColor, "WHITE")) {
            String whiteUsername = gameData.whiteUsername();
            if (whiteUsername == null) {
                games.remove(gameID);
                GameData newGameData = new GameData(gameID, username, gameData.blackUsername(), gameData.gameName(), gameData.game());
                games.put(gameID, newGameData);
                return true;
            }
            else {
                return false;
            }
        }
        else {
            String blackUsername = gameData.blackUsername();
            if (blackUsername == null) {
                games.remove(gameID);
                GameData newGameData = new GameData(gameID, gameData.whiteUsername(), username, gameData.gameName(), gameData.game());
                games.put(gameID, newGameData);
                return true;
            }
            else {
                return false;
            }
        }
    }

    public HashMap<Integer, GameData> getGames() throws ResponseException {
        return games;
    }

    public GameData getGame(Integer gameID) throws ResponseException {
        return games.get(gameID);
    }

    @Override
    public boolean leaveGame(String playerColor, Integer gameID, String username) throws ResponseException {
        GameData gameData = games.get(gameID);
        if (Objects.equals(playerColor, "WHITE")) {
            String whiteUsername = gameData.whiteUsername();
            if (whiteUsername == username) {
                games.remove(gameID);
                GameData newGameData = new GameData(gameID, null, gameData.blackUsername(), gameData.gameName(), gameData.game());
                games.put(gameID, newGameData);
                return true;
            }
            else {
                return false;
            }
        }
        else {
            String blackUsername = gameData.blackUsername();
            if (blackUsername == username) {
                games.remove(gameID);
                GameData newGameData = new GameData(gameID, gameData.whiteUsername(), null, gameData.gameName(), gameData.game());
                games.put(gameID, newGameData);
                return true;
            }
            else {
                return false;
            }
        }
    }
}
