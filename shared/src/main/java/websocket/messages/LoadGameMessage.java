package websocket.messages;

public class LoadGameMessage extends ServerMessage{
    private final String game;

    public LoadGameMessage(String game) {
        super(ServerMessageType.LOAD_GAME); // Always set the type to NOTIFICATION
        this.game = game;
    }

    public String getMessage() {
        return game;
    }

    @Override
    public String toString() {
        return "NotificationMessage: " + game;
    }
}
