package cinema.booking.cinemabooking.dto.response;

import cinema.booking.cinemabooking.enums.ReservationStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReservationSummaryDto {
    private Long id;
    private double totalPrice;
    private LocalDateTime expiresAt;
    private ReservationStatus status;
    private int ticketCount;
    private String movieTitle;
    private LocalDateTime seanceStartTime;
}
