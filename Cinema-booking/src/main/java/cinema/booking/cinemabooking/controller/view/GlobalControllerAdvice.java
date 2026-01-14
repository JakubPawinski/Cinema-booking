package cinema.booking.cinemabooking.controller.view;

import cinema.booking.cinemabooking.exception.ResourceNotFoundException;
import cinema.booking.cinemabooking.exception.SeanceConflictException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Global Controller Advice.
 * Adds common attributes to the model for all View Controllers.
 */
@ControllerAdvice
@Slf4j
public class GlobalControllerAdvice {

    /**
     * Adds the current username to the model.
     * @return username if logged in, null otherwise
     */
    @ModelAttribute("currentUser")
    public String getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // Check if user is authenticated and not anonymous
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return auth.getName();
        }
        return null;
    }

    /**
     * Adds a boolean flag indicating if the user is logged in.
     * @return true if logged in, false otherwise
     */
    @ModelAttribute("isLoggedIn")
    public boolean isLoggedIn() {
        return getCurrentUser() != null;
    }

    /**
     * Adds a boolean flag indicating if the current user has ADMIN role.
     * @return true if user is admin, false otherwise
     */
    @ModelAttribute("isAdmin")
    public boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }

    /**
     * Handles validation exceptions for view controllers.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleConstraintViolation(ConstraintViolationException ex, Model model) {
        log.warn("View Validation Error: {}", ex.getMessage());
        String errors = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .reduce((e1, e2) -> e1 + ", " + e2)
                .orElse("Validation error occurred");
        model.addAttribute("errorMessage", errors);
        model.addAttribute("errorCode", "400");
        return "error/error-page";
    }

    /**
     * Handles exceptions related to seance conflicts.
     */
    @ExceptionHandler({IllegalStateException.class, SeanceConflictException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleSeanceConflict(RuntimeException ex, Model model) {
        log.warn("View Seance Conflict Error: {}", ex.getMessage());
        model.addAttribute("error", ex.getMessage());
        return "admin/seance-form";
    }

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
     * Handles access denied exceptions.
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAccessDenied(AccessDeniedException e, Model model) {
        log.warn("View 403 Error: {}", e.getMessage());

        model.addAttribute("errorMessage", e.getMessage());
        model.addAttribute("errorCode", "403");

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
}
