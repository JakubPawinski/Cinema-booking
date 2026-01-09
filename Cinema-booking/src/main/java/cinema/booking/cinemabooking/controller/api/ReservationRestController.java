package cinema.booking.cinemabooking.controller.api;

import cinema.booking.cinemabooking.dto.request.CreateReservationDto;
import cinema.booking.cinemabooking.dto.response.ReservationSummaryDto;
import cinema.booking.cinemabooking.enums.TicketType;
import cinema.booking.cinemabooking.service.PdfTicketService;
import cinema.booking.cinemabooking.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;

/**
 * REST API controller for managing reservations
 */
@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
@Slf4j
public class ReservationRestController {
    private final ReservationService reservationService;
    private final PdfTicketService pdfTicketService;

    /**
     * Create a new reservation
     * @param dto reservation data
     * @param authentication authenticated user
     * @return summary of the created reservation
     */
    @PostMapping
    public ResponseEntity<ReservationSummaryDto> createReservation(@Valid @RequestBody CreateReservationDto dto, Authentication authentication) {
        log.info("API: Creating reservation for seanceId: {} by user: {}", dto.getSeanceId(), authentication.getName());

        String username = authentication.getName();
        ReservationSummaryDto summary = reservationService.createReservation(dto, username);
        return ResponseEntity.ok(summary);
    }

    /**
     * Confirm and pay for a reservation
     * @param reservationId ID of the reservation to confirm
     * @param authentication authenticated user
     * @return HTTP 200 OK if successful
     */
    @PutMapping("/pay")
    public ResponseEntity<Void> confirmReservation(@RequestParam Long reservationId, Authentication authentication) {
        log.info("API: Confirming and paying for reservationId: {} by user: {}", reservationId, authentication.getName());

        String username = authentication.getName();
        reservationService.payForReservation(reservationId);
        return ResponseEntity.ok().build();
    }

    /**
     * Cancel a reservation
     * @param reservationId ID of the reservation to cancel
     * @param authentication authenticated user
     * @return HTTP 204 No Content if successful
     */
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long reservationId, Authentication authentication) {
        log.info("API: Cancelling reservationId: {} by user: {}", reservationId, authentication.getName());

        String username = authentication.getName();
        reservationService.cancelReservation(reservationId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Remove a ticket from a reservation
     * @param id reservation ID
     * @param ticketId ticket ID to remove
     * @return updated reservation summary
     */
    @DeleteMapping("/{id}/tickets/{ticketId}")
    public ResponseEntity<ReservationSummaryDto> removeTicket(@PathVariable Long id, @PathVariable Long ticketId) {
        log.info("API: Removing ticketId: {} from reservationId: {}", ticketId, id);

        ReservationSummaryDto summary = reservationService.removeTicket(id, ticketId);
        return ResponseEntity.ok(summary);
    }

    /**
     * Update the type of a ticket in a reservation
     * @param id reservation ID
     * @param ticketId ticket ID to update
     * @param type new ticket type
     * @return updated reservation summary
     */
    @PatchMapping("/{id}/tickets/{ticketId}")
    public ResponseEntity<ReservationSummaryDto> updateTicketType(@PathVariable Long id,
                                                                  @PathVariable Long ticketId,
                                                                  @RequestParam TicketType type) {
        log.info("API: Updating ticketId: {} in reservationId: {} to type: {}", ticketId, id, type);

        ReservationSummaryDto summary = reservationService.updateTicketType(id, ticketId, type);
        return ResponseEntity.ok(summary);
    }

    /**
     * Add a ticket to a reservation
     * @param id reservation ID
     * @param seatId seat ID for the new ticket
     * @return updated reservation summary
     */
    @PostMapping("/{id}/tickets")
    public ResponseEntity<ReservationSummaryDto> addTicket(@PathVariable Long id, @RequestParam Long seatId) {
        log.info("API: Adding ticket for seatId: {} to reservationId: {}", seatId, id);

        return ResponseEntity.ok(reservationService.addTicketToReservation(id, seatId));
    }

    /**
     * Download reservation tickets as a PDF
     * @param id reservation ID
     * @param authentication authenticated user
     * @return PDF file containing the tickets
     */
    @GetMapping(value = "/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> downloadPdf(@PathVariable Long id, Authentication authentication) {
        log.info("API: Downloading PDF tickets for reservationId: {} by user: {}", id, authentication.getName());

        String username = authentication.getName();

        // Generate PDF
        ByteArrayInputStream bis = reservationService.generatePdfForReservation(id, username);

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=bilety_" + id + ".pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }
}
