package exception;

public class DuplicateFieldException extends RuntimeException {
    private String name;

    public DuplicateFieldException(String name) {
        this.name = name;
    }

    @Override
    public String getMessage() {
        return "Exception: database " + name + " has duplicate fields!";
    }
}