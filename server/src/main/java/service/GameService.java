package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.ListGamesRequest;
import result.CreateGameResult;
import result.JoinGameResult;
import result.ListGamesResult;

import java.util.List;
import java.util.Map;

public class GameService {
    private GameDAO games;
    private AuthDAO authTokens;

    public GameService(AuthDAO authTokens, GameDAO games) {
        this.authTokens = authTokens;
        this.games = games;
    }


    public CreateGameResult createGame(CreateGameRequest createGameRequest) throws DataAccessException {
        String authToken = createGameRequest.authToken();
        if (authTokens.getAuthToken(authToken)==null) {
            return new CreateGameResult("Error: unauthorized");
        }
        int gameID = games.createGame(createGameRequest.gameName());
        return new CreateGameResult(gameID);
    }

    public ListGamesResult listGames(ListGamesRequest listGamesRequest) throws DataAccessException {
        String authToken = listGamesRequest.authToken();
        if (authTokens.getAuthToken(authToken)==null) {
            return new ListGamesResult("Error: unauthorized");
        }
        List<Map<String, Object>> allGames = games.listGames();
        return new ListGamesResult(allGames);
    }

    public void clearAllGames() throws DataAccessException {
        games.clearAllGames();
    }

    public JoinGameResult joinGame(JoinGameRequest joinGameRequest) throws DataAccessException {
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
