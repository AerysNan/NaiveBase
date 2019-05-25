package exception;

public class NumberCompareNotNumberException extends RuntimeException {
    @Override
    public String getMessage() {
        return "Exception: Number Value cannot be compared to Not-Number value!";
    }
}