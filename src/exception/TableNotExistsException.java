package exception;

public class TableNotExistsException extends RuntimeException {
    private String name;

    public TableNotExistsException(String name) {
        this.name = name;
    }

    @Override
    public String getMessage() {
        return "Exception: table " + name + " doesn't exist!";
    }
}