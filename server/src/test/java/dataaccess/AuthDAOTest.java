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
    void addAuthToken() throws DataAccessException {
        String username = "User1";
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, username);
        try {
            authTokens.addAuthToken(authData);
        } catch (exception.ResponseException e) {
            throw new RuntimeException(e);
        }
        AuthData actualResult = null;
        try {
            actualResult = authTokens.getAuthToken(authToken);
        } catch (exception.ResponseException e) {
            throw new RuntimeException(e);
        }
        assertEquals(authData, actualResult);
    }

    @Test
    void invalidAddAuthToken() throws DataAccessException {
        String username = "User1";
        String authToken = null;
        AuthData authData = new AuthData(authToken, username);
        assertThrows(ResponseException.class, () ->  authTokens.addAuthToken(authData));
    }

    @Test
    void removeAuthToken() throws DataAccessException {
        String username = "User1";
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, username);
        try {
            authTokens.addAuthToken(authData);
        } catch (exception.ResponseException e) {
            throw new RuntimeException(e);
        }
        try {
            authTokens.removeAuthToken(authData);
        } catch (exception.ResponseException e) {
            throw new RuntimeException(e);
        }
        try {
            assertNull(authTokens.getAuthToken(authToken));
        } catch (exception.ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void invalidRemoveAuthToken() throws DataAccessException {
        String username = "User1";
        String authToken = null;
        AuthData authData = new AuthData(authToken, username);
        assertThrows(ResponseException.class, () -> authTokens.removeAuthToken(authData));
    }

    @Test
    void getAuthToken() throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, "User2");
        try {
            authTokens.addAuthToken(authData);
        } catch (exception.ResponseException e) {
            throw new RuntimeException(e);
        }
        AuthData actualResult = null;
        try {
            actualResult = authTokens.getAuthToken(authToken);
        } catch (exception.ResponseException e) {
            throw new RuntimeException(e);
        }
        assertEquals(authData, actualResult);
    }

    @Test
    void invalidGetAuthToken() throws DataAccessException {
        String username = "User1";
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, username);
        try {
            assertNull(authTokens.getAuthToken(authToken));
        } catch (exception.ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void clearAllAuthData() throws DataAccessException {
        String username = "User1";
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, username);
        try {
            authTokens.addAuthToken(authData);
        } catch (exception.ResponseException e) {
            throw new RuntimeException(e);
        }
        try {
            authTokens.clearAllAuthData();
        } catch (exception.ResponseException e) {
            throw new RuntimeException(e);
        }
        try {
            assertTrue(authTokens.getAuthTokens().isEmpty());
        } catch (exception.ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getAuthTokens() throws DataAccessException {
        String username = "User1";
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, username);
        try {
            authTokens.addAuthToken(authData);
        } catch (exception.ResponseException e) {
            throw new RuntimeException(e);
        }
        String username2 = "User2";
        String authToken2 = UUID.randomUUID().toString();
        AuthData authData2 = new AuthData(authToken2, username2);
        try {
            authTokens.addAuthToken(authData2);
        } catch (exception.ResponseException e) {
            throw new RuntimeException(e);
        }
        HashMap<String, AuthData> actualResult = null;
        try {
            actualResult = authTokens.getAuthTokens();
        } catch (exception.ResponseException e) {
            throw new RuntimeException(e);
        }
        HashMap<String, AuthData> expectedResult = new HashMap<>();
        expectedResult.put(authToken, authData);
        expectedResult.put(authToken2, authData2);
        assertEquals(expectedResult, actualResult);

    }

    @Test
    void invalidGetAuthTokens() throws DataAccessException {
        HashMap<String, AuthData> expectedResult = new HashMap<>();
        HashMap<String, AuthData> actualResult = null;
        try {
            actualResult = authTokens.getAuthTokens();
        } catch (exception.ResponseException e) {
            throw new RuntimeException(e);
        }
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void addUser() throws DataAccessException {
        UserData user = new UserData("User1", "1234", "User1@gmail.com");
        try {
            users.addUser(user);
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        UserData actualResult = null;
        try {
            actualResult = users.getUser("User1");
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        assertEquals(user, actualResult);
    }

    @Test
    void invalidAddUser() throws DataAccessException {
        UserData user = new UserData("User1", null, "User1@gmail.com");
        assertThrows(ResponseException.class, () -> users.addUser(user));
    }

    @Test
    void getUser() throws DataAccessException {
        UserData user = new UserData("User2", "5678", "User2@gmail.com");
        try {
            users.addUser(user);
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        UserData actualResult = null;
        try {
            actualResult = users.getUser("User2");
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        assertEquals(user, actualResult);
    }

    @Test
    void invalidGetUser() throws DataAccessException {
        UserData user = new UserData("User2", "5678", "User2@gmail.com");
        UserData actualResult = null;
        try {
            actualResult = users.getUser("User2");
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        assertNull(actualResult);
    }

    @Test
    void getPassword() throws DataAccessException {
        UserData user = new UserData("User2", "5678", "User2@gmail.com");
        try {
            users.addUser(user);
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        String actualResult = null;
        try {
            actualResult = users.getPassword("User2");
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        assertEquals("5678", actualResult);
    }

    @Test
    void invalidGetPassword() throws DataAccessException {
        UserData user = new UserData("User2", "5678", "User2@gmail.com");
        String actualResult = null;
        try {
            actualResult = users.getPassword("User2");
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        assertNull(actualResult);
    }

    @Test
    void clearAllUserData() throws DataAccessException {
        UserData user = new UserData("User2", "5678", "User2@gmail.com");
        try {
            users.addUser(user);
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        try {
            users.clearAllUserData();
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        try {
            assertTrue(users.getUsers().isEmpty());
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getUsers() throws DataAccessException {
        UserData user2 = new UserData("User2", "5678", "User2@gmail.com");
        UserData user1 = new UserData("User1", "1234", "User1@gmail.com");
        try {
            users.addUser(user2);
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        try {
            users.addUser(user1);
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        HashMap<String, UserData> actualResult = null;
        try {
            actualResult = users.getUsers();
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        HashMap<String, UserData> expectedResult = new HashMap<>();
        expectedResult.put("User2", user2);
        expectedResult.put("User1", user1);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void invalidGetUsers() throws DataAccessException {
        HashMap<String, UserData> actualResult = null;
        try {
            actualResult = users.getUsers();
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        HashMap<String, UserData> expectedResult = new HashMap<>();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void createGame() throws DataAccessException {
        Integer gameID = null;
        try {
            gameID = games.createGame("mygame");
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        assertEquals(1,gameID);
    }

    @Test
    void invalidCreateGame() throws DataAccessException {
        assertThrows(ResponseException.class, () -> games.createGame(null));
    }

    @Test
    void listGames() throws DataAccessException {
        Integer gameID = null;
        try {
            gameID = games.createGame("newGame");
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        Integer gameID2 = null;
        try {
            gameID2 = games.createGame("secondGame");
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        List<Map<String, Object>> actualResult = null;
        try {
            actualResult = games.listGames();
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        List<Map<String, Object>> gamesList = new ArrayList<Map<String, Object>>();

        HashMap<String, Object>  gameInfo1 = new HashMap<String, Object>();
        try {
            gameInfo1.put("gameID", games.getGame(gameID).gameID());
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        try {
            gameInfo1.put("whiteUsername", games.getGame(gameID).whiteUsername());
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        try {
            gameInfo1.put("blackUsername", games.getGame(gameID).blackUsername());
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        try {
            gameInfo1.put("gameName", games.getGame(gameID).gameName());
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        gamesList.add(gameInfo1);

        HashMap<String, Object> gameInfo2 = new HashMap<String, Object>();
        try {
            gameInfo2.put("gameID", games.getGame(gameID2).gameID());
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        try {
            gameInfo2.put("whiteUsername", games.getGame(gameID2).whiteUsername());
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        try {
            gameInfo2.put("blackUsername", games.getGame(gameID2).blackUsername());
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        try {
            gameInfo2.put("gameName", games.getGame(gameID2).gameName());
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        gamesList.add(gameInfo2);

        assertEquals(gamesList, actualResult);
    }

    @Test
    void invalidListGames() throws DataAccessException {
        try {
            assertTrue(games.listGames().isEmpty());
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    void clearAllGames() throws DataAccessException {
        try {
            Integer gameID = games.createGame("newGame");
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        try {
            Integer gameID2 = games.createGame("secondGame");
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        try {
            games.clearAllGames();
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        try {
            assertTrue(games.getGames().isEmpty());
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void joinGame() throws DataAccessException {
        Integer gameID = null;
        try {
            gameID = games.createGame("newGame");
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        try {
            games.joinGame("WHITE", gameID, "User1");
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        GameData actualResult = null;
        try {
            actualResult = games.getGame(gameID);
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        assertEquals(gameID, actualResult.gameID());
        assertEquals("User1", actualResult.whiteUsername());
        assertNull(actualResult.blackUsername());
        assertEquals("newGame", actualResult.gameName());
    }

    @Test
    void invalidJoinGame() throws DataAccessException {
        try {
            Integer gameID = games.createGame("newGame");
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        assertThrows(ResponseException.class, () -> games.joinGame("WHITE", null, "User1"));
    }

    @Test
    void getGames() throws DataAccessException {
        Integer gameID1 = null;
        try {
            gameID1 = games.createGame("game1");
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        Integer gameID2 = null;
        try {
            gameID2 = games.createGame("game2");
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        HashMap<Integer, GameData> actualResult = null;
        try {
            actualResult = games.getGames();
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        HashMap<Integer, GameData> expectedResult = new HashMap<>();
        try {
            expectedResult.put(games.getGame(gameID1).gameID(), games.getGame(gameID1));
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        try {
            expectedResult.put(games.getGame(gameID2).gameID(), games.getGame(gameID2));
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void invalidGetGames() throws DataAccessException {
        try {
            Integer gameID1 = games.createGame("game1");
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        try {
            Integer gameID2 = games.createGame("game2");
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        try {
            games.clearAllGames();
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        try {
            assertTrue(games.getGames().isEmpty());
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getGame() throws DataAccessException {
        Integer gameID1 = null;
        try {
            gameID1 = games.createGame("game1");
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        GameData actualResult = null;
        try {
            actualResult = games.getGame(gameID1);
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        GameData expectedResult = new GameData(1, null, null, "game1", new ChessGame());
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void invalidGetGame() throws ResponseException {
        try {
            Integer gameID1 = games.createGame("game1");
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        assertThrows(ResponseException.class, () -> games.getGame(null));
    }
}