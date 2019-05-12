package exception;

public class MultiplePrimaryKeyException extends RuntimeException {
    String name;

    public MultiplePrimaryKeyException(String name) {
        this.name = name;
    }

    @Override
    public String getMessage() {
        return "Exception: database " + name + "has multiple primary keys, which is not allowed in NaiveBase!";
    }
}
