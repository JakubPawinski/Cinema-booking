package cinema.booking.cinemabooking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesReportDto {
    private String movieTitle;
    private Long ticketsSold;
    private Double totalRevenue;
}
