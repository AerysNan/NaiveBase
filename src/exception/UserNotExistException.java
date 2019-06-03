package exception;

public class UserNotExistException extends RuntimeException {
    private String name;

    public UserNotExistException(String name) {
        this.name = name;
    }

    @Override
    public String getMessage() {
        return "Exception: user " + name + " doesn't exist!";
    }
}