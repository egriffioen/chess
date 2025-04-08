package result;

public record UpdateGameResult(String message) {
    public UpdateGameResult() {
        this(null);
    }

    public UpdateGameResult(String message) {
        this.message = message; // Error case
    }
}
