package exception;

public class RelationNotExistsException extends RuntimeException {
    private String name;

    public RelationNotExistsException(String name) {
        this.name = name;
    }

    @Override
    public String getMessage() {
        return "Exception: relation " + name + " doesn't exist!";
    }
}