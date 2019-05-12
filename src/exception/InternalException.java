package exception;

public class InternalException extends RuntimeException{
    String message;
    public InternalException(String message){
        this.message = message;
    }

    @Override
    public String getMessage() {
        return "Exception: an internal error occurred! Error message: " + message;
    }
}
