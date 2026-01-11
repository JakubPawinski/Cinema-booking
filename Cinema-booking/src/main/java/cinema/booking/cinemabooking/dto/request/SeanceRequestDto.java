package cinema.booking.cinemabooking.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * DTO representing a request to create or update a seance.
 */
@Data
public class SeanceRequestDto {
    /**
     * The ID of the movie for the seance.
     */
    @NotNull(message = "Movie ID is required")
    @Schema(description = "The ID of the movie for the seance", example = "1")
    private Long movieId;

    /**
     * The ID of the room where the seance will take place.
     */
    @NotNull(message = "Room ID is required")
    @Schema(description = "The ID of the room where the seance will take place", example = "2")
    private Long roomId;

    /**
     * The start time of the seance.
     */
    @NotNull(message = "Start time is required")
    @Future(message = "Start time must be in the future")
    @Schema(description = "The start time of the seance in ISO 8601 format", example = "2024-12-31T18:30:00")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startTime;

    /**
     * The price for regular tickets.
     */
    @NotNull(message = "Regular ticket price is required")
    @PositiveOrZero(message = "Regular ticket price must be zero or positive")
    @Schema(description = "The price for regular tickets", example = "12.50")
    private double regularTicketPrice;

    /**
     * The price for reduced tickets.
     */
    @NotNull(message = "Reduced ticket price is required")
    @PositiveOrZero(message = "Reduced ticket price must be zero or positive")
    @Schema(description = "The price for reduced tickets", example = "8.00")
    private double reducedTicketPrice;
}
