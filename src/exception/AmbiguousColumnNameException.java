package exception;

public class AmbiguousColumnNameException extends RuntimeException{
    private String name;

    public AmbiguousColumnNameException(String name) {
        this.name = name;
    }
    @Override
    public String getMessage() {
        return "Exception: ambiguous column name : " + this.name + "!";
    }
}