package exception;

public class ValueFormatException extends RuntimeException {
    private String name;

    public ValueFormatException(String name) {
        this.name = name;
    }

    @Override
    public String getMessage() {
        return "Exception: value format mismatched on column " + name + "!";
    }
}
