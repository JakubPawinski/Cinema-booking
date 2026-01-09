package cinema.booking.cinemabooking.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailySalesDto {
    private LocalDate date;
    private Long ticketsSold;
    private Double totalRevenue;
}
