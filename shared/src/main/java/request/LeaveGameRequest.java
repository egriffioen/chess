package request;

public record LeaveGameRequest(String authToken, String playerColor, Integer gameID) {
}
