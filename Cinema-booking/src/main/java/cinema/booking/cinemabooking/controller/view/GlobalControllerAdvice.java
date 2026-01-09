package cinema.booking.cinemabooking.controller.view;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Global Controller Advice.
 * Adds common attributes to the model for all View Controllers.
 */
@ControllerAdvice(basePackages = "cinema.booking.cinemabooking.controller.view")
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
}
