package hexlet.code.exception;

public final class UrlAlreadyExistsException extends RuntimeException {
    public UrlAlreadyExistsException(String message) {
        super(message);
    }
}
