package cinema.booking.cinemabooking.mapper;

import cinema.booking.cinemabooking.dto.response.SeatDto;
import cinema.booking.cinemabooking.model.Seat;
import org.springframework.stereotype.Component;

/**
 * Mapper class for Seat entity and DTOs.
 */
@Component
public class SeatMapper {

    /**
     * Converts Seat entity to SeatDto with occupancy status.
     *
     * @param seat       the Seat entity
     * @param isOccupied boolean indicating if the seat is occupied
     * @return the SeatDto
     */
    public SeatDto toDto(Seat seat, boolean isOccupied) {
        return SeatDto.builder()
                .id(seat.getId())
                .rowNumber(seat.getRowNumber())
                .seatNumber(seat.getSeatNumber())
                .isOccupied(isOccupied)
                .build();
    }
}
