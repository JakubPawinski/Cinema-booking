package cinema.booking.cinemabooking.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SeanceDto {
    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double ticketPrice;
    private String roomName;
    private Long movieId;
}
