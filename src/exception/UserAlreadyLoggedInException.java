package exception;

public class UserAlreadyLoggedInException extends RuntimeException {
    public String name;

    public UserAlreadyLoggedInException(String name) {
        this.name = name;
    }

    @Override
    public String getMessage() {
        return "Exception: user " + name + " already logged in!";
    }
}