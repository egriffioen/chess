package ui;

import chess.*;
import exception.ResponseException;
import facade.ServerFacade;
import model.GameData;
import request.LeaveGameRequest;
import request.ListGamesRequest;
import request.UpdateGameRequest;
import result.LeaveGameResult;
import result.ListGamesResult;
import result.UpdateGameResult;

import java.util.Arrays;

import java.util.*;
import java.util.List;

import ui.websocket.NotificationHandler;
import ui.websocket.WebSocketFacade;

public class InGameClient {
    private final ServerFacade server;
    private final String serverUrl;
    private final String colorPerspective;
    private int gameID;
    private String authToken;
    private boolean observer;
    private boolean gameComplete;
    private final NotificationHandler notificationHandler;
    private WebSocketFacade ws;

    public InGameClient(String serverUrl, int gameID, String authToken, String colorPerspective, boolean observer,
                        NotificationHandler notificationHandler, WebSocketFacade ws) throws ResponseException {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.gameID = gameID;
        this.authToken = authToken;
        this.colorPerspective = colorPerspective;
        this.observer = observer;
        this.gameComplete = getCurrentGame().game().isGameResigned();
        this.notificationHandler = notificationHandler;
        this.ws = ws;
    }

    public String eval(String input) throws ResponseException, InvalidMoveException {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "highlight" -> highlightMoves(params);
                case "resign" -> resign();
                case "move" -> makeMove(params);
                case "leave" -> leave();
                case "redraw" -> redraw();
                case "quit" -> "quit --> Returning to Lobby";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String help() {
        return """
                - redraw chess board -> redraw the chessboard
                - leave -> exit the game
                - resign -> forfeit the game
                - move <current pos> <new position> -> make move a piece on your turn
                - highlight <position> -> highlight all possible moves for a piece
                - quit -> quit playing chess
                - help -> help with possible commands
                """;
    }

    public void printChessBoard(String colorPerspective) throws ResponseException {
        GameData currentGame = getCurrentGame();
        PrintChess printedBoard = new PrintChess(colorPerspective, currentGame.game());
        System.out.println();
        printedBoard.print();
    }

    public void printHighlightedChessBoard(String colorPerspective, Collection<ChessMove> validMoves, ChessPosition position)
            throws ResponseException {
        GameData currentGame = getCurrentGame();
        PrintHighlightedChess highlightedBoard = new PrintHighlightedChess(colorPerspective, currentGame.game(), validMoves, position);
        highlightedBoard.print();
    }

    public String redraw(String... params) throws ResponseException {
        printChessBoard(colorPerspective);
        return "Redrawn board";
    }

    public String leave(String... params) throws ResponseException {
        if (observer) {
            ws.leave(authToken, gameID);
            return String.format("You left game #%d", gameID);
        }
        else if (Objects.equals(colorPerspective, "WHITE")) {
            ws.leave(authToken, gameID);
            return String.format("You left game #%d", gameID);

        }
        else if (Objects.equals(colorPerspective, "BLACK")) {
            ws.leave(authToken, gameID);
            return String.format("You left game #%d", gameID);
        }
        else {
            return "Can't leave the game";
        }
    }

    public String makeMove(String... params) throws ResponseException, InvalidMoveException {
        if (observer) {
            return "You are observing, you can't make moves";
        }

        if (params.length>3) {
            throw new ResponseException(400, "Expected: move <current pos> <new position>");
        }
        String startpos = params[0];
        String endpos = params[1];

        if (startpos.length()!=2 || endpos.length() != 2) {
            throw new ResponseException(400, "Invalid position");
        }
        if (!isValidPosition(startpos) || !isValidPosition(endpos)) {
            throw new ResponseException(400, "Invalid position");
        }

        char startPosColChar = startpos.charAt(0);
        int startPosCol = startPosColChar - 'a' + 1;
        int startPosRow = Character.getNumericValue(startpos.charAt(1));
        ChessPosition startPosition = new ChessPosition(startPosRow, startPosCol);

        char endPosColChar = endpos.charAt(0);
        int endPosCol = endPosColChar - 'a' + 1;
        int endPosRow = Character.getNumericValue(endpos.charAt(1));
        ChessPosition endPosition = new ChessPosition(endPosRow, endPosCol);

        GameData currentGameData = getCurrentGame();
        ChessGame chessGame = currentGameData.game();
        if (chessGame.isGameResigned()) {
            return "Game is resigned, can't make move";
        }
        if (!chessGame.getTeamTurn().toString().equalsIgnoreCase(colorPerspective)) {
            return "Not your turn";
        }
        if (chessGame.isInCheckmate(ChessGame.TeamColor.BLACK) || chessGame.isInCheckmate(ChessGame.TeamColor.WHITE)) {
            gameComplete = true;
            return "Game is in Checkmate";
        }
        if (chessGame.isInStalemate(ChessGame.TeamColor.BLACK) || chessGame.isInStalemate(ChessGame.TeamColor.WHITE)) {
            gameComplete = true;
            return "Game is in Stalemate";
        }

        ChessMove move = null;
        if (params.length == 2) {
            move = new ChessMove(startPosition, endPosition, null);
        }
        else {
            String promotion = params[2];
            ChessPiece.PieceType type = ChessPiece.PieceType.valueOf(promotion);
            move = new ChessMove(startPosition, endPosition, type);
        }
        try {
            chessGame.makeMove(move);
        }
        catch (InvalidMoveException e) {
            throw new ResponseException(400, "Invalid Move: " + e.getMessage());
        }
        ws.makeMove(authToken, gameID, move);
        return String.format("You moved %s to %s.", startpos, endpos);
    }

    public GameData getCurrentGame() throws ResponseException {
        ListGamesRequest listGamesRequest = new ListGamesRequest(authToken);
        ListGamesResult listGamesResult = server.listGames(listGamesRequest);
        ArrayList<GameData> allGames = listGamesResult.games();
        GameData currentGame = null;
        for (GameData game : allGames) {
            if (gameID == game.gameID()) {
                currentGame = game;
            }
        }
        return currentGame;
    }

    public String resign() throws ResponseException {
        GameData gameData = getCurrentGame();
        ChessGame chessGame = gameData.game();
        if (observer) {
            return "You are an observer, you cannot resign";
        }
        if(chessGame.isGameResigned()) {
            return "Game is already resigned";
        }
        if(chessGame.isInCheckmate(ChessGame.TeamColor.BLACK) || chessGame.isInCheckmate(ChessGame.TeamColor.WHITE)) {
            return "Game is already over by Checkmate";
        }
        if(chessGame.isInStalemate(ChessGame.TeamColor.BLACK) || chessGame.isInStalemate(ChessGame.TeamColor.WHITE)) {
            return "Game is already over by Stalemate";
        }
        System.out.println("Are you sure you want to resign? y/n");
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        if (line.equalsIgnoreCase("y")||line.equalsIgnoreCase("yes")){
            gameComplete = true;
            ws.resign(authToken, gameID);
            return "You resigned from the game. Game is over";
        }
        else {
            return "You did not resign. Keep playing";
        }
    }

    public String highlightMoves(String... params) throws ResponseException {
        GameData gameData = getCurrentGame();
        ChessGame chessGame = gameData.game();

        if (params.length!=1) {
            throw new ResponseException(400, "Expected: highlight <position>");
        }
        String startpos = params[0];
        if (startpos.length()!=2) {
            throw new ResponseException(400, "Invalid position");
        }
        if (!isValidPosition(startpos)) {
            throw new ResponseException(400, "Invalid position");
        }

        char startPosColChar = startpos.charAt(0);
        int startPosCol = startPosColChar - 'a' + 1;
        int startPosRow = Character.getNumericValue(startpos.charAt(1));
        ChessPosition position = new ChessPosition(startPosRow, startPosCol);

        if(chessGame.getBoard().getPiece(position)==null) {
            throw new ResponseException(400, String.format("No piece at %s", startpos));
        }

        Collection<ChessMove> validMoves = chessGame.validMoves(position);
        printHighlightedChessBoard(colorPerspective, validMoves, position);
        return String.format("Valid moves for %s", startpos);
    }

    private boolean isValidPosition(String position) {
        String validLetters = "abcdefgh";
        String validNumbers = "12345678";
        char char1 = position.charAt(0);
        char char2 = position.charAt(1);
        return validLetters.indexOf(char1) != -1 && validNumbers.indexOf(char2) != -1;
    }


}