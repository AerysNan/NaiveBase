package exception;

public class DatabaseAlreadyExistsException extends RuntimeException {
    private String name;
    public DatabaseAlreadyExistsException(String name){
        this.name = name;
    }

    @Override
    public String getMessage() {
        return "Exception: database " + name + " already exists!";
    }
}