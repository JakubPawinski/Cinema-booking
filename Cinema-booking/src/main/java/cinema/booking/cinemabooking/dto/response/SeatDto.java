package cinema.booking.cinemabooking.dto.response;

import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO representing a seat in the cinema.
 */
@Data
@Builder
public class SeatDto {

    @NotNull(message = "Seat ID cannot be null")
    private Long id;

    @Min(value = 1, message = "Row number must be at least 1")
    private int rowNumber;

    @Min(value = 1, message = "Seat number must be at least 1")
    private int seatNumber;

    @NotNull(message = "Occupancy status must be defined")
    private boolean isOccupied;
}
