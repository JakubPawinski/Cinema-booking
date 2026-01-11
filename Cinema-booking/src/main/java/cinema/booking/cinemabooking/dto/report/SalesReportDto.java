package cinema.booking.cinemabooking.dto.report;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing sales report data for a movie.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesReportDto {

    /**
     * The title of the movie.
     */
    @NotBlank(message = "Movie title is required")
    @Schema(description = "The title of the movie", example = "Inception")
    private String movieTitle;

    /**
     * The total number of tickets sold for the movie.
     */
    @NotNull(message = "Tickets sold count is required")
    @PositiveOrZero(message = "Tickets sold cannot be negative")
    @Schema(description = "The total number of tickets sold for the movie", example = "300")
    private Long ticketsSold;

    /**
     * The total revenue generated from ticket sales for the movie.
     */
    @NotNull(message = "Total revenue is required")
    @PositiveOrZero(message = "Total revenue cannot be negative")
    @Schema(description = "The total revenue generated from ticket sales for the movie", example = "4500.00")
    private Double totalRevenue;
}
