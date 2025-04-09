package ui.websocket;

import exception.ResponseException;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

public interface NotificationHandler {
    void notify(NotificationMessage message);
    void notify(LoadGameMessage message) throws ResponseException;
    void notify(ErrorMessage message);
}