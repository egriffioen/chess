package service;

import dataaccess.*;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class UserServiceTestWithSQL {
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
    void validRegister() throws DataAccessException {
        UserService userService = new UserService(authTokens, users);
        RegisterRequest registerRequest = new RegisterRequest("user1", "1234", "user1@gmail.com");
        RegisterResult actualResult = userService.register(registerRequest);
        RegisterResult expectedResult = new RegisterResult("user1", actualResult.authToken());
        assertEquals(expectedResult, actualResult);
    }



    @Test
    void invalidRegister() throws DataAccessException {
        UserService userService = new UserService(authTokens, users);
        RegisterRequest registerRequest = new RegisterRequest("user1", "1234", "user1@gmail.com");
        RegisterResult firstResult = userService.register(registerRequest);
        RegisterResult actualResult = userService.register(registerRequest);
        RegisterResult expectedResult = new RegisterResult("Error: already taken");
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void validLogin() throws DataAccessException {
        UserService userService = new UserService(authTokens, users);
        RegisterRequest registerRequest = new RegisterRequest("user1", "1234", "user1@gmail.com");
        RegisterResult registerResult = userService.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("user1", "1234");
        LoginResult actualResult = userService.login(loginRequest);
        LoginResult expectedResult = new LoginResult("user1", actualResult.authToken());
        assertEquals(expectedResult, actualResult);

    }

    @Test
    void invalidLogin() throws DataAccessException {
        UserService userService = new UserService(authTokens, users);
        RegisterRequest registerRequest = new RegisterRequest("user1", "1234", "user1@gmail.com");
        RegisterResult registerResult = userService.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("user1", "abcd");
        LoginResult actualResult = userService.login(loginRequest);
        LoginResult expectedResult = new LoginResult("Error: unauthorized");
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void validLogout() throws DataAccessException {
        UserService userService = new UserService(authTokens, users);
        RegisterRequest registerRequest = new RegisterRequest("user1", "1234", "user1@gmail.com");
        RegisterResult registerResult = userService.register(registerRequest);
        LogoutRequest logoutRequest = new LogoutRequest(registerResult.authToken());
        LogoutResult actualResult = userService.logout(logoutRequest);
        LogoutResult expectedResult = new LogoutResult();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void invalidLogout() throws DataAccessException {
        UserService userService = new UserService(authTokens, users);
        RegisterRequest registerRequest = new RegisterRequest("user1", "1234", "user1@gmail.com");
        RegisterResult registerResult = userService.register(registerRequest);
        LogoutRequest logoutRequest = new LogoutRequest("");
        LogoutResult actualResult = userService.logout(logoutRequest);
        LogoutResult expectedResult = new LogoutResult("Error: unauthorized");
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void clearAllUsersAndTokens() throws DataAccessException {
        UserService userService = new UserService(authTokens, users);
        RegisterRequest registerRequest = new RegisterRequest("user1", "1234", "user1@gmail.com");
        RegisterResult registerResult = userService.register(registerRequest);
        userService.clearAllUsersAndTokens();
        assertNull(authTokens.getAuthToken(registerResult.authToken()));
        assertNull(users.getUser("user1"));
    }

    @Test
    void validCreateGame() throws DataAccessException {
        UserService userService = new UserService(authTokens, users);
        GameService gameService = new GameService(authTokens, games);
        RegisterRequest registerRequest = new RegisterRequest("user1", "1234", "user1@gmail.com");
        RegisterResult registerResult = userService.register(registerRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest(registerResult.authToken(), "chess");
        CreateGameResult createGameResult = gameService.createGame(createGameRequest);
        GameData gameData = games.getGame(createGameResult.gameID());
        CreateGameResult expectedResult = new CreateGameResult(gameData.gameID());
        assertEquals(expectedResult, createGameResult);
    }

    @Test
    void invalidCreateGame() throws DataAccessException {
        UserService userService = new UserService(authTokens, users);
        GameService gameService = new GameService(authTokens, games);
        RegisterRequest registerRequest = new RegisterRequest("user1", "1234", "user1@gmail.com");
        RegisterResult registerResult = userService.register(registerRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest("", "chess");
        CreateGameResult createGameResult = gameService.createGame(createGameRequest);
        CreateGameResult expectedResult = new CreateGameResult("Error: unauthorized");
        assertEquals(expectedResult, createGameResult);
    }

    @Test
    void validListGames() throws DataAccessException {
        UserService userService = new UserService(authTokens, users);
        GameService gameService = new GameService(authTokens, games);
        RegisterRequest registerRequest = new RegisterRequest("user1", "1234", "user1@gmail.com");
        RegisterResult registerResult = userService.register(registerRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest(registerResult.authToken(), "chess");
        CreateGameResult createGameResult = gameService.createGame(createGameRequest);
        ListGamesRequest listGamesRequest = new ListGamesRequest(registerResult.authToken());
        ListGamesResult listGamesResult = gameService.listGames(listGamesRequest);

        List<Map<String, Object>> expectedGames = new ArrayList<>();
        Map<String, Object> gameInfo = new HashMap<>();
        gameInfo.put("gameID", createGameResult.gameID());
        gameInfo.put("whiteUsername", null);
        gameInfo.put("blackUsername", null);
        gameInfo.put("gameName", "chess");

        expectedGames.add(gameInfo);

        ListGamesResult expectedResult = new ListGamesResult(expectedGames);
        assertEquals(expectedResult, listGamesResult);
    }

    @Test
    void invalidListGames() throws DataAccessException {
        UserService userService = new UserService(authTokens, users);
        GameService gameService = new GameService(authTokens, games);
        RegisterRequest registerRequest = new RegisterRequest("user1", "1234", "user1@gmail.com");
        RegisterResult registerResult = userService.register(registerRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest(registerResult.authToken(), "chess");
        CreateGameResult createGameResult = gameService.createGame(createGameRequest);
        ListGamesRequest listGamesRequest = new ListGamesRequest("");
        ListGamesResult listGamesResult = gameService.listGames(listGamesRequest);
        ListGamesResult expectedResult = new ListGamesResult("Error: unauthorized");
        assertEquals(expectedResult, listGamesResult);
    }

    @Test
    void clearAllGames() throws DataAccessException {
        UserService userService = new UserService(authTokens, users);
        GameService gameService = new GameService(authTokens, games);
        RegisterRequest registerRequest = new RegisterRequest("user1", "1234", "user1@gmail.com");
        RegisterResult registerResult = userService.register(registerRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest(registerResult.authToken(), "chess");
        CreateGameResult createGameResult = gameService.createGame(createGameRequest);
        gameService.clearAllGames();
        HashMap<Integer, GameData> expectedGames = new HashMap<>();
        assertEquals(expectedGames, games.getGames());
    }

    @Test
    void validJoinGame() throws DataAccessException {
        UserService userService = new UserService(authTokens, users);
        GameService gameService = new GameService(authTokens, games);
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
    void invalidJoinGame() throws DataAccessException {
        UserService userService = new UserService(authTokens, users);
        GameService gameService = new GameService(authTokens, games);
        RegisterRequest registerRequest = new RegisterRequest("user1", "1234", "user1@gmail.com");
        RegisterResult registerResult = userService.register(registerRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest(registerResult.authToken(), "chess");
        CreateGameResult createGameResult = gameService.createGame(createGameRequest);

        JoinGameRequest joinGameRequest = new JoinGameRequest(registerResult.authToken(),"WHITE",createGameResult.gameID());
        JoinGameResult joinGameResult = gameService.joinGame(joinGameRequest);
        JoinGameResult actualResult = gameService.joinGame(joinGameRequest);

        JoinGameResult expectedResult = new JoinGameResult("Error: already taken");
        assertEquals(expectedResult, actualResult);
    }

}
