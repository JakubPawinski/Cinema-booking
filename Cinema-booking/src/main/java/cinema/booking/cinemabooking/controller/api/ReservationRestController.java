package cinema.booking.cinemabooking.controller.api;

import cinema.booking.cinemabooking.dto.CreateReservationDto;
import cinema.booking.cinemabooking.dto.ReservationSummaryDto;
import cinema.booking.cinemabooking.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationRestController {
    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationSummaryDto> createReservation(@Valid @RequestBody CreateReservationDto dto, Authentication authentication) {
        String username = authentication.getName();

        ReservationSummaryDto summary = reservationService.createReservation(dto, username);
        return ResponseEntity.ok(summary);
    }

    @PutMapping
    public ResponseEntity<Void> confirmReservation(@RequestParam Long reservationId, Authentication authentication) {
        String username = authentication.getName();

        reservationService.payForReservation(reservationId);
        return ResponseEntity.ok().build();
    }
}
