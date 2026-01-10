package cinema.booking.cinemabooking.dto.report;

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
    private String movieTitle;

    /**
     * The total number of tickets sold for the movie.
     */
    @NotNull(message = "Tickets sold count is required")
    @PositiveOrZero(message = "Tickets sold cannot be negative")
    private Long ticketsSold;

    /**
     * The total revenue generated from ticket sales for the movie.
     */
    @NotNull(message = "Total revenue is required")
    @PositiveOrZero(message = "Total revenue cannot be negative")
    private Double totalRevenue;
}
