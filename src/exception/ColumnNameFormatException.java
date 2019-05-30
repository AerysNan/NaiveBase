package exception;

public class ColumnNameFormatException extends RuntimeException{
    @Override
    public String getMessage() {
        return "Exception: invalid column name format!";
    }
}
