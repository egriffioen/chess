package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.MemoryGameDAO;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.ListGamesRequest;
import result.CreateGameResult;
import result.JoinGameResult;
import result.ListGamesResult;

import java.util.List;
import java.util.Map;

public class GameService {
    private GameDAO games = new MemoryGameDAO();
    private AuthDAO authTokens;

    public GameService(AuthDAO authTokens) {
        this.authTokens = authTokens;
    }


    public CreateGameResult createGame(CreateGameRequest createGameRequest) {
        String authToken = createGameRequest.authToken();
        if (authTokens.getAuthToken(authToken)==null) {
            return new CreateGameResult("Error: unauthorized");
        }
        int gameID = games.createGame(createGameRequest.gameName());
        return new CreateGameResult(gameID);
    }

    public ListGamesResult listGames(ListGamesRequest listGamesRequest) {
        String authToken = listGamesRequest.authToken();
        if (authTokens.getAuthToken(authToken)==null) {
            return new ListGamesResult("Error: unauthorized");
        }
        List<Map<String, Object>> allGames = games.listGames();
        return new ListGamesResult(allGames);
    }

    public void clearAllGames() {
        games.clearAllGames();
    }

    public JoinGameResult joinGame(JoinGameRequest joinGameRequest) {
        String authToken = joinGameRequest.authToken();
        if (authTokens.getAuthToken(authToken)==null) {
            return new JoinGameResult("Error: unauthorized");
        }
        String username = authTokens.getAuthToken(authToken).username();
        boolean joinedGame = games.joinGame(joinGameRequest.playerColor(), joinGameRequest.gameID(), username);
        if (!joinedGame) {
            return new JoinGameResult("Error: already taken");
        }
        else {
            return new JoinGameResult();
        }
    }
}
