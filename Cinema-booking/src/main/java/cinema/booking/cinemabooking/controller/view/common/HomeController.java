package cinema.booking.cinemabooking.controller.view;

import cinema.booking.cinemabooking.service.SeanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeViewController {
    private final SeanceService seanceService;

    @GetMapping("/")
    public String homeView() {
        return "index";
    }
}
