package exception;

public class TableNameCollisionException extends RuntimeException {
    private String name;

    public TableNameCollisionException(String name) {
        this.name = name;
    }

    @Override
    public String getMessage() {
        return "Exception: table name " + name + " collides with an existing view or table!";
    }
}