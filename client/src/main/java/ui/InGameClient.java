package ui;

import exception.ResponseException;
import facade.ServerFacade;
import model.GameData;
import request.CreateGameRequest;
import request.ListGamesRequest;
import result.CreateGameResult;
import result.ListGamesResult;

import java.util.Arrays;

import java.util.*;
import java.util.List;

public class InGameClient {
    private final ServerFacade server;
    private final String serverUrl;
    private final String colorPerspective;
    private int gameID;
    private String authToken;

    public InGameClient(String serverUrl, int gameID, String authToken, String colorPerspective) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.gameID = gameID;
        this.authToken = authToken;
        this.colorPerspective = colorPerspective;
    }

    public String eval(String input) throws ResponseException {
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (cmd) {
            case "redraw" -> redraw();
            case "quit" -> "quit --> Returning to Lobby";
            default -> help();
        };
    }

    public String help() {
        return """
                - redraw chess board -> redraw the chessboard
                - leave -> exit the game
                - resign -> forfeit the game
                - make move <current pos> <new position> -> move a piece on your turn
                - highlight legal moves <position> -> see all possible moves for a piece
                - quit -> quit playing chess
                - help -> help with possible commands
                """;
    }

    public void printChessBoard(String colorPerspective) throws ResponseException {
        ListGamesRequest listGamesRequest = new ListGamesRequest(authToken);
        ListGamesResult listGamesResult = server.listGames(listGamesRequest);
        ArrayList<GameData> allGames = listGamesResult.games();
        GameData currentGame = null;
        for (GameData game : allGames) {
            if (gameID == game.gameID()) {
                currentGame = game;
            }
        }
        PrintChess printedBoard = new PrintChess(colorPerspective, currentGame.game());
        printedBoard.print();
    }

    public String redraw(String... params) throws ResponseException {
        printChessBoard(colorPerspective);
        return "Redrawn board";
    }
}