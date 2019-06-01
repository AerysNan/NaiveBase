package exception;

public class ColumnNotFoundException extends RuntimeException {
    private String name;

    public ColumnNotFoundException(String name) {
        this.name = name;
    }

    @Override
    public String getMessage() {
        return "Exception: column " + name + " not found!";
    }
}