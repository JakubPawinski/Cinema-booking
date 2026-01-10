package cinema.booking.cinemabooking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class FileStorageException extends RuntimeException {
    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }
    public FileStorageException(String message) {
        super(message);
    }
}
