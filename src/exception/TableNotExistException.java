package exception;

public class TableNotExistException extends RuntimeException {
    private String name;

    public TableNotExistException(String name) {
        this.name = name;
    }

    @Override
    public String getMessage() {
        return "Exception: table " + name + " doesn't exist!";
    }
}
