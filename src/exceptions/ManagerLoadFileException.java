package exceptions;

public class ManagerLoadFileException extends RuntimeException {
    public ManagerLoadFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
