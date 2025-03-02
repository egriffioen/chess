package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryGameDAO implements GameDAO{
    private HashMap<Integer, GameData> games = new HashMap<>();
    private static int idCounter = 0;

    @Override
    public int createGame(String gameName) {
        int gameID = ++idCounter;
        ChessGame chessGame = new ChessGame();
        GameData gameData = new GameData(gameID, null, null, gameName, chessGame);
        games.put(gameID, gameData);
        return gameID;
    }

    public List<Map<String, Object>> listGames() {
        List<Map<String, Object>> gamesList = new ArrayList<>();

        for (GameData gameData : games.values()) {
            Map<String, Object> gameInfo = new HashMap<>();
            gameInfo.put("gameID", gameData.gameID());
            gameInfo.put("whiteUsername", gameData.whiteUsername());
            gameInfo.put("blackUsername", gameData.blackUsername());
            gameInfo.put("gameName", gameData.gameName());

            gamesList.add(gameInfo);
        }

//        // Wrap in the required format
//        Map<String, Object> finalResult = new HashMap<>();
//        finalResult.put("games", gamesList);
//
        return gamesList;
    }

    public void clearAllGames() {
        games.clear();
    }
}
