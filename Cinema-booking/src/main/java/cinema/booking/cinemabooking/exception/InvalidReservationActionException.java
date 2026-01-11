package cinema.booking.cinemabooking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when an invalid action is performed on a reservation.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidReservationActionException extends RuntimeException {
    public InvalidReservationActionException(String message) {
        super(message);
    }
}
