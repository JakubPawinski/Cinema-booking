package cinema.booking.cinemabooking.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class SeanceRequestDto {
    private Long movieId;
    private Long roomId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startTime;

    private double regularTicketPrice;
    private double reducedTicketPrice;
}
