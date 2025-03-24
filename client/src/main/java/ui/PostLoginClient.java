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
//                case "create" -> create(params);
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

//    public String register(String... params) throws ResponseException {
//        if (params.length == 3) {
//            username = params[0];
//            password = params[1];
//            email = params[2];
//            RegisterRequest registerRequest = new RegisterRequest(username, password, email);
//            RegisterResult registerResult = server.register(registerRequest);
//            state = State.SIGNEDIN;
//            return String.format("You signed in as %s.", registerResult.username());
//        }
//        throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD> <EMAIL>");
//    }


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