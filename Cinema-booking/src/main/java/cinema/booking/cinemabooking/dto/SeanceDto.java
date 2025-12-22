package cinema.booking.cinemabooking.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SeanceDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private double price;
    private String roomName;
    private Long movieId;
}
