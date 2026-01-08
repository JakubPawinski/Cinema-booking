package cinema.booking.cinemabooking.dto;

import cinema.booking.cinemabooking.enums.TicketType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class CreateReservationDto {

    @NotNull(message = "Seance ID is required")
    private Long seanceId;

    @NotEmpty(message = "At least one ticket must be requested")
    private List<TicketRequest> tickets;

    @Data
    public static class TicketRequest {
        @NotNull(message = "Seat ID is required")
        private Long seatId;

        @NotNull(message = "Ticket type is required")
        private TicketType ticketType;
    }
}
