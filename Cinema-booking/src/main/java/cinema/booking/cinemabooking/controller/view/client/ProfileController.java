package cinema.booking.cinemabooking.controller.view;

import cinema.booking.cinemabooking.dto.response.ReservationSummaryDto;
import cinema.booking.cinemabooking.enums.ReservationStatus;
import cinema.booking.cinemabooking.model.Reservation;
import cinema.booking.cinemabooking.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileViewController {
    private final ReservationService reservationService;

    @GetMapping
    public String userProfile(
            Model model,
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) ReservationStatus status // 1. Nowy parametr
    ) {
        String username = authentication.getName();

        // 2. Przekazujemy status do serwisu
        Page<ReservationSummaryDto> reservationPage = reservationService.getUserReservations(username, page, size, status);

        model.addAttribute("reservations", reservationPage);
        model.addAttribute("username", username);

        // 3. Kluczowe: Przekazujemy obecny status do widoku, żeby Thymeleaf wiedział co podświetlić
        model.addAttribute("currentStatus", status);

        return "profile/index"; // Upewnij się, że ścieżka do pliku html jest poprawna
    }

    @GetMapping("/reservation/{id}")
    public String reservationDetails(@PathVariable Long id, Model model, Authentication authentication) {
        String username = authentication.getName();

        try {
            Reservation reservation = reservationService.getReservationDetails(id, username);
            model.addAttribute("reservation", reservation);

            // Jeśli rezerwacja ma bilety, pobieramy ID seansu do mapy
            if (!reservation.getTickets().isEmpty()) {
                model.addAttribute("seanceId", reservation.getTickets().get(0).getSeance().getId());
                model.addAttribute("roomName", reservation.getTickets().get(0).getSeance().getCinemaRoom().getName());
            }

            return "profile/reservation-details";
        } catch (SecurityException e) {
            return "redirect:/profile?error=access_denied";
        }
    }


}
