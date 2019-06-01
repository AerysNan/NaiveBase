package exception;

public class ColumnMismatchException extends RuntimeException {
    @Override
    public String getMessage() {
        return "Exception: column type doesn't match!";
    }
}