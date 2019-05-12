package exception;

public class NoRemovalAuthorityException extends RuntimeException {
    private String name;

    public NoRemovalAuthorityException(String name) {
        this.name = name;
    }

    @Override
    public String getMessage() {
        return "Exception: not enough authority to delete " + name + "!";
    }
}