package exception;

public class ViewNameCollisionException extends RuntimeException {
    String name;

    public ViewNameCollisionException(String name) {
        this.name = name;
    }

    @Override
    public String getMessage() {
        return "Exception: view name " + name + " collides with an existing view or table!";
    }
}
