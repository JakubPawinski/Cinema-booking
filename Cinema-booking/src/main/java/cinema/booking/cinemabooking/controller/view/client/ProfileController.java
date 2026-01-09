package cinema.booking.cinemabooking.controller.view.client;

import cinema.booking.cinemabooking.dto.response.ReservationSummaryDto;
import cinema.booking.cinemabooking.enums.ReservationStatus;
import cinema.booking.cinemabooking.model.Reservation;
import cinema.booking.cinemabooking.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * View controller for user profile and reservations
 */
@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
@Slf4j
public class ProfileController {
    private final ReservationService reservationService;

    /**
     * Display user profile with reservations
     * @param model Spring MVC model
     * @param authentication authenticated user
     * @param page pagination page number
     * @param size pagination page size
     * @param status (optional) filter by reservation status
     * @return profile view
     */
    @GetMapping
    public String userProfile(
            Model model,
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) ReservationStatus status
    ) {
        String username = authentication.getName();

        if (status != null) {
            log.info("View: Fetching reservations for user: {} with status: {}, page: {}, size: {}", username, status, page, size);
        }

        Page<ReservationSummaryDto> reservationPage = reservationService.getUserReservations(username, page, size, status);

        model.addAttribute("reservations", reservationPage);
        model.addAttribute("currentStatus", status);

        return "profile/index";
    }

    /**
     * Display details of a specific reservation
     * @param id reservation ID
     * @param model Spring MVC model
     * @param authentication authenticated user
     * @return reservation details view
     */
    @GetMapping("/reservation/{id}")
    public String reservationDetails(@PathVariable Long id, Model model, Authentication authentication) {
        String username = authentication.getName();

        try {
            Reservation reservation = reservationService.getReservationDetails(id, username);
            model.addAttribute("reservation", reservation);

            log.info("View: Fetching details for reservation ID: {} by user: {}", id, username);

            return "profile/reservation-details";
        } catch (SecurityException e) {
            log.warn("View: Access denied for reservation ID: {} by user: {}", id, username);
            return "redirect:/profile?error=access_denied";
        } catch (Exception e) {
            log.error("View: Error fetching reservation ID: {} by user: {}: {}", id, username, e.getMessage());
            return "redirect:/profile?error=not_found";
        }
    }


}
