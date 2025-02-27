package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.HashMap;

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
}
