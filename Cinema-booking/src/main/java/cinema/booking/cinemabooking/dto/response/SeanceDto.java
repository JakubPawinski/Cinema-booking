package cinema.booking.cinemabooking.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "Unique identifier of the seance", example = "1")
    private Long id;

    @NotNull(message = "Start time is required")
    @Schema(description = "Start time of the seance in ISO 8601 format", example = "2024-12-31T18:30:00")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    @Schema(description = "End time of the seance in ISO 8601 format", example = "2024-12-31T20:30:00")
    private LocalDateTime endTime;

    @PositiveOrZero(message = "Regular ticket price must be positive or zero")
    @Schema(description = "Price for regular tickets", example = "12.50")
    private double regularTicketPrice;

    @PositiveOrZero(message = "Reduced ticket price must be positive or zero")
    @Schema(description = "Price for reduced tickets", example = "8.00")
    private double reducedTicketPrice;

    @NotBlank(message = "Room name is required")
    @Schema(description = "Name of the room where the seance takes place", example = "Room A")
    private String roomName;

    @NotNull(message = "Movie ID is required")
    @Schema(description = "Unique identifier of the movie", example = "1")
    private Long movieId;

    @NotBlank(message = "Movie title is required")
    @Schema(description = "Title of the movie", example = "Inception")
    private String movieTitle;
}
