package result;

public record CreateGameResult(Integer gameID, String message) {
    public CreateGameResult(int gameID) {
        this(gameID, null);
    }

    public CreateGameResult(String message) {
        this(null, message);
    }
}