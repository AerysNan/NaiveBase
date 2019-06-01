package exception;

public class DatabaseNotExistsException extends RuntimeException {
    private String name;
    public DatabaseNotExistsException(String name){
        this.name = name;
    }

    @Override
    public String getMessage() {
        return "Exception: database " + name + " doesn't exist!";
    }
}