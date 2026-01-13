package cinema.booking.cinemabooking.controller.advice;

import cinema.booking.cinemabooking.dto.response.ErrorResponseDto;
import cinema.booking.cinemabooking.exception.ResourceNotFoundException;
import cinema.booking.cinemabooking.exception.SeatAlreadyOccupiedException;
import cinema.booking.cinemabooking.exception.UserAlreadyExistsException;
import cinema.booking.cinemabooking.exception.SeanceConflictException;
import cinema.booking.cinemabooking.exception.InvalidReservationActionException;
import cinema.booking.cinemabooking.exception.FileStorageException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Global exception handler for API controllers.
 */
@Slf4j
@RestControllerAdvice(basePackages = "cinema.booking.cinemabooking.controller.api")
public class ApiGlobalExceptionHandler {

    /**
     * Handle exceptions for resource not found (HTTP 404).
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        log.warn("API 404 Error: {}", ex.getMessage());
        return createErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    /**
     * Handle exceptions for invalid reservation actions (HTTP 400).
     */
    @ExceptionHandler(InvalidReservationActionException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidReservationAction(InvalidReservationActionException ex, HttpServletRequest request) {
        log.warn("API Invalid Reservation Action: {}", ex.getMessage());
        return createErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    /**
     * Handle exceptions for file storage issues (HTTP 500).
     */
    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<ErrorResponseDto> handleFileStorageException(FileStorageException ex, HttpServletRequest request) {
        log.error("API File Storage Error: {}", ex.getMessage());
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Could not store or process the file", request);
    }

    /**
     * Handle exceptions for conflict (HTTP 409).
     */
    @ExceptionHandler({UserAlreadyExistsException.class, SeatAlreadyOccupiedException.class, SeanceConflictException.class})
    public ResponseEntity<ErrorResponseDto> handleConflictExceptions(RuntimeException ex, HttpServletRequest request) {
        log.warn("API Conflict Error: {}", ex.getMessage());
        return createErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    /**
     * Handle exceptions for bad request (HTTP 400).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.warn("API Validation Error: {}", errors);
        return createErrorResponse(HttpStatus.BAD_REQUEST, "Validation failed: " + errors, request);
    }

    /**
     * Handle global exception (HTTP 500).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGlobalException(Exception ex, HttpServletRequest request) {
        log.error("API Critical Error", ex);
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected server error occurred.", request);
    }

    /**
     * Handle access denied exceptions (HTTP 403).
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDto> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        log.warn("API 403 Access Denied: {}", ex.getMessage());
        return createErrorResponse(HttpStatus.FORBIDDEN, "Access denied: You do not have permission to access this resource.", request);
    }

    /**
     * Handle method argument type mismatch exceptions (HTTP 400).
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        log.warn("API Type Mismatch Error: Parameter '{}' with value '{}' could not be converted to type {}",
                ex.getName(), ex.getValue(), ex.getRequiredType().getSimpleName());
        String message = String.format("Invalid value '%s' for parameter '%s'. Expected type: %s",
                ex.getValue(), ex.getName(), ex.getRequiredType().getSimpleName());
        return createErrorResponse(HttpStatus.BAD_REQUEST, message, request);
    }


    /**
     * Helper method to create error response.
     * @param status HTTP status to return
     * @param message Error message
     * @param request HTTP request
     */
    private ResponseEntity<ErrorResponseDto> createErrorResponse(HttpStatus status, String message, HttpServletRequest request) {
        ErrorResponseDto error = new ErrorResponseDto(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, status);
    }
}
