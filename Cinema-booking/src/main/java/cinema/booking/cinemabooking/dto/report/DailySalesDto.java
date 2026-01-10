package cinema.booking.cinemabooking.dto.report;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO representing daily sales data.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailySalesDto {

    /**
     * The date for which the sales data is reported.
     */
    @NotNull(message = "Date is required")
    @PastOrPresent(message = "Date cannot be in the future")
    private LocalDate date;

    /**
     * The total number of tickets sold on the specified date.
     */
    @NotNull(message = "Tickets sold count is required")
    @PositiveOrZero(message = "Tickets sold cannot be negative")
    private Long ticketsSold;

    /**
     * The total revenue generated from ticket sales on the specified date.
     */
    @NotNull(message = "Total revenue is required")
    @PositiveOrZero(message = "Total revenue cannot be negative")
    private Double totalRevenue;
}
