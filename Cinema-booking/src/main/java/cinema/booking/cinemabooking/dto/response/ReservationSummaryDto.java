package cinema.booking.cinemabooking.dto.response;

import cinema.booking.cinemabooking.enums.ReservationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO representing a summary of a reservation.
 */
@Data
@Builder
public class ReservationSummaryDto {
    @NotNull(message = "Reservation ID is required")
    @Schema(description = "Unique identifier of the reservation", example = "1001")
    private Long id;

    @PositiveOrZero(message = "Total price cannot be negative")
    @Schema(description = "Total price of the reservation", example = "45.00")
    private double totalPrice;

    @NotNull(message = "Expiration time is required")
    @Schema(description = "Expiration time of the reservation in ISO 8601 format", example = "2024-12-31T17:00:00")
    private LocalDateTime expiresAt;

    @NotNull(message = "Reservation status is required")
    @Schema(description = "Status of the reservation", example = "CONFIRMED")
    private ReservationStatus status;

    @Min(value = 1, message = "Ticket count must be at least 1")
    @Schema(description = "Number of tickets in the reservation", example = "3")
    private int ticketCount;

    @NotBlank(message = "Movie title is required")
    @Schema(description = "Title of the movie for which the reservation is made", example = "Inception")
    private String movieTitle;

    @NotNull(message = "Seance start time is required")
    @Schema(description = "Start time of the seance in ISO 8601 format", example = "2024-12-31T18:30:00")
    private LocalDateTime seanceStartTime;

    @Schema(description = "List of tickets associated with the reservation")
    private List<TicketDto> tickets;
}
