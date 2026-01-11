package cinema.booking.cinemabooking.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "Unique identifier of the seat", example = "1")
    private Long id;

    @Min(value = 1, message = "Row number must be at least 1")
    @Schema(description = "Row number of the seat", example = "5")
    private int rowNumber;

    @Min(value = 1, message = "Seat number must be at least 1")
    @Schema(description = "Seat number within the row", example = "12")
    private int seatNumber;

    @NotNull(message = "Occupancy status must be defined")
    @Schema(description = "Indicates whether the seat is occupied", example = "false")
    private boolean isOccupied;
}
