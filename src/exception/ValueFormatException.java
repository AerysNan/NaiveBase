package exception;

public class ValueFormatException extends RuntimeException {
    @Override
    public String getMessage() {
        return "Exception: value format mismatched!";
    }
}