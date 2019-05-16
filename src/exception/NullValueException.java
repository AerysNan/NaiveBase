package exception;

public class NullValueException extends RuntimeException {
    public String name;

    public NullValueException(String name) {
        this.name = name;
    }

    @Override
    public String getMessage() {
        return "Exception: values on column " + name + " cannot be null!";
    }
}