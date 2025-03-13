package dataaccess;

import chess.ChessGame;
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
    void setUp() throws SQLException, DataAccessException {
        users = new SQLUserDAO();
        authTokens = new SQLAuthDAO();
        games = new SQLGameDAO();

        users.clearAllUserData();
        authTokens.clearAllAuthData();
        games.clearAllGames();
    }

    @AfterEach
    void tearDown() throws DataAccessException {
        users.clearAllUserData();
        authTokens.clearAllAuthData();
        games.clearAllGames();
    }

    @Test
    void addAuthToken() throws DataAccessException {
        String username = "User1";
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, username);
        authTokens.addAuthToken(authData);
        AuthData actualResult = authTokens.getAuthToken(authToken);
        assertEquals(authData, actualResult);
    }

    @Test
    void invalidAddAuthToken() throws DataAccessException {
        String username = "User1";
        String authToken = null;
        AuthData authData = new AuthData(authToken, username);
        assertThrows(DataAccessException.class, () ->  authTokens.addAuthToken(authData));
    }

    @Test
    void removeAuthToken() throws DataAccessException {
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
        assertThrows(DataAccessException.class, () -> authTokens.removeAuthToken(authData));
    }

    @Test
    void getAuthToken() throws DataAccessException {
        String username = "User1";
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, username);
        authTokens.addAuthToken(authData);
        AuthData actualResult = authTokens.getAuthToken(authToken);
        assertEquals(authData, actualResult);
    }

    @Test
    void invalidGetAuthToken() throws DataAccessException {
        String username = "User1";
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, username);
        assertNull(authTokens.getAuthToken(authToken));
    }

    @Test
    void clearAllAuthData() throws DataAccessException {
        String username = "User1";
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, username);
        authTokens.addAuthToken(authData);
        authTokens.clearAllAuthData();
        assertTrue(authTokens.getAuthTokens().isEmpty());
    }

    @Test
    void getAuthTokens() throws DataAccessException {
        String username = "User1";
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, username);
        authTokens.addAuthToken(authData);
        String username2 = "User2";
        String authToken2 = UUID.randomUUID().toString();
        AuthData authData2 = new AuthData(authToken2, username2);
        authTokens.addAuthToken(authData2);
        HashMap<String, AuthData> actualResult = authTokens.getAuthTokens();
        HashMap<String, AuthData> expectedResult = new HashMap<>();
        expectedResult.put(authToken, authData);
        expectedResult.put(authToken2, authData2);
        assertEquals(expectedResult, actualResult);

    }

    @Test
    void invalidGetAuthTokens() throws DataAccessException {
        HashMap<String, AuthData> expectedResult = new HashMap<>();
        HashMap<String, AuthData> actualResult = authTokens.getAuthTokens();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void addUser() throws DataAccessException {
        UserData user = new UserData("User1", "1234", "User1@gmail.com");
        users.addUser(user);
        UserData actualResult = users.getUser("User1");
        assertEquals(user, actualResult);
    }

    @Test
    void invalidAddUser() throws DataAccessException {
        UserData user = new UserData("User1", null, "User1@gmail.com");
        assertThrows(DataAccessException.class, () -> users.addUser(user));
    }

    @Test
    void getUser() throws DataAccessException {
        UserData user = new UserData("User2", "5678", "User2@gmail.com");
        users.addUser(user);
        UserData actualResult = users.getUser("User2");
        assertEquals(user, actualResult);
    }

    @Test
    void invalidGetUser() throws DataAccessException {
        UserData user = new UserData("User2", "5678", "User2@gmail.com");
        UserData actualResult = users.getUser("User2");
        assertNull(actualResult);
    }

    @Test
    void getPassword() throws DataAccessException {
        UserData user = new UserData("User2", "5678", "User2@gmail.com");
        users.addUser(user);
        String actualResult = users.getPassword("User2");
        assertEquals("5678", actualResult);
    }

    @Test
    void invalidGetPassword() throws DataAccessException {
        UserData user = new UserData("User2", "5678", "User2@gmail.com");
        String actualResult = users.getPassword("User2");
        assertNull(actualResult);
    }

    @Test
    void clearAllUserData() throws DataAccessException {
        UserData user = new UserData("User2", "5678", "User2@gmail.com");
        users.addUser(user);
        users.clearAllUserData();
        assertTrue(users.getUsers().isEmpty());
    }

    @Test
    void getUsers() throws DataAccessException {
        UserData user2 = new UserData("User2", "5678", "User2@gmail.com");
        UserData user1 = new UserData("User1", "1234", "User1@gmail.com");
        users.addUser(user2);
        users.addUser(user1);
        HashMap<String, UserData> actualResult = users.getUsers();
        HashMap<String, UserData> expectedResult = new HashMap<>();
        expectedResult.put("User2", user2);
        expectedResult.put("User1", user1);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void invalidGetUsers() throws DataAccessException {
        HashMap<String, UserData> actualResult = users.getUsers();
        HashMap<String, UserData> expectedResult = new HashMap<>();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void createGame() throws DataAccessException {
        Integer gameID = games.createGame("mygame");
        assertEquals(1,gameID);
    }

    @Test
    void invalidCreateGame() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> games.createGame(null));
    }

    @Test
    void listGames() throws DataAccessException {
        Integer gameID = games.createGame("newGame");
        Integer gameID2 = games.createGame("secondGame");
        List<Map<String, Object>> actualResult = games.listGames();
        List<Map<String, Object>> gamesList = new ArrayList<Map<String, Object>>();

        HashMap<String, Object>  gameInfo1 = new HashMap<String, Object>();
        gameInfo1.put("gameID", games.getGame(gameID).gameID());
        gameInfo1.put("whiteUsername", games.getGame(gameID).whiteUsername());
        gameInfo1.put("blackUsername", games.getGame(gameID).blackUsername());
        gameInfo1.put("gameName", games.getGame(gameID).gameName());
        gamesList.add(gameInfo1);

        HashMap<String, Object> gameInfo2 = new HashMap<String, Object>();
        gameInfo2.put("gameID", games.getGame(gameID2).gameID());
        gameInfo2.put("whiteUsername", games.getGame(gameID2).whiteUsername());
        gameInfo2.put("blackUsername", games.getGame(gameID2).blackUsername());
        gameInfo2.put("gameName", games.getGame(gameID2).gameName());
        gamesList.add(gameInfo2);

        assertEquals(gamesList, actualResult);
    }

    @Test
    void invalidListGames() throws DataAccessException {
        assertTrue(games.listGames().isEmpty());
    }


    @Test
    void clearAllGames() throws DataAccessException {
        Integer gameID = games.createGame("newGame");
        Integer gameID2 = games.createGame("secondGame");
        games.clearAllGames();
        assertTrue(games.getGames().isEmpty());
    }

    @Test
    void joinGame() throws DataAccessException {
        Integer gameID = games.createGame("newGame");
        games.joinGame("WHITE", gameID, "User1");
        GameData actualResult = games.getGame(gameID);
        assertEquals(gameID, actualResult.gameID());
        assertEquals("User1", actualResult.whiteUsername());
        assertNull(actualResult.blackUsername());
        assertEquals("newGame", actualResult.gameName());
    }

    @Test
    void invalidJoinGame() throws DataAccessException {
        Integer gameID = games.createGame("newGame");
        assertThrows(DataAccessException.class, () -> games.joinGame("WHITE", null, "User1"));
    }

    @Test
    void getGames() throws DataAccessException {
        Integer gameID1 = games.createGame("game1");
        Integer gameID2 = games.createGame("game2");
        HashMap<Integer, GameData> actualResult = games.getGames();
        HashMap<Integer, GameData> expectedResult = new HashMap<>();
        expectedResult.put(games.getGame(gameID1).gameID(), games.getGame(gameID1));
        expectedResult.put(games.getGame(gameID2).gameID(), games.getGame(gameID2));
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void invalidGetGames() throws DataAccessException {
        Integer gameID1 = games.createGame("game1");
        Integer gameID2 = games.createGame("game2");
        games.clearAllGames();
        assertTrue(games.getGames().isEmpty());
    }

    @Test
    void getGame() throws DataAccessException {
        Integer gameID1 = games.createGame("game1");
        GameData actualResult = games.getGame(gameID1);
        GameData expectedResult = new GameData(1, null, null, "game1", new ChessGame());
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void invalidGetGame() throws DataAccessException {
        Integer gameID1 = games.createGame("game1");
        assertThrows(DataAccessException.class, () -> games.getGame(null));
    }
}