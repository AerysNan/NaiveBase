package exception;

public class NotImplementedException extends RuntimeException {
    public String name;

    public NotImplementedException(String name) {
        this.name = name;
    }

    @Override
    public String getMessage() {
        return "Exception: feature " + name + " not implemented yet!";
    }
}
