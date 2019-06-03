package exception;

public class NoAuthorityException extends RuntimeException {

    @Override
    public String getMessage() {
        return "Exception: not enough authority to perform this action!";
    }
}