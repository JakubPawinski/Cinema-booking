package cinema.booking.cinemabooking.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateReservationDto {

    @NotNull(message = "Seance ID is required")
    private Long seanceId;

    @NotEmpty(message = "At least one seat must be selected")
    private Long[] seatIds;
}
