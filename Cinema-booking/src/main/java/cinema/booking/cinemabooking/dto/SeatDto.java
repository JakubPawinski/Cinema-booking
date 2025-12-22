package cinema.booking.cinemabooking.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SeatDto {
    private Long id;
    private int rowNumber;
    private int seatNumber;
    private boolean isOccupied;
}
