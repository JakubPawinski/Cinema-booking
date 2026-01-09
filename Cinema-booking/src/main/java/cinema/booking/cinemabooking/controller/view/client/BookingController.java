package cinema.booking.cinemabooking.controller.view.client;

import cinema.booking.cinemabooking.dto.response.SeanceDto;
import cinema.booking.cinemabooking.model.Reservation;
import cinema.booking.cinemabooking.repository.ReservationRepository;
import cinema.booking.cinemabooking.repository.SeatRepository;
import cinema.booking.cinemabooking.service.SeanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * View Controller for Booking Process
 */
@Controller
@RequestMapping("/booking")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final SeanceService seanceService;
    private final ReservationRepository reservationRepository;

    /**
     * Display seat selection for a specific seance
     * @param seanceId ID of the seance
     * @param model Spring MVC model
     * @return seat selection view
     */
    @GetMapping("/seance/{seanceId}")
    public String selectSeats(@PathVariable Long seanceId, Model model) {
        log.info("View: Displaying seat selection for seance ID: {}", seanceId);

        SeanceDto seance = seanceService.getSeanceDetails(seanceId);
        model.addAttribute("seance", seance);

        return "booking/seat-selection";
    }

    /**
     * Display payment/confirmation page for a specific reservation
     * @param reservationId ID of the reservation
     * @param model Spring MVC model
     * @return payment view
     */
    @GetMapping("/payment/{reservationId}")
    public String payment(@PathVariable Long reservationId, Model model) {
        log.info("View: Displaying payment page for reservation ID: {}", reservationId);

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        model.addAttribute("reservation", reservation);
        return "booking/payment";
    }
}
