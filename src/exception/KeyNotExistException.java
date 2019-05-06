package exception;

public class KeyNotExistException extends RuntimeException{
    @Override
    public String getMessage() {
        return "Exception: key for deletion doesn't exist!";
    }
}
