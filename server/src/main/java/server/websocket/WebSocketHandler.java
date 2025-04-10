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
            case MAKE_MOVE -> makeMove(new Gson().fromJson(message, MakeMoveCommand.class), session);
            case RESIGN -> resign(userGameCommand.getAuthToken(), userGameCommand.getGameID());
        }
    }

    private void connect(String authToken, Session session, Integer gameID) throws IOException, ResponseException {
        AuthData authData = authDAO.getAuthToken(authToken);
        if (authData == null) {
            connections.add(authToken, session, gameID);
            var error = new ErrorMessage("Invalid Authtoken");
            connections.broadcastRootClient(authToken, error, gameID);
            connections.remove(authToken);
            return;
        }
        String username = authData.username();


        String teamColor = null;
        GameData gameData = gameDAO.getGame(gameID);
        if (gameData == null) {
            connections.add(username, session, gameID);
            var error = new ErrorMessage("Invalid GameID");
            connections.broadcastRootClient(username, error, gameID);
            connections.remove(username);
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

        connections.add(username, session, gameID);
        var message = String.format("%s joined the game as %s", username, teamColor);
        var notification = new NotificationMessage(message);
        connections.broadcastAllOthers(username, notification, gameID);
        var loadGame = new LoadGameMessage(gameData.game());
        connections.broadcastRootClient(username, loadGame, gameID);
    }

    private void leave(String authToken, Integer gameID) throws IOException, ResponseException {
        AuthData authData = authDAO.getAuthToken(authToken);
        String username = authData.username();
        connections.remove(username);
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
        connections.broadcastAllOthers(username, notification, gameID);
    }

    private void resign(String authToken, Integer gameID) throws IOException, ResponseException {
        AuthData authData = authDAO.getAuthToken(authToken);
        String username = authData.username();
        GameData gameData = gameDAO.getGame(gameID);
        if(!username.equals(gameData.whiteUsername()) && !username.equals(gameData.blackUsername())) {
            var error = new ErrorMessage("Observer can not resign");
            connections.broadcastRootClient(username, error, gameID);
            return;
        }

        if (gameData.game().isGameResigned()) {
            var error = new ErrorMessage("Game already resigned");
            connections.broadcastRootClient(username, error, gameID);
            return;
        }
        gameData.game().setGameResigned(true);
        gameDAO.updateGame(gameID, gameData);
        var message = String.format("%s resigned the game", username);
        var notification = new NotificationMessage(message);
        connections.broadcastAllOthers(username, notification, gameID);
        connections.broadcastRootClient(username, notification, gameID);
    }

    private void makeMove(MakeMoveCommand makeMoveCommand, Session session) throws IOException, ResponseException, InvalidMoveException {
        String authToken = makeMoveCommand.getAuthToken();
        Integer gameID = makeMoveCommand.getGameID();
        ChessMove move = makeMoveCommand.getMove();
        AuthData authData = authDAO.getAuthToken(authToken);
        if (authData == null) {
            var error = new ErrorMessage("Invalid Authtoken");
            session.getRemote().sendString(error.toString());
            return;
        }
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
            connections.broadcastRootClient(username, error, gameID);
            return;
        }

        if (gameData.game().isGameResigned()) {
            var error = new ErrorMessage("Game already over");
            connections.broadcastRootClient(username, error, gameID);
            return;
        }
        if (game.getBoard().getPiece(move.getStartPosition()).getTeamColor()!=teamColor) {
            var error = new ErrorMessage("Not your team's piece");
            connections.broadcastRootClient(username, error, gameID);
            return;
        }

        try {
            game.makeMove(move);
            gameData = new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
            gameDAO.updateGame(gameID, gameData);

            var loadGame = new LoadGameMessage(gameData.game());
            connections.broadcastAllOthers(username, loadGame, gameID);
            connections.broadcastRootClient(username, loadGame, gameID);

            var notificationMessage = String.format("%s moved %s to %s",
                    username, move.getStartPosition().toString(), move.getEndPosition().toString());
            var notification = new NotificationMessage(notificationMessage);
            connections.broadcastAllOthers(username, notification, gameID);

            checkEndGame(gameData, username, gameID);

        }
        catch (InvalidMoveException e) {
            var error = new ErrorMessage(e.getMessage());
            connections.broadcastRootClient(username, error, gameID);
        }
    }

    private void checkEndGame(GameData gameData, String username, Integer gameID) throws ResponseException, IOException {
        ChessGame game = gameData.game();
        if (game.isInCheckmate(ChessGame.TeamColor.BLACK)) {
            gameData.game().setGameResigned(true);
            gameDAO.updateGame(gameID, gameData);
            var checkMate = String.format("%s (Black) is in Checkmate", gameData.blackUsername());
            var checkMateNotification = new NotificationMessage(checkMate);
            connections.broadcastAllOthers(username, checkMateNotification, gameID);
            connections.broadcastRootClient(username, checkMateNotification, gameID);
            return;
        }
        if (game.isInCheckmate(ChessGame.TeamColor.WHITE)) {
            gameData.game().setGameResigned(true);
            gameDAO.updateGame(gameID, gameData);
            var checkMate = String.format("%s (White) is in Checkmate", gameData.whiteUsername());
            var checkMateNotification = new NotificationMessage(checkMate);
            connections.broadcastAllOthers(username, checkMateNotification, gameID);
            connections.broadcastRootClient(username, checkMateNotification, gameID);
            return;
        }

        if (game.isInCheck(ChessGame.TeamColor.BLACK)) {
            var check = String.format("%s (Black) is in Check", gameData.blackUsername());
            var checkNotification = new NotificationMessage(check);
            connections.broadcastAllOthers(username, checkNotification, gameID);
            connections.broadcastRootClient(username, checkNotification, gameID);
            return;
        }
        if (game.isInCheck(ChessGame.TeamColor.WHITE)) {
            var check = String.format("%s (White) is in Check", gameData.whiteUsername());
            var checkNotification = new NotificationMessage(check);
            connections.broadcastAllOthers(username, checkNotification, gameID);
            connections.broadcastRootClient(username, checkNotification, gameID);
            return;
        }

        if (game.isInStalemate(ChessGame.TeamColor.BLACK)) {
            gameData.game().setGameResigned(true);
            gameDAO.updateGame(gameID, gameData);
            var staleMate = String.format("%s (Black) is in Stalemate", gameData.blackUsername());
            var staleMateNotification = new NotificationMessage(staleMate);
            connections.broadcastAllOthers(username, staleMateNotification, gameID);
            connections.broadcastRootClient(username, staleMateNotification, gameID);
            return;
        }
        if (game.isInStalemate(ChessGame.TeamColor.WHITE)) {
            gameData.game().setGameResigned(true);
            gameDAO.updateGame(gameID, gameData);
            var staleMate = String.format("%s (White) is in Stalemate", gameData.whiteUsername());
            var staleMateNotification = new NotificationMessage(staleMate);
            connections.broadcastAllOthers(username, staleMateNotification, gameID);
            connections.broadcastRootClient(username, staleMateNotification, gameID);
            return;
        }
    }

}