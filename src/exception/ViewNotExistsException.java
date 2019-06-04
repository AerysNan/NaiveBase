package exception;

public class ViewNotExistsException extends RuntimeException {
    private String name;

    public ViewNotExistsException(String name) {
        this.name = name;
    }

    @Override
    public String getMessage() {
        return "Exception: view " + name + " doesn't exist!";
    }
}