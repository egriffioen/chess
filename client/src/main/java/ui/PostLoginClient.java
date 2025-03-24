package ui;

import java.util.Arrays;
import java.util.Map;
import java.util.List;
import java.util.Objects;

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
                case "list" -> list(params);
                case "join" -> join(params);
                case "observe" -> observe(params);
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
            state = State.SIGNEDIN;
            return String.format("You created a new game: %s.", gameName);
        }
        throw new ResponseException(400, "Expected: <NAME>");
    }

    public String join(String... params) throws ResponseException {
        try {
            if (params.length == 2) {
                int gameID = Integer.parseInt(params[0]);
                var playerColor = params[1];

                ListGamesRequest listGamesRequest = new ListGamesRequest(authToken);
                ListGamesResult listGamesResult = server.listGames(listGamesRequest);
                List<Map<String, Object>> games = listGamesResult.games();

                if (games == null || games.isEmpty()) {
                    throw new ResponseException(400, "No available games to join.");
                }

                boolean gameExists = false;
                for (Map<String, Object> game : games) {
                    var gameIDFromList = ((Number) game.get("gameID")).intValue();
                    if (gameIDFromList == gameID) {
                        gameExists = true;
                        break;
                    }
                }
                if (!gameExists) {
                    throw new ResponseException(400, "Invalid game ID. Please choose a valid game.");
                }

                if (Objects.equals(playerColor.toUpperCase(), "WHITE")) {
                    JoinGameRequest joinGameRequest = new JoinGameRequest(authToken, "WHITE", gameID);
                    JoinGameResult joinGameResult = server.joinGame(joinGameRequest);
                    state = State.INGAME;
                    return String.format("You joined game #%d.", gameID);

                }
                else if (Objects.equals(playerColor.toUpperCase(), "BLACK")) {
                    JoinGameRequest joinGameRequest = new JoinGameRequest(authToken, "BLACK", gameID);
                    JoinGameResult joinGameResult = server.joinGame(joinGameRequest);
                    state = State.INGAME;
                    return String.format("You joined game #%d.", gameID);

                }
                else {
                    throw new ResponseException(400, "Expected: <ID> [WHITE|BLACK]");
                }

            }
        } catch (NumberFormatException e) {
            throw new ResponseException(400, "Game ID must be a number.");
        }
        throw new ResponseException(400, "Expected: <ID> [WHITE|BLACK");
    }

    public String list(String... params) throws ResponseException {
        if (authToken != null) {
            ListGamesRequest listGamesRequest = new ListGamesRequest(authToken);
            ListGamesResult listGamesResult = server.listGames(listGamesRequest);
            List<Map<String, Object>> games = listGamesResult.games();
            if (games == null || games.isEmpty()) {
                return "No games available.";
            }

            StringBuilder result = new StringBuilder("Current Games:\n");
            int index = 1; // Start numbering from 1

            for (Map<String, Object> game : games) {
                String gameName = (String) game.get("gameName");
                String whitePlayer = (String) game.getOrDefault("whiteUsername", "");
                String blackPlayer = (String) game.getOrDefault("blackUsername", "");

                result.append(String.format("%d. GameName: %s --> WhitePlayer: %s, BlackPlayer: %s%n", index++, gameName, whitePlayer, blackPlayer));
            }

            return result.toString();
        }
        throw new ResponseException(401, "Unauthorized");
    }

    public String observe(String... params) throws ResponseException {
        try {
            if (params.length == 1) {
                int gameID = Integer.parseInt(params[0]);

                ListGamesRequest listGamesRequest = new ListGamesRequest(authToken);
                ListGamesResult listGamesResult = server.listGames(listGamesRequest);
                List<Map<String, Object>> games = listGamesResult.games();

                if (games == null || games.isEmpty()) {
                    throw new ResponseException(400, "No available games to observe.");
                }

                boolean gameExists = false;
                for (Map<String, Object> game : games) {
                    var gameIDFromList = ((Number) game.get("gameID")).intValue();
                    if (gameIDFromList == gameID) {
                        gameExists = true;
                        break;
                    }
                }
                if (!gameExists) {
                    throw new ResponseException(400, "Invalid game ID. Please choose a valid game.");
                }
                else {
                    return String.format("You are observing game #%d.", gameID);
                }

            }
        } catch (NumberFormatException e) {
            throw new ResponseException(400, "Game ID must be a number.");
        }
        throw new ResponseException(400, "Expected: <ID>");
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