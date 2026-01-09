package cinema.booking.cinemabooking.controller.view;

import cinema.booking.cinemabooking.repository.MovieRepository;
import cinema.booking.cinemabooking.repository.ReservationRepository;
import cinema.booking.cinemabooking.repository.UserRepository;
import cinema.booking.cinemabooking.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminViewController {
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final ReportService reportService;

    @GetMapping
    public String adminDashboard(Model model) {
        long moviesCount = movieRepository.count();
        long usersCount = userRepository.count();
        long reservationsCount = reservationRepository.count();

        model.addAttribute("moviesCount", moviesCount);
        model.addAttribute("usersCount", usersCount);
        model.addAttribute("reservationsCount", reservationsCount);

        return "admin/dashboard";
    }

    @GetMapping("/reports")
    public String viewReports(Model model) {
        // Pobieramy oba raporty: per Film i per Dzień
        model.addAttribute("salesByMovie", reportService.getSalesReport());
        model.addAttribute("salesByDate", reportService.getDailySalesReport());

        return "admin/reports";
    }
}
