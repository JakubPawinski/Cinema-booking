package cinema.booking.cinemabooking.dto.response;

import cinema.booking.cinemabooking.enums.ReservationStatus;
import lombok.Builder;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO representing a summary of a reservation.
 */
@Data
@Builder
public class ReservationSummaryDto {
    @NotNull(message = "Reservation ID is required")
    private Long id;

    @PositiveOrZero(message = "Total price cannot be negative")
    private double totalPrice;

    @NotNull(message = "Expiration time is required")
    private LocalDateTime expiresAt;

    @NotNull(message = "Reservation status is required")
    private ReservationStatus status;

    @Min(value = 1, message = "Ticket count must be at least 1")
    private int ticketCount;

    @NotBlank(message = "Movie title is required")
    private String movieTitle;

    @NotNull(message = "Seance start time is required")
    private LocalDateTime seanceStartTime;
}
