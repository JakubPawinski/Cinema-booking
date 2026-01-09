package cinema.booking.cinemabooking.controller.view.common;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * View controller for home page
 */
@Controller
@RequiredArgsConstructor
public class HomeController {

    /**
     * Display home page
     * @return home view
     */
    @GetMapping("/")
    public String homeView() {
        return "index";
    }
}
