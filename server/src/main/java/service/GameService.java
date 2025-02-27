package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.MemoryGameDAO;
import request.CreateGameRequest;
import result.CreateGameResult;

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
}
