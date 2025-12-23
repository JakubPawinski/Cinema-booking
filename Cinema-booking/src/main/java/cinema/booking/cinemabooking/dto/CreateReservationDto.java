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

    @NotEmpty(message = "At least one seat must be selected")
    private List<Long> seatIds;

    @Data
    public static class TicketRequest {
        private Long seatId;
        private TicketType ticketType;
    }
}
