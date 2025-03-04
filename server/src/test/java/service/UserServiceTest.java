package service;

import dataaccess.*;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import request.*;
import result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    @AfterEach
    void tearDown() {
        clearAllGames();
        clearAllUsersAndTokens();
    }

    @Test
    void validRegister() {
        AuthDAO authDAO = new MemoryAuthDAO();
        UserDAO users = new MemoryUserDAO();
        UserService userService = new UserService(authDAO, users);
        RegisterRequest registerRequest = new RegisterRequest("user1", "1234", "user1@gmail.com");
        RegisterResult actualResult = userService.register(registerRequest);
        RegisterResult expectedResult = new RegisterResult("user1", actualResult.authToken());
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void invalidRegister() {
        AuthDAO authDAO = new MemoryAuthDAO();
        UserDAO users = new MemoryUserDAO();
        UserService userService = new UserService(authDAO, users);
        RegisterRequest registerRequest = new RegisterRequest("user1", null, "user1@gmail.com");
        RegisterResult firstResult = userService.register(registerRequest);
        RegisterResult actualResult = userService.register(registerRequest);
        RegisterResult expectedResult = new RegisterResult("Error: already taken");
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void validLogin() {
        AuthDAO authDAO = new MemoryAuthDAO();
        UserDAO users = new MemoryUserDAO();
        UserService userService = new UserService(authDAO, users);
        RegisterRequest registerRequest = new RegisterRequest("user1", "1234", "user1@gmail.com");
        RegisterResult registerResult = userService.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("user1", "1234");
        LoginResult actualResult = userService.login(loginRequest);
        LoginResult expectedResult = new LoginResult("user1", actualResult.authToken());
        assertEquals(expectedResult, actualResult);

    }

    @Test
    void invalidLogin() {
        AuthDAO authDAO = new MemoryAuthDAO();
        UserDAO users = new MemoryUserDAO();
        UserService userService = new UserService(authDAO, users);
        RegisterRequest registerRequest = new RegisterRequest("user1", "1234", "user1@gmail.com");
        RegisterResult registerResult = userService.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("user1", "abcd");
        LoginResult actualResult = userService.login(loginRequest);
        LoginResult expectedResult = new LoginResult("Error: unauthorized");
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void validLogout() {
        AuthDAO authDAO = new MemoryAuthDAO();
        UserDAO users = new MemoryUserDAO();
        UserService userService = new UserService(authDAO, users);
        RegisterRequest registerRequest = new RegisterRequest("user1", "1234", "user1@gmail.com");
        RegisterResult registerResult = userService.register(registerRequest);
        LogoutRequest logoutRequest = new LogoutRequest(registerResult.authToken());
        LogoutResult actualResult = userService.logout(logoutRequest);
        LogoutResult expectedResult = new LogoutResult();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void invalidLogout() {
        AuthDAO authDAO = new MemoryAuthDAO();
        UserDAO users = new MemoryUserDAO();
        UserService userService = new UserService(authDAO, users);
        RegisterRequest registerRequest = new RegisterRequest("user1", "1234", "user1@gmail.com");
        RegisterResult registerResult = userService.register(registerRequest);
        LogoutRequest logoutRequest = new LogoutRequest("");
        LogoutResult actualResult = userService.logout(logoutRequest);
        LogoutResult expectedResult = new LogoutResult("Error: unauthorized");
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void clearAllUsersAndTokens() {
        AuthDAO authDAO = new MemoryAuthDAO();
        UserDAO users = new MemoryUserDAO();
        UserService userService = new UserService(authDAO, users);
        RegisterRequest registerRequest = new RegisterRequest("user1", "1234", "user1@gmail.com");
        RegisterResult registerResult = userService.register(registerRequest);
        userService.clearAllUsersAndTokens();
        assertNull(authDAO.getAuthToken(registerResult.authToken()));
        assertNull(users.getUser("user1"));
    }

    @Test
    void validCreateGame() {
        AuthDAO authDAO = new MemoryAuthDAO();
        UserDAO users = new MemoryUserDAO();
        GameDAO games = new MemoryGameDAO();
        UserService userService = new UserService(authDAO, users);
        GameService gameService = new GameService(authDAO, games);
        RegisterRequest registerRequest = new RegisterRequest("user1", "1234", "user1@gmail.com");
        RegisterResult registerResult = userService.register(registerRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest(registerResult.authToken(), "chess");
        CreateGameResult createGameResult = gameService.createGame(createGameRequest);
        GameData gameData = games.getGame(createGameResult.gameID());
        CreateGameResult expectedResult = new CreateGameResult(gameData.gameID());
        assertEquals(expectedResult, createGameResult);
    }

    @Test
    void invalidCreateGame() {
        AuthDAO authDAO = new MemoryAuthDAO();
        UserDAO users = new MemoryUserDAO();
        GameDAO games = new MemoryGameDAO();
        UserService userService = new UserService(authDAO, users);
        GameService gameService = new GameService(authDAO, games);
        RegisterRequest registerRequest = new RegisterRequest("user1", "1234", "user1@gmail.com");
        RegisterResult registerResult = userService.register(registerRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest("", "chess");
        CreateGameResult createGameResult = gameService.createGame(createGameRequest);
        CreateGameResult expectedResult = new CreateGameResult("Error: unauthorized");
        assertEquals(expectedResult, createGameResult);
    }

    @Test
    void validListGames() {
        AuthDAO authDAO = new MemoryAuthDAO();
        UserDAO users = new MemoryUserDAO();
        GameDAO games = new MemoryGameDAO();
        UserService userService = new UserService(authDAO, users);
        GameService gameService = new GameService(authDAO, games);
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
    void invalidListGames() {
        AuthDAO authDAO = new MemoryAuthDAO();
        UserDAO users = new MemoryUserDAO();
        GameDAO games = new MemoryGameDAO();
        UserService userService = new UserService(authDAO, users);
        GameService gameService = new GameService(authDAO, games);
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
    void clearAllGames() {
        AuthDAO authDAO = new MemoryAuthDAO();
        UserDAO users = new MemoryUserDAO();
        GameDAO games = new MemoryGameDAO();
        UserService userService = new UserService(authDAO, users);
        GameService gameService = new GameService(authDAO, games);
        RegisterRequest registerRequest = new RegisterRequest("user1", "1234", "user1@gmail.com");
        RegisterResult registerResult = userService.register(registerRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest(registerResult.authToken(), "chess");
        CreateGameResult createGameResult = gameService.createGame(createGameRequest);
        gameService.clearAllGames();
        HashMap<Integer, GameData> expectedGames = new HashMap<>();
        assertEquals(expectedGames, games.getGames());
    }

    @Test
    void validJoinGame() {
        AuthDAO authDAO = new MemoryAuthDAO();
        UserDAO users = new MemoryUserDAO();
        GameDAO games = new MemoryGameDAO();
        UserService userService = new UserService(authDAO, users);
        GameService gameService = new GameService(authDAO, games);
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
    void invalidJoinGame() {
        AuthDAO authDAO = new MemoryAuthDAO();
        UserDAO users = new MemoryUserDAO();
        GameDAO games = new MemoryGameDAO();
        UserService userService = new UserService(authDAO, users);
        GameService gameService = new GameService(authDAO, games);
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