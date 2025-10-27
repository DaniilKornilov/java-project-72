package hexlet.code.exception;

public class DatabaseAccessException extends RuntimeException {
    public DatabaseAccessException(String message) {
        super(message);
    }
}
