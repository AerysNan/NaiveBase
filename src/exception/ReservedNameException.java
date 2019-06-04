package exception;

public class ReservedNameException extends RuntimeException {
    public String name;

    public ReservedNameException(String name) {
        this.name = name;
    }

    @Override
    public String getMessage() {
        return "Exception: " + name + " is a reserved name!";
    }
}