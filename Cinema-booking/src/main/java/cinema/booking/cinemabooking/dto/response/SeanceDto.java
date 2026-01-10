package cinema.booking.cinemabooking.dto.response;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * DTO representing a seance (movie screening).
 */
@Data
@Builder
public class SeanceDto {

    @NotNull(message = "Seance ID cannot be null")
    private Long id;

    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    private LocalDateTime endTime;

    @PositiveOrZero(message = "Regular ticket price must be positive or zero")
    private double regularTicketPrice;

    @PositiveOrZero(message = "Reduced ticket price must be positive or zero")
    private double reducedTicketPrice;

    @NotBlank(message = "Room name is required")
    private String roomName;

    @NotNull(message = "Movie ID is required")
    private Long movieId;

    @NotBlank(message = "Movie title is required")
    private String movieTitle;
}
