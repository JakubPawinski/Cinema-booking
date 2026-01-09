package cinema.booking.cinemabooking.controller.api;

import cinema.booking.cinemabooking.dto.CreateReservationDto;
import cinema.booking.cinemabooking.dto.ReservationSummaryDto;
import cinema.booking.cinemabooking.enums.TicketType;
import cinema.booking.cinemabooking.model.Reservation;
import cinema.booking.cinemabooking.service.PdfTicketService;
import cinema.booking.cinemabooking.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationRestController {
    private final ReservationService reservationService;
    private final PdfTicketService pdfTicketService;

    @PostMapping
    public ResponseEntity<ReservationSummaryDto> createReservation(@Valid @RequestBody CreateReservationDto dto, Authentication authentication) {
        String username = authentication.getName();

        ReservationSummaryDto summary = reservationService.createReservation(dto, username);
        return ResponseEntity.ok(summary);
    }

    @PutMapping("/pay")
    public ResponseEntity<Void> confirmReservation(@RequestParam Long reservationId, Authentication authentication) {
        String username = authentication.getName();

        reservationService.payForReservation(reservationId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long reservationId, Authentication authentication) {
        String username = authentication.getName();

        reservationService.cancelReservation(reservationId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/tickets/{ticketId}")
    public ResponseEntity<ReservationSummaryDto> removeTicket(@PathVariable Long id, @PathVariable Long ticketId) {
        ReservationSummaryDto summary = reservationService.removeTicket(id, ticketId);
        return ResponseEntity.ok(summary);
    }

    @PatchMapping("/{id}/tickets/{ticketId}")
    public ResponseEntity<ReservationSummaryDto> updateTicketType(@PathVariable Long id,
                                                                  @PathVariable Long ticketId,
                                                                  @RequestParam TicketType type) {
        ReservationSummaryDto summary = reservationService.updateTicketType(id, ticketId, type);
        return ResponseEntity.ok(summary);
    }
    @PostMapping("/{id}/tickets")
    public ResponseEntity<ReservationSummaryDto> addTicket(@PathVariable Long id, @RequestParam Long seatId) {
        return ResponseEntity.ok(reservationService.addTicketToReservation(id, seatId));
    }

    @GetMapping(value = "/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> downloadPdf(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();

        // 1. Pobierz rezerwację (z weryfikacją użytkownika!)
        Reservation reservation = reservationService.getReservationDetails(id, username);

        // 2. Sprawdź czy opłacona (nie drukujemy biletów PENDING)
        if (reservation.getStatus() != cinema.booking.cinemabooking.enums.ReservationStatus.PAID) {
            throw new IllegalStateException("Nie można wygenerować biletu dla nieopłaconej rezerwacji.");
        }

        // 3. Generuj PDF
        java.io.ByteArrayInputStream bis = pdfTicketService.generateReservationPdf(reservation);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=bilety_" + reservation.getReservationCode() + ".pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }
}
