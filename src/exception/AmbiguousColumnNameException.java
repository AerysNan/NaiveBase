package exception;

public class AmbiguousColumnNameException extends RuntimeException{
    @Override
    public String getMessage() {
        return "Exception: ambiguous column name!";
    }
}
