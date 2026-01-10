package cinema.booking.cinemabooking.controller.advice;

import cinema.booking.cinemabooking.exception.InvalidReservationActionException;
import cinema.booking.cinemabooking.exception.ResourceNotFoundException;
import cinema.booking.cinemabooking.exception.SeatAlreadyOccupiedException;
import cinema.booking.cinemabooking.exception.SeanceConflictException;
import cinema.booking.cinemabooking.exception.UserAlreadyExistsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Global exception handler for view controllers.
 */
@Slf4j
@ControllerAdvice
public class ViewGlobalExceptionHandler {

    /**
     * Handles exceptions related to invalid reservation actions.
     */
    @ExceptionHandler({ResourceNotFoundException.class, NoResourceFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleResourceNotFound(Exception ex, Model model) {
        log.warn("View 404 Error: {}", ex.getMessage());
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("errorCode", "404");
        return "error/error-page";
    }

    /**
     * Handles exceptions related to internal server errors.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGlobalException(Exception ex, Model model) {
        log.error("View Critical Error", ex);
        model.addAttribute("errorMessage", "An unexpected error occurred. Please try again later.");
        model.addAttribute("errorCode", "500");
        return "error/error-page";
    }

    /**
     * Handles access denied exceptions.
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAccessDenied(AccessDeniedException e, Model model) {
        System.err.println("Error 403: " + e.getMessage());

        model.addAttribute("errorMessage", e.getMessage());
        model.addAttribute("errorCode", "403");

        return "error/error-page";
    }
}
