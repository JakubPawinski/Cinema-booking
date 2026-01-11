package cinema.booking.cinemabooking.controller.view.common;

import cinema.booking.cinemabooking.dto.request.UserDto;
import cinema.booking.cinemabooking.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * View controller for authentication: login and registration
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final UserService userService;

    /**
     * Display login page
     * @return login view
     */
    @GetMapping("/login")
    public String loginView() {
        return "auth/login";
    }

    /**
     * Display registration page
     * @param model Spring MVC model
     * @return registration view
     */
    @GetMapping("/register")
    public String registerView(Model model) {
        model.addAttribute("user", new UserDto());
        return "auth/register";
    }

    /**
     * Handle user registration
     * @param model Spring MVC model
     * @param dto user registration data
     * @param redirectAttributes attributes for redirect
     * @return redirect to login on success, registration view on failure
     */
    @PostMapping("/register")
    public String registerUser(Model model, @Valid @ModelAttribute("user") UserDto dto, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            log.warn("Auth: Registration validation failed for user: {}", dto.getUsername());
            return "auth/register";
        }

        try {
            log.info("Auth: Registering user with username: {}", dto.getUsername());

            userService.register(dto);

            log.info("Auth: User registered successfully: {}", dto.getUsername());
            redirectAttributes.addAttribute("registered", true);
            return "redirect:/login";
        } catch (RuntimeException e) {
            log.warn("Auth: Registration failed for user: {}: {}", dto.getUsername(), e.getMessage());

            result.rejectValue("username", "error.user", e.getMessage());
            return "auth/register";
        }
    }
}
