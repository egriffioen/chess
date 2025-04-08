package result;

public record LeaveGameResult(String message) {
    public LeaveGameResult() {
        this(null);
    }

    public LeaveGameResult(String message) {
        this.message = message; // Error case
    }
}
