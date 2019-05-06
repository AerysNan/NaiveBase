package exception;

public class TableAlreadyExistsException extends RuntimeException {
    private String name;

    public TableAlreadyExistsException(String name) {
        this.name = name;
    }

    @Override
    public String getMessage() {
        return "Exception: table " + name + " already exists!";
    }
}
