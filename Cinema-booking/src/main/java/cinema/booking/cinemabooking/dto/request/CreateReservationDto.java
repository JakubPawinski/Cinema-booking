package cinema.booking.cinemabooking.dto.request;

import cinema.booking.cinemabooking.enums.TicketType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

/**
 * DTO for creating a new reservation.
 */
@Data
public class CreateReservationDto {

    /**
     * The ID of the seance for which the reservation is being made.
     */
    @NotNull(message = "Seance ID is required")
    private Long seanceId;

    /**
     * List of tickets requested in the reservation.
     */
    @NotEmpty(message = "At least one ticket must be requested")
    @Valid
    private List<TicketRequest> tickets;

    /**
     * Inner class representing a ticket request.
     */
    @Data
    public static class TicketRequest {
        /**
         * The ID of the seat being reserved.
         */
        @NotNull(message = "Seat ID is required")
        private Long seatId;

        /**
         * The type of the ticket (e.g., REGULAR, REDUCED).
         */
        @NotNull(message = "Ticket type is required")
        private TicketType ticketType;
    }
}
