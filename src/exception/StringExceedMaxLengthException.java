package exception;

public class StringExceedMaxLengthException extends RuntimeException {
    public String name;

    public StringExceedMaxLengthException(String name) {
        this.name = name;
    }

    @Override
    public String getMessage() {
        return "Exception: string inserted on column " + name + "exceeds max length!";
    }
}
