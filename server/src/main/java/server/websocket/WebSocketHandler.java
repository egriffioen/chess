package server.websocket;

import com.google.gson.Gson;
import dataaccess.*;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Timer;


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
    public void onMessage(Session session, String message) throws IOException, ResponseException {
        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        switch (userGameCommand.getCommandType()) {
            case CONNECT -> connect(userGameCommand.getAuthToken(), session, userGameCommand.getGameID());
//            case LEAVE -> leave(userGameCommand.getAuthToken());
//            case MAKE_MOVE -> makeMove(userGameCommand.getAuthToken(), userGameCommand.getGameID());
//            case RESIGN -> resign(userGameCommand.getAuthToken(), userGameCommand.getGameID());
        }
    }

    private void connect(String authToken, Session session, Integer gameID) throws IOException, ResponseException {
        AuthData authData = authDAO.getAuthToken(authToken);
        String username = authData.username();

        String teamColor = null;
        GameData gameData = gameDAO.getGame(gameID);
        if (username.equals(gameData.whiteUsername())) {
            teamColor = "WHITE";
        }
        if (username.equals(gameData.blackUsername())) {
            teamColor = "BLACK";
        }

        connections.add(username, session);
        var message = String.format("%s joined the game as %s", username, teamColor);
        var notification = new NotificationMessage(message);
        connections.broadcast(username, notification);
    }

//    private void exit(String visitorName) throws IOException {
//        connections.remove(visitorName);
//        var message = String.format("%s left the shop", visitorName);
//        var notification = new Notification(Notification.Type.DEPARTURE, message);
//        connections.broadcast(visitorName, notification);
//    }
//
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