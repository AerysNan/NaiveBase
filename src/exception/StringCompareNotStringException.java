package exception;

public class StringCompareNotStringException extends RuntimeException {
    @Override
    public String getMessage() {
        return "Exception: String Value cannot be compared to Not-String value!";
    }
}