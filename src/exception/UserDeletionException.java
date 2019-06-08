package exception;

public class UserDeletionException extends RuntimeException {
    String name;

    public UserDeletionException(String name) {
        this.name = name;
    }

    @Override
    public String getMessage() {
        return "Exception: user " + name + " is currently being used!";
    }
}
