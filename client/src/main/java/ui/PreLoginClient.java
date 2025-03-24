package ui;

import java.util.Arrays;

import exception.ResponseException;

import facade.ServerFacade;
import request.*;
import result.*;

public class PreLoginClient {
    private String username = null;
    private String password = null;
    private String email = null;
    private final ServerFacade server;
    private final String serverUrl;
    private State state = State.SIGNEDOUT;
    private String authToken = null;

    public PreLoginClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "login" -> login(params);
                case "register" -> register(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String login(String... params) throws ResponseException {
        if (params.length == 2) {
            username = params[0];
            password = params[1];
            LoginRequest loginRequest = new LoginRequest(username, password);
            LoginResult loginResult = server.login(loginRequest);
            state = State.SIGNEDIN;
            authToken = loginResult.authToken();
            return String.format("You signed in as %s.", loginResult.username());
        }
        throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD>");
    }

    public String register(String... params) throws ResponseException {
        if (params.length == 3) {
            username = params[0];
            password = params[1];
            email = params[2];
            RegisterRequest registerRequest = new RegisterRequest(username, password, email);
            RegisterResult registerResult = server.register(registerRequest);
            state = State.SIGNEDIN;
            authToken = registerResult.authToken();
            return String.format("You signed in as %s.", registerResult.username());
        }
        throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD> <EMAIL>");
    }


    public String help() {
            return """
                    - register <USERNAME> <PASSWORD> <EMAIL> -> to create an account
                    - login <USERNAME> <PASSWORD> -> to play chess
                    - quit -> quit playing chess
                    - help -> help with possible commands
                    """;
    }


    public String getAuthToken() {
        return authToken;
    }

}