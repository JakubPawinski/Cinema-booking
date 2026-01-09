package cinema.booking.cinemabooking.controller.view;

import cinema.booking.cinemabooking.dto.SeanceDto;
import cinema.booking.cinemabooking.model.Seat;
import cinema.booking.cinemabooking.repository.ReservationRepository;
import cinema.booking.cinemabooking.repository.SeatRepository;
import cinema.booking.cinemabooking.service.SeanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/booking")
@RequiredArgsConstructor
public class BookingViewController {
    private final SeanceService seanceService;
    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;

    // KROK 1: Wybór Miejsc (Siatka)
    @GetMapping("/seance/{seanceId}")
    public String selectSeats(@PathVariable Long seanceId, Model model) {
        SeanceDto seance = seanceService.getSeanceDetails(seanceId);
        model.addAttribute("seance", seance);
        return "booking/seat-selection";
    }

    // KROK 2: Podsumowanie i Wybór Biletów (POST z formularza z kroku 1)
    @PostMapping("/summary")
    public String summary(@RequestParam Long seanceId, @RequestParam("selectedSeatIds") List<Long> seatIds, Model model) {
        SeanceDto seance = seanceService.getSeanceDetails(seanceId);
        List<Seat> seats = seatRepository.findAllById(seatIds); // Pobieramy detale wybranych miejsc

        model.addAttribute("seance", seance);
        model.addAttribute("seats", seats);
        return "booking/summary";
    }

    // KROK 3: Potwierdzenie / Płatność (Po utworzeniu rezerwacji przez API)
    @GetMapping("/payment/{reservationId}")
    public String payment(@PathVariable Long reservationId, Model model) {
        var reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Rezerwacja nie znaleziona"));

        model.addAttribute("reservation", reservation);
        return "booking/payment";
    }
}
