package exception;

public class WrongUserOrPasswordException extends RuntimeException {
    @Override
    public String getMessage() {
        return "Exception: wrong username or password!";
    }
}
