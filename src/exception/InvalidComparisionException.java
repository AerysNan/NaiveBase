package exception;

public class InvalidComparisionException extends RuntimeException {
    @Override
    public String getMessage() {
        return "Exception: comparision in condition expression is invalid!";
    }
}