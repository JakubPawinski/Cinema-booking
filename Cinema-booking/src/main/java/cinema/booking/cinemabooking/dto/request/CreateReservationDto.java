package cinema.booking.cinemabooking.dto.request;

import cinema.booking.cinemabooking.enums.TicketType;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "The ID of the seance for which the reservation is being made", example = "1")
    private Long seanceId;

    /**
     * List of tickets requested in the reservation.
     */
    @NotEmpty(message = "At least one ticket must be requested")
    @Schema(description = "List of tickets requested in the reservation")
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
        @Schema(description = "The ID of the seat being reserved", example = "10")
        private Long seatId;

        /**
         * The type of the ticket (e.g., REGULAR, REDUCED).
         */
        @NotNull(message = "Ticket type is required")
        @Schema(description = "The type of the ticket (e.g., REGULAR, REDUCED)", example = "REGULAR")
        private TicketType ticketType;
    }
}
