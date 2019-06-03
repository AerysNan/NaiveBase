package exception;

public class UserAlreadyExistsException extends RuntimeException {
    private String name;

    public UserAlreadyExistsException(String name) {
        this.name = name;
    }

    @Override
    public String getMessage() {
        return "Exception: user " + name + " already exists!";
    }
}