package dataaccess;

import chess.ChessGame;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class AuthDAOTest {

    private UserDAO users;
    private AuthDAO authTokens;
    private GameDAO games;

    @BeforeEach
    void setUp() throws SQLException, DataAccessException, ResponseException {
        users = new SQLUserDAO();
        authTokens = new SQLAuthDAO();
        games = new SQLGameDAO();

        users.clearAllUserData();
        try {
            authTokens.clearAllAuthData();
        } catch (exception.ResponseException e) {
            throw new RuntimeException(e);
        }
        games.clearAllGames();
    }

    @AfterEach
    void tearDown() throws DataAccessException {
        try {
            users.clearAllUserData();
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        try {
            authTokens.clearAllAuthData();
        } catch (exception.ResponseException e) {
            throw new RuntimeException(e);
        }
        try {
            games.clearAllGames();
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void addAuthToken() throws DataAccessException, ResponseException {
        String username = "User1";
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, username);
        authTokens.addAuthToken(authData);
        AuthData actualResult = null;
        actualResult = authTokens.getAuthToken(authToken);
        assertEquals(authData, actualResult);
    }

    @Test
    void invalidAddAuthToken() throws DataAccessException {
        String username = "User1";
        String authToken = null;
        AuthData authData = new AuthData(authToken, username);
        assertThrows(ResponseException.class, () -> authTokens.addAuthToken(authData));
    }

    @Test
    void removeAuthToken() throws DataAccessException, ResponseException {
        String username = "User1";
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, username);
        authTokens.addAuthToken(authData);
        authTokens.removeAuthToken(authData);
        assertNull(authTokens.getAuthToken(authToken));

    }

    @Test
    void invalidRemoveAuthToken() throws DataAccessException {
        String username = "User1";
        String authToken = null;
        AuthData authData = new AuthData(authToken, username);
        assertThrows(ResponseException.class, () -> authTokens.removeAuthToken(authData));
    }

    @Test
    void getAuthToken() throws DataAccessException, ResponseException {
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, "User2");
        authTokens.addAuthToken(authData);
        AuthData actualResult = null;
        actualResult = authTokens.getAuthToken(authToken);
        assertEquals(authData, actualResult);
    }

    @Test
    void invalidGetAuthToken() throws DataAccessException, ResponseException {
        String username = "User1";
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, username);
        assertNull(authTokens.getAuthToken(authToken));
    }

    @Test
    void clearAllAuthData() throws DataAccessException, ResponseException {
        String username = "User1";
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, username);
        authTokens.addAuthToken(authData);
        authTokens.clearAllAuthData();
        assertTrue(authTokens.getAuthTokens().isEmpty());

    }

    @Test
    void getAuthTokens() throws DataAccessException, ResponseException {
        String username = "User1";
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, username);
        authTokens.addAuthToken(authData);
        String username2 = "User2";
        String authToken2 = UUID.randomUUID().toString();
        AuthData authData2 = new AuthData(authToken2, username2);
        authTokens.addAuthToken(authData2);
        HashMap<String, AuthData> actualResult = null;
        actualResult = authTokens.getAuthTokens();

        HashMap<String, AuthData> expectedResult = new HashMap<>();
        expectedResult.put(authToken, authData);
        expectedResult.put(authToken2, authData2);
        assertEquals(expectedResult, actualResult);

    }

    @Test
    void invalidGetAuthTokens() throws DataAccessException, ResponseException {
        HashMap<String, AuthData> expectedResult = new HashMap<>();
        HashMap<String, AuthData> actualResult = null;
        actualResult = authTokens.getAuthTokens();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void addUser() throws DataAccessException, ResponseException {
        UserData user = new UserData("User1", "1234", "User1@gmail.com");
        users.addUser(user);
        UserData actualResult = null;
        actualResult = users.getUser("User1");
        assertEquals(user, actualResult);
    }

    @Test
    void invalidAddUser() throws DataAccessException {
        UserData user = new UserData("User1", null, "User1@gmail.com");
        assertThrows(ResponseException.class, () -> users.addUser(user));
    }

    @Test
    void getUser() throws DataAccessException, ResponseException {
        UserData user = new UserData("User2", "5678", "User2@gmail.com");
        users.addUser(user);
        UserData actualResult = null;
        actualResult = users.getUser("User2");
        assertEquals(user, actualResult);
    }

    @Test
    void invalidGetUser() throws DataAccessException, ResponseException {
        UserData user = new UserData("User2", "5678", "User2@gmail.com");
        UserData actualResult = null;
        actualResult = users.getUser("User2");
        assertNull(actualResult);
    }

    @Test
    void getPassword() throws DataAccessException, ResponseException {
        UserData user = new UserData("User2", "5678", "User2@gmail.com");
        users.addUser(user);
        String actualResult = null;
        actualResult = users.getPassword("User2");
        assertEquals("5678", actualResult);
    }

    @Test
    void invalidGetPassword() throws DataAccessException, ResponseException {
        UserData user = new UserData("User2", "5678", "User2@gmail.com");
        String actualResult = null;
        actualResult = users.getPassword("User2");
        assertNull(actualResult);
    }

    @Test
    void clearAllUserData() throws DataAccessException, ResponseException {
        UserData user = new UserData("User2", "5678", "User2@gmail.com");
        users.addUser(user);
        users.clearAllUserData();
        assertTrue(users.getUsers().isEmpty());
    }

    @Test
    void getUsers() throws DataAccessException, ResponseException {
        UserData user2 = new UserData("User2", "5678", "User2@gmail.com");
        UserData user1 = new UserData("User1", "1234", "User1@gmail.com");
        users.addUser(user2);
        users.addUser(user1);
        HashMap<String, UserData> actualResult = null;
        actualResult = users.getUsers();
        HashMap<String, UserData> expectedResult = new HashMap<>();
        expectedResult.put("User2", user2);
        expectedResult.put("User1", user1);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void invalidGetUsers() throws DataAccessException, ResponseException {
        HashMap<String, UserData> actualResult = null;
        actualResult = users.getUsers();
        HashMap<String, UserData> expectedResult = new HashMap<>();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void createGame() throws DataAccessException, ResponseException {
        Integer gameID = null;
        gameID = games.createGame("mygame");
        assertEquals(1, gameID);
    }

    @Test
    void invalidCreateGame() throws DataAccessException {
        assertThrows(ResponseException.class, () -> games.createGame(null));
    }

    @Test
    void listGames() throws DataAccessException, ResponseException {
        Integer gameID = null;
        gameID = games.createGame("newGame");
        Integer gameID2 = null;
        gameID2 = games.createGame("secondGame");
        ArrayList<GameData> actualResult = new ArrayList<>();
        actualResult = games.listGames();

        ArrayList<GameData> gamesList = new ArrayList<>();
        GameData game1 = new GameData(games.getGame(gameID).gameID(), null, null, "newGame", new ChessGame());
        GameData game2 = new GameData(games.getGame(gameID2).gameID(), null, null, "secondGame", new ChessGame());
        gamesList.add(game1);
        gamesList.add(game2);
//        List<Map<String, Object>> actualResult = null;
//        actualResult = games.listGames();
//        List<Map<String, Object>> gamesList = new ArrayList<Map<String, Object>>();
//        HashMap<String, Object> gameInfo1 = new HashMap<String, Object>();
//        gameInfo1.put("gameID", games.getGame(gameID).gameID());
//        gameInfo1.put("whiteUsername", games.getGame(gameID).whiteUsername());
//        gameInfo1.put("blackUsername", games.getGame(gameID).blackUsername());
//        gameInfo1.put("gameName", games.getGame(gameID).gameName());
//        gamesList.add(gameInfo1);
//
//        HashMap<String, Object> gameInfo2 = new HashMap<String, Object>();
//        gameInfo2.put("gameID", games.getGame(gameID2).gameID());
//        gameInfo2.put("whiteUsername", games.getGame(gameID2).whiteUsername());
//        gameInfo2.put("blackUsername", games.getGame(gameID2).blackUsername());
//        gameInfo2.put("gameName", games.getGame(gameID2).gameName());
//        gamesList.add(gameInfo2);

        assertEquals(gamesList, actualResult);
    }

    @Test
    void invalidListGames() throws DataAccessException, ResponseException {
        assertTrue(games.listGames().isEmpty());
    }


    @Test
    void clearAllGames() throws DataAccessException, ResponseException {
        Integer gameID = games.createGame("newGame");
        Integer gameID2 = games.createGame("secondGame");
        games.clearAllGames();
        assertTrue(games.getGames().isEmpty());
    }

    @Test
    void joinGame() throws DataAccessException, ResponseException {
        Integer gameID = null;
        gameID = games.createGame("newGame");
        games.joinGame("WHITE", gameID, "User1");
        GameData actualResult = null;
        actualResult = games.getGame(gameID);
        assertEquals(gameID, actualResult.gameID());
        assertEquals("User1", actualResult.whiteUsername());
        assertNull(actualResult.blackUsername());
        assertEquals("newGame", actualResult.gameName());
    }

    @Test
    void invalidJoinGame() throws DataAccessException, ResponseException {
        Integer gameID = games.createGame("newGame");
        assertThrows(ResponseException.class, () -> games.joinGame("WHITE", null, "User1"));
    }

    @Test
    void getGames() throws DataAccessException, ResponseException {
        Integer gameID1 = null;
        gameID1 = games.createGame("game1");
        Integer gameID2 = null;
        gameID2 = games.createGame("game2");
        HashMap<Integer, GameData> actualResult = null;
        actualResult = games.getGames();
        HashMap<Integer, GameData> expectedResult = new HashMap<>();
        expectedResult.put(games.getGame(gameID1).gameID(), games.getGame(gameID1));
        expectedResult.put(games.getGame(gameID2).gameID(), games.getGame(gameID2));
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void invalidGetGames() throws DataAccessException, ResponseException {
        Integer gameID1 = games.createGame("game1");
        Integer gameID2 = games.createGame("game2");
        games.clearAllGames();
        assertTrue(games.getGames().isEmpty());
    }

    @Test
    void getGame() throws DataAccessException, ResponseException {
        Integer gameID1 = null;
        gameID1 = games.createGame("game1");
        GameData actualResult = null;
        actualResult = games.getGame(gameID1);
        GameData expectedResult = new GameData(1, null, null, "game1", new ChessGame());
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void invalidGetGame() throws ResponseException {
        Integer gameID1 = games.createGame("game1");
        assertThrows(ResponseException.class, () -> games.getGame(null));
    }
}