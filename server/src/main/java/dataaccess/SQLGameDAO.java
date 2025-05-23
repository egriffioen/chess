package dataaccess;

import chess.ChessGame;
import chess.ChessPosition;
import com.google.gson.Gson;
import exception.ResponseException;
import model.GameData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLGameDAO extends DatabaseConfigurations implements GameDAO{

    public SQLGameDAO() throws ResponseException, DataAccessException, SQLException {
        configureDatabase(createStatements);
    }

    @Override
    public int createGame(String gameName) throws ResponseException {
        var statement = "INSERT INTO games (whiteUsername, blackUsername, gameName, json) VALUES (?,?,?,?)";
        ChessGame chessGame = new ChessGame();
        var json = new Gson().toJson(chessGame);
        var id = executeUpdate(statement, null, null, gameName, json);
        return id;
    }

    @Override
    //public List<Map<String, Object>> listGames() throws ResponseException {
    public ArrayList<GameData> listGames() throws ResponseException {
        //var gamesList = new ArrayList<Map<String, Object>>();
        var result = new ArrayList<GameData>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, json FROM games";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readGame(rs));
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(500, String.format("Unable to list data: %s", e.getMessage()));
        }
        //return gamesList;
        return result;

    }

    @Override
    public void clearAllGames() throws ResponseException {
        var statement = "TRUNCATE games";
        executeUpdate(statement);
    }

    @Override
    public boolean joinGame(String playerColor, Integer gameID, String username) throws ResponseException {
        GameData gameData = getGame(gameID);
        if (Objects.equals(playerColor, "WHITE")) {
            String whiteUsername = gameData.whiteUsername();
            if (whiteUsername == null) {
                var statement = "UPDATE games SET whiteUsername = ? WHERE gameID = ? AND whiteUsername IS NULL";
                int rowsUpdated = executeUpdate(statement, username, gameID);
                return true;
            }
            else {
                return false;
            }
        }
        else {
            String blackUsername = gameData.blackUsername();
            if (blackUsername == null) {
                var statement = "UPDATE games SET blackUsername = ? WHERE gameID = ? AND blackUsername IS NULL";
                int rowsUpdated = executeUpdate(statement, username, gameID);
                return true;
            }
            else {
                return false;
            }
        }

    }

    @Override
    public HashMap<Integer, GameData> getGames() throws ResponseException {
        var games = new HashMap<Integer, GameData>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, json FROM games";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        GameData gameData = readGame(rs);
                        games.put(gameData.gameID(), gameData);
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(500, String.format("Unable to retrieve games: %s", e.getMessage()));
        }
        return games;
    }

    @Override
    public GameData getGame(Integer gameID) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, json FROM games WHERE gameID=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public boolean leaveGame(String playerColor, Integer gameID, String username) throws ResponseException {
        GameData game = getGame(gameID);
        if (Objects.equals(playerColor, "WHITE")) {
            if (username.equals(game.whiteUsername())) {
                var statement = "UPDATE games SET whiteUsername = NULL WHERE gameID = ? AND whiteUsername = ?";
                int rowsUpdated = executeUpdate(statement, gameID, username);
                return true;
            }
        } else if (Objects.equals(playerColor, "BLACK")) {
            if (username.equals(game.blackUsername())) {
                var statement = "UPDATE games SET blackUsername = NULL WHERE gameID = ? AND blackUsername = ?";
                int rowsUpdated = executeUpdate(statement, gameID, username);
                return true;
            }
        }
        return false;
    }

    @Override
    public void updateGame(Integer gameID, GameData gameData) throws ResponseException {
        ChessGame chessGame = gameData.game();
        var json = new Gson().toJson(chessGame);
        var statement = "UPDATE games SET json = ? WHERE gameID = ?";
        executeUpdate(statement, json, gameID);
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        var gameID = rs.getInt("gameID");
        var whiteUsername = rs.getString("whiteUsername");
        var blackUsername = rs.getString("blackUsername");
        var gameName = rs.getString("gameName");
        var json = rs.getString("json");
        var chessGame = new Gson().fromJson(json, ChessGame.class);
        return new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);
    }


    private int executeUpdate(String statement, Object... params) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) {
                        ps.setString(i + 1, p);
                    }
                    else if (param instanceof Integer p) {
                        ps.setInt(i + 1, p);
                    }
                    else if (param instanceof GameData p) {
                        ps.setString(i + 1, p.toString());
                    }
                    else if (param == null) {
                        ps.setNull(i + 1, NULL);
                    }
                }
                ps.executeUpdate();
                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (SQLException | DataAccessException e) {
            throw new ResponseException(500, String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS games (
            `gameID` int NOT NULL AUTO_INCREMENT,
            `whiteUsername` varchar(256) DEFAULT NULL,
            `blackUsername` varchar(256) DEFAULT NULL,
            `gameName` varchar(256) NOT NULL,
            `json` TEXT DEFAULT NULL,
            PRIMARY KEY(`gameID`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

}
