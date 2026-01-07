package cinema.booking.cinemabooking.controller.view;

import cinema.booking.cinemabooking.dto.UserDto;
import cinema.booking.cinemabooking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthViewController {
    private final UserService userService;

    @GetMapping("/login")
    public String loginView() {
        return "login";
    }

    @GetMapping("/register")
    public String registerView() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(UserDto dto) {
        userService.register(dto);
        return "redirect:/login";
    }

    // TODO: Implement /profile view controller methods
}
