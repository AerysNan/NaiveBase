package exception;

public class DuplicateKeyException extends RuntimeException {
    String name;

    public DuplicateKeyException(String name) {
        this.name = name;
    }

    @Override
    public String getMessage() {
        return "Exception: database " + name + " has duplicate keys!";
    }
}