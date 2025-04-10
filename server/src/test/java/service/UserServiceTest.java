package service;

import chess.ChessGame;
import dataaccess.*;
import exception.ResponseException;
import model.GameData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.*;
import result.*;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    private UserDAO dbusers;
    private AuthDAO dbauthTokens;
    private GameDAO dbgames;

    @BeforeEach
    void setUp() throws SQLException, DataAccessException, ResponseException {
        dbusers = new SQLUserDAO();
        dbauthTokens = new SQLAuthDAO();
        dbgames = new SQLGameDAO();

        dbusers.clearAllUserData();
        try {
            dbauthTokens.clearAllAuthData();
        } catch (exception.ResponseException e) {
            throw new RuntimeException(e);
        }
        try {
            dbgames.clearAllGames();
        } catch (exception.ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    void tearDown() throws DataAccessException {
        try {
            dbusers.clearAllUserData();
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        try {
            dbauthTokens.clearAllAuthData();
        } catch (exception.ResponseException e) {
            throw new RuntimeException(e);
        }
        try {
            dbgames.clearAllGames();
        } catch (exception.ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void validRegister() throws ResponseException, DataAccessException {
        UserService userService = new UserService(dbauthTokens, dbusers);
        RegisterRequest registerRequest = new RegisterRequest("user1", "1234", "user1@gmail.com");
        RegisterResult actualResult = userService.register(registerRequest);
        RegisterResult expectedResult = new RegisterResult("user1", actualResult.authToken());
        assertEquals(expectedResult, actualResult);
    }



    @Test
    void invalidRegister() throws ResponseException, DataAccessException {
        UserService userService = new UserService(dbauthTokens, dbusers);
        RegisterRequest registerRequest = new RegisterRequest("user1", "1234", "user1@gmail.com");
        RegisterResult firstResult = userService.register(registerRequest);
        assertThrows(ResponseException.class, () -> userService.register(registerRequest));
    }

    @Test
    void validLogin() throws ResponseException, DataAccessException {
        UserService userService = new UserService(dbauthTokens, dbusers);
        RegisterRequest registerRequest = new RegisterRequest("user1", "1234", "user1@gmail.com");
        RegisterResult registerResult = userService.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("user1", "1234");
        LoginResult actualResult = userService.login(loginRequest);
        LoginResult expectedResult = new LoginResult("user1", actualResult.authToken());
        assertEquals(expectedResult, actualResult);

    }

    @Test
    void invalidLogin() throws ResponseException, DataAccessException {
        UserService userService = new UserService(dbauthTokens, dbusers);
        RegisterRequest registerRequest = new RegisterRequest("user1", "1234", "user1@gmail.com");
        RegisterResult registerResult = userService.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("user1", "abcd");
        assertThrows(ResponseException.class, () -> userService.login(loginRequest));
    }

    @Test
    void validLogout() throws ResponseException, DataAccessException {
        UserService userService = new UserService(dbauthTokens, dbusers);
        RegisterRequest registerRequest = new RegisterRequest("user1", "1234", "user1@gmail.com");
        RegisterResult registerResult = userService.register(registerRequest);
        LogoutRequest logoutRequest = new LogoutRequest(registerResult.authToken());
        LogoutResult actualResult = userService.logout(logoutRequest);
        LogoutResult expectedResult = new LogoutResult();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void invalidLogout() throws ResponseException, DataAccessException {
        UserService userService = new UserService(dbauthTokens, dbusers);
        RegisterRequest registerRequest = new RegisterRequest("user1", "1234", "user1@gmail.com");
        RegisterResult registerResult = userService.register(registerRequest);
        LogoutRequest logoutRequest = new LogoutRequest("");
        assertThrows(ResponseException.class, () -> userService.logout(logoutRequest));
    }

    @Test
    void clearAllUsersAndTokens() throws ResponseException, DataAccessException {
        UserService userService = new UserService(dbauthTokens, dbusers);
        RegisterRequest registerRequest = new RegisterRequest("user1", "1234", "user1@gmail.com");
        RegisterResult registerResult = userService.register(registerRequest);
        userService.clearAllUsersAndTokens();
        try {
            assertNull(dbauthTokens.getAuthToken(registerResult.authToken()));
        } catch (exception.ResponseException e) {
            throw new RuntimeException(e);
        }
        try {
            assertNull(dbusers.getUser("user1"));
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void validCreateGame() throws ResponseException, DataAccessException {
        UserService userService = new UserService(dbauthTokens, dbusers);
        GameService gameService = new GameService(dbauthTokens, dbgames);
        RegisterRequest registerRequest = new RegisterRequest("user1", "1234", "user1@gmail.com");
        RegisterResult registerResult = userService.register(registerRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest(registerResult.authToken(), "chess");
        CreateGameResult createGameResult = gameService.createGame(createGameRequest);
        GameData gameData = null;
        try {
            gameData = dbgames.getGame(createGameResult.gameID());
        } catch (exception.ResponseException e) {
            throw new RuntimeException(e);
        }
        CreateGameResult expectedResult = new CreateGameResult(gameData.gameID());
        assertEquals(expectedResult, createGameResult);
    }

    @Test
    void invalidCreateGame() throws ResponseException, DataAccessException {
        UserService userService = new UserService(dbauthTokens, dbusers);
        GameService gameService = new GameService(dbauthTokens, dbgames);
        RegisterRequest registerRequest = new RegisterRequest("user1", "1234", "user1@gmail.com");
        RegisterResult registerResult = userService.register(registerRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest("", "chess");
        assertThrows(ResponseException.class, () -> gameService.createGame(createGameRequest));
    }

    @Test
    void validListGames() throws ResponseException, DataAccessException {
        UserService userService = new UserService(dbauthTokens, dbusers);
        GameService gameService = new GameService(dbauthTokens, dbgames);
        RegisterRequest registerRequest = new RegisterRequest("user2", "4567", "user2@gmail.com");
        RegisterResult registerResult = userService.register(registerRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest(registerResult.authToken(), "chess");
        CreateGameResult createGameResult = gameService.createGame(createGameRequest);
        ListGamesRequest listGamesRequest = new ListGamesRequest(registerResult.authToken());
        ListGamesResult listGamesResult = gameService.listGames(listGamesRequest);
        GameData gameData = new GameData(createGameResult.gameID(), null, null, "chess", new ChessGame());
        ArrayList<GameData> expectedGames = null;
        expectedGames.add(gameData);
        //expectedGames.add(gameInfo);

        ListGamesResult expectedResult = new ListGamesResult(expectedGames);
        assertEquals(expectedResult, listGamesResult);
    }

    @Test
    void invalidListGames() throws ResponseException, DataAccessException {
        UserService userService = new UserService(dbauthTokens, dbusers);
        GameService gameService = new GameService(dbauthTokens, dbgames);
        RegisterRequest registerRequest = new RegisterRequest("user1", "1234", "user1@gmail.com");
        RegisterResult registerResult = userService.register(registerRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest(registerResult.authToken(), "chess");
        CreateGameResult createGameResult = gameService.createGame(createGameRequest);
        ListGamesRequest listGamesRequest = new ListGamesRequest("");
        assertThrows(ResponseException.class, () -> gameService.listGames(listGamesRequest));
    }

    @Test
    void clearAllGames() throws ResponseException, DataAccessException {
        UserService userService = new UserService(dbauthTokens, dbusers);
        GameService gameService = new GameService(dbauthTokens, dbgames);
        RegisterRequest registerRequest = new RegisterRequest("user1", "1234", "user1@gmail.com");
        RegisterResult registerResult = userService.register(registerRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest(registerResult.authToken(), "chess");
        CreateGameResult createGameResult = gameService.createGame(createGameRequest);
        gameService.clearAllGames();
        HashMap<Integer, GameData> expectedGames = new HashMap<>();
        try {
            assertEquals(expectedGames, dbgames.getGames());
        } catch (exception.ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void validJoinGame() throws ResponseException, DataAccessException {
        UserService userService = new UserService(dbauthTokens, dbusers);
        GameService gameService = new GameService(dbauthTokens, dbgames);
        RegisterRequest registerRequest = new RegisterRequest("user1", "1234", "user1@gmail.com");
        RegisterResult registerResult = userService.register(registerRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest(registerResult.authToken(), "chess");
        CreateGameResult createGameResult = gameService.createGame(createGameRequest);

        JoinGameRequest joinGameRequest = new JoinGameRequest(registerResult.authToken(),"WHITE",createGameResult.gameID());
        JoinGameResult joinGameResult = gameService.joinGame(joinGameRequest);

        JoinGameResult expectedResult = new JoinGameResult();
        assertEquals(expectedResult, joinGameResult);
    }

    @Test
    void invalidJoinGame() throws ResponseException, DataAccessException {
        UserService userService = new UserService(dbauthTokens, dbusers);
        GameService gameService = new GameService(dbauthTokens, dbgames);
        RegisterRequest registerRequest = new RegisterRequest("user1", "1234", "user1@gmail.com");
        RegisterResult registerResult = userService.register(registerRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest(registerResult.authToken(), "chess");
        CreateGameResult createGameResult = gameService.createGame(createGameRequest);

        JoinGameRequest joinGameRequest = new JoinGameRequest(registerResult.authToken(),"WHITE",createGameResult.gameID());
        JoinGameResult joinGameResult = gameService.joinGame(joinGameRequest);
        assertThrows(ResponseException.class, () -> gameService.joinGame(joinGameRequest));
    }
}