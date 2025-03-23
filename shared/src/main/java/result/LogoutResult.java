package result;

public record LogoutResult(String message) {
    public LogoutResult() {
        this(null);
    }

    public LogoutResult(String message) {
        this.message = message; // Error case
    }
}