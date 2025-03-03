package result;

public record JoinGameResult(String message) {
    public JoinGameResult() {
        this(null);
    }

    public JoinGameResult(String message) {
        this.message = message; // Error case
    }
}
