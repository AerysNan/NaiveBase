package exception;

public class ColumnNotFoundException extends  RuntimeException {
    String name;

    public ColumnNotFoundException(String name) {
        this.name = name;
    }

    @Override
    public String getMessage() {
        return "Exception: column " + name + " not found!";
    }
}
