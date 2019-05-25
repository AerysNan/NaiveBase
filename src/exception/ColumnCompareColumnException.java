package exception;

public class ColumnCompareColumnException extends RuntimeException {
    @Override
    public String getMessage() {
        return "Exception: Column cannot be compared with each other directly!";
    }
}