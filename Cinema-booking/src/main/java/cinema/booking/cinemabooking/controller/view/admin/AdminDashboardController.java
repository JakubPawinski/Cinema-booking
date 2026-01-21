package cinema.booking.cinemabooking.controller.view.admin;

import cinema.booking.cinemabooking.service.MovieService;
import cinema.booking.cinemabooking.service.ReportService;
import cinema.booking.cinemabooking.service.ReservationService;
import cinema.booking.cinemabooking.service.UserService;
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
    private final ReportService reportService;
    private final UserService userService;
    private final MovieService movieService;
    private final ReservationService reservationService;

    /**
     * Display admin dashboard with counts of movies, users, and reservations
     * @param model Spring MVC model
     * @return admin dashboard view
     */
    @GetMapping
    public String dashboard(Model model) {
        long moviesCount = movieService.getMoviesCount();
        long usersCount = userService.getUserCount();
        long reservationsCount = reservationService.getTotalReservationCount();

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
