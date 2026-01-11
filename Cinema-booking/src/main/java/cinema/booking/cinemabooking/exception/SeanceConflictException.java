package cinema.booking.cinemabooking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when there is a conflict with an existing seance.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class SeanceConflictException extends RuntimeException {
    public SeanceConflictException(String message) {
        super(message);
    }
}
