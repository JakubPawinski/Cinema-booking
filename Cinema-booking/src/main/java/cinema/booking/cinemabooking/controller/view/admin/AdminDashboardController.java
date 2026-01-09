package cinema.booking.cinemabooking.controller.view.admin;

import cinema.booking.cinemabooking.repository.MovieRepository;
import cinema.booking.cinemabooking.repository.ReservationRepository;
import cinema.booking.cinemabooking.repository.UserRepository;
import cinema.booking.cinemabooking.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * View controller for admin dashboard and reports
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminDashboardController {
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final ReportService reportService;

    /**
     * Display admin dashboard with counts of movies, users, and reservations
     * @param model Spring MVC model
     * @return admin dashboard view
     */
    @GetMapping
    public String dashboard(Model model) {
        long moviesCount = movieRepository.count();
        long usersCount = userRepository.count();
        long reservationsCount = reservationRepository.count();

        model.addAttribute("moviesCount", moviesCount);
        model.addAttribute("usersCount", usersCount);
        model.addAttribute("reservationsCount", reservationsCount);

        return "admin/dashboard";
    }

    /**
     * Display sales reports: by Movie and by Day
     * @param model Spring MVC model
     * @return admin reports view
     */
    @GetMapping("/reports")
    public String viewReports(Model model) {
        model.addAttribute("salesByMovie", reportService.getSalesReport());
        model.addAttribute("salesByDate", reportService.getDailySalesReport());

        return "admin/reports";
    }
}
