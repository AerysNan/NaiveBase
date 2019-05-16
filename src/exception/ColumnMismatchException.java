package exception;

public class ColumnMismatchException extends RuntimeException {
    @Override
    public String getMessage() {
        return "Exception: inserted row doesn't match given columns!";
    }
}
