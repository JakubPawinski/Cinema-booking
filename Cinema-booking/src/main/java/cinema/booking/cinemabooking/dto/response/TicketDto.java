package cinema.booking.cinemabooking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TicketDto {
    private Long ticketId;
    private Long seatId;
    private int seatNumber;
    private int rowNumber;
}
