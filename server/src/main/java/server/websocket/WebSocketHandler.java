package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.*;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.util.Collection;


@WebSocket
public class WebSocketHandler {
    AuthDAO authDAO;
    GameDAO gameDAO;
    private final ConnectionManager connections = new ConnectionManager();

    public WebSocketHandler(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, ResponseException, InvalidMoveException {
        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        switch (userGameCommand.getCommandType()) {
            case CONNECT -> connect(userGameCommand.getAuthToken(), session, userGameCommand.getGameID());
            case LEAVE -> leave(userGameCommand.getAuthToken(), userGameCommand.getGameID());
            case MAKE_MOVE -> makeMove(new Gson().fromJson(message, MakeMoveCommand.class));
            case RESIGN -> resign(userGameCommand.getAuthToken(), userGameCommand.getGameID());
        }
    }

    private void connect(String authToken, Session session, Integer gameID) throws IOException, ResponseException {
        AuthData authData = authDAO.getAuthToken(authToken);
        if (authData == null) {
            connections.add("invalidAuthtoken", session);
            var error = new ErrorMessage("Invalid Authtoken");
            connections.broadcastRootClient("invalidAuthtoken", error);
            connections.remove("invalidAuthtoken");
            return;
        }
        String username = authData.username();

        String teamColor = null;
        GameData gameData = gameDAO.getGame(gameID);
        if (gameData == null) {
            connections.add(authToken, session);
            var error = new ErrorMessage("Invalid GameID");
            connections.broadcastRootClient(authToken, error);
            connections.remove(authToken);
            return;
        }
        if (username.equals(gameData.whiteUsername())) {
            teamColor = "WHITE";
        }
        if (username.equals(gameData.blackUsername())) {
            teamColor = "BLACK";
        }
        if (teamColor==null) {
            teamColor = "Observer";
        }

        //TODO SEND LOAD_GAME BACK TO CLIENT
        connections.add(authToken, session);
        var message = String.format("%s joined the game as %s", username, teamColor);
        var notification = new NotificationMessage(message);
        connections.broadcastAllOthers(authToken, notification);
        var loadGame = new LoadGameMessage(String.format("Loading Game %d", gameID));
        connections.broadcastRootClient(authToken, loadGame);
    }

    private void leave(String authToken, Integer gameID) throws IOException, ResponseException {
        connections.remove(authToken);
        AuthData authData = authDAO.getAuthToken(authToken);
        String username = authData.username();
        String teamColor = null;
        GameData gameData = gameDAO.getGame(gameID);
        if (username.equals(gameData.whiteUsername())) {
            teamColor = "WHITE";
            gameDAO.leaveGame("WHITE", gameID, username);
        }
        if (username.equals(gameData.blackUsername())) {
            teamColor = "BLACK";
            gameDAO.leaveGame("BLACK", gameID, username);
        }
        if (teamColor==null) {
            teamColor = "Observer";
        }


        var message = String.format("%s left the game", username);
        var notification = new NotificationMessage(message);
        connections.broadcastAllOthers(authToken, notification);
    }

    private void resign(String authToken, Integer gameID) throws IOException, ResponseException {
        AuthData authData = authDAO.getAuthToken(authToken);
        String username = authData.username();
        GameData gameData = gameDAO.getGame(gameID);
        if(!username.equals(gameData.whiteUsername()) && !username.equals(gameData.blackUsername())) {
            var error = new ErrorMessage("Observer can not resign");
            connections.broadcastRootClient(authToken, error);
            return;
        }

        if (gameData.game().isGameResigned()) {
            var error = new ErrorMessage("Game already resigned");
            connections.broadcastRootClient(authToken, error);
            return;
        }
        gameData.game().setGameResigned(true);
        gameDAO.updateGame(gameID, gameData);
        var message = String.format("%s resigned the game", username);
        var notification = new NotificationMessage(message);
        connections.broadcastAllOthers(authToken, notification);
        connections.broadcastRootClient(authToken, notification);
    }

    private void makeMove(MakeMoveCommand makeMoveCommand) throws IOException, ResponseException, InvalidMoveException {
        String authToken = makeMoveCommand.getAuthToken();
        Integer gameID = makeMoveCommand.getGameID();
        ChessMove move = makeMoveCommand.getMove();
        AuthData authData = authDAO.getAuthToken(authToken);
        String username = authData.username();
        GameData gameData = gameDAO.getGame(gameID);
        ChessGame game = gameData.game();
        ChessGame.TeamColor teamColor = null;
        if (username.equals(gameData.whiteUsername())) {
            teamColor = ChessGame.TeamColor.WHITE;
        }
        if (username.equals(gameData.blackUsername())) {
            teamColor = ChessGame.TeamColor.BLACK;
        }
        if(!username.equals(gameData.whiteUsername()) && !username.equals(gameData.blackUsername())) {
            var error = new ErrorMessage("Observer can not make move");
            connections.broadcastRootClient(authToken, error);
            return;
        }

        if (gameData.game().isGameResigned()) {
            var error = new ErrorMessage("Game already over");
            connections.broadcastRootClient(authToken, error);
            return;
        }

        Collection<ChessMove> validMoves = game.validMoves(move.getStartPosition());
        for(ChessMove validMove: validMoves) {
            if (move.equals(validMove)) {
                game.makeMove(move);
                gameData = new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
                gameDAO.updateGame(gameID, gameData);

                var message = String.format("Loading Game");
                var loadGame = new LoadGameMessage(message);
                connections.broadcastAllOthers(authToken, loadGame);
                connections.broadcastRootClient(authToken, loadGame);

                var notificationMessage = String.format("%s moved %s to %s", username, move.getStartPosition().toString(), move.getEndPosition().toString());
                var notification = new NotificationMessage(notificationMessage);
                connections.broadcastAllOthers(authToken, notification);

                if (game.isInCheckmate(ChessGame.TeamColor.BLACK)) {
                    var checkMate = String.format("%s (Black) is in Checkmate", gameData.blackUsername());
                    var checkMateNotification = new NotificationMessage(checkMate);
                    connections.broadcastAllOthers(authToken, checkMateNotification);
                    connections.broadcastRootClient(authToken, checkMateNotification);
                }
                if (game.isInCheckmate(ChessGame.TeamColor.WHITE)) {
                    var checkMate = String.format("%s (White) is in Checkmate", gameData.whiteUsername());
                    var checkMateNotification = new NotificationMessage(checkMate);
                    connections.broadcastAllOthers(authToken, checkMateNotification);
                    connections.broadcastRootClient(authToken, checkMateNotification);
                }

                if (game.isInCheck(ChessGame.TeamColor.BLACK)) {
                    var check = String.format("%s (Black) is in Check", gameData.blackUsername());
                    var checkNotification = new NotificationMessage(check);
                    connections.broadcastAllOthers(authToken, checkNotification);
                    connections.broadcastRootClient(authToken, checkNotification);
                }
                if (game.isInCheck(ChessGame.TeamColor.WHITE)) {
                    var check = String.format("%s (White) is in Check", gameData.whiteUsername());
                    var checkNotification = new NotificationMessage(check);
                    connections.broadcastAllOthers(authToken, checkNotification);
                    connections.broadcastRootClient(authToken, checkNotification);
                }

                if (game.isInStalemate(ChessGame.TeamColor.BLACK)) {
                    var staleMate = String.format("%s (Black) is in Stalemate", gameData.blackUsername());
                    var staleMateNotification = new NotificationMessage(staleMate);
                    connections.broadcastAllOthers(authToken, staleMateNotification);
                    connections.broadcastRootClient(authToken, staleMateNotification);
                }
                if (game.isInStalemate(ChessGame.TeamColor.WHITE)) {
                    var staleMate = String.format("%s (White) is in Stalemate", gameData.whiteUsername());
                    var staleMateNotification = new NotificationMessage(staleMate);
                    connections.broadcastAllOthers(authToken, staleMateNotification);
                    connections.broadcastRootClient(authToken, staleMateNotification);
                }
            }
        }

    }

//    public void makeNoise(String petName, String sound) throws ResponseException {
//        try {
//            var message = String.format("%s says %s", petName, sound);
//            var notification = new Notification(Notification.Type.NOISE, message);
//            connections.broadcast("", notification);
//        } catch (Exception ex) {
//            throw new ResponseException(500, ex.getMessage());
//        }
//    }
}