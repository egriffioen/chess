package ui;

import java.util.Arrays;

import exception.ResponseException;

import facade.ServerFacade;
import request.*;
import result.*;

public class PostLoginClient {
    private final String authToken;
    private final ServerFacade server;
    private final String serverUrl;
    private State state = State.SIGNEDOUT;

    public PostLoginClient(String serverUrl, String authToken) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.authToken = authToken;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "logout" -> logout(params);
                case "create" -> create(params);
//                case "list" -> list(params);
//                case "join" -> join(params);
//                case "observe" -> observe(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String logout(String... params) throws ResponseException {
        if (authToken != null) {
            LogoutRequest logoutRequest = new LogoutRequest(authToken);
            LogoutResult logoutResult = server.logout(logoutRequest);
            state = State.SIGNEDOUT;
            return String.format("You logged out");
        }
        throw new ResponseException(401, "Unauthorized");
    }



    public String create(String... params) throws ResponseException {
        if (params.length == 1) {
            var gameName = params[0];
            CreateGameRequest createGameRequest = new CreateGameRequest(authToken, gameName);
            CreateGameResult createGameResult = server.createGame(createGameRequest);
            state = State.INGAME;
            return String.format("You created a new game: %s.", gameName);
        }
        throw new ResponseException(400, "Expected: <NAME>");
    }


    public String help() {
        return """
                    - logout
                    - create <NAME> -> create a game
                    - list -> list all games
                    - join <ID> [WHITE|BLACK] -> join a game
                    - observe <ID> -> observe a game
                    - quit -> quit playing chess
                    - help -> help with possible commands
                    """;
    }


    private void assertSignedIn() throws ResponseException {
        if (state == State.SIGNEDOUT) {
            throw new ResponseException(400, "You must sign in");
        }
    }
}