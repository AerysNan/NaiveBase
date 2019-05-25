package exception;

public class NullCompareNotNullException extends RuntimeException {
    @Override
    public String getMessage() {
        return "Exception: Null Value cannot be compared to Not-Null value!";
    }
}