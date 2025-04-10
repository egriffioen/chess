package websocket.messages;

import com.google.gson.Gson;

public class ErrorMessage extends ServerMessage{
    private final String errorMessage;

    public ErrorMessage(String errorMessage) {
        super(ServerMessageType.ERROR); // Always set the type to NOTIFICATION
        this.errorMessage = errorMessage;
    }

    public String getMessage() {
        return errorMessage;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
