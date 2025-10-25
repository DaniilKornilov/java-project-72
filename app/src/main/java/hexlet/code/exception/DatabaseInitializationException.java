package hexlet.code.exception;

public final class DatabaseInitializationException extends RuntimeException {
    public DatabaseInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
