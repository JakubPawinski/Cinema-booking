package cinema.booking.cinemabooking.dao;

import cinema.booking.cinemabooking.dto.report.DailySalesDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import cinema.booking.cinemabooking.dto.report.SalesReportDto;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Data Access Object for sales reports and reservation management
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class SalesDao {
    private final JdbcTemplate jdbcTemplate;

    /**
     * Fetches sales report data including movie titles, ticket counts, and total revenue.
     * @return list of SalesReportDto
     */
    public List<SalesReportDto> fetchSalesReport() {
        log.info("Fetching sales report");

        String sql = """
            SELECT\s
                m.title as movie_title,\s
                COUNT(t.id) as tickets_count,\s
                COALESCE(SUM(t.price), 0) as total_revenue
            FROM movie m
            LEFT JOIN seance s ON s.movie_id = m.id
            LEFT JOIN ticket t ON t.seance_id = s.id
            LEFT JOIN reservation r ON t.reservation_id = r.id
            WHERE r.status = 'PAID' OR r.status IS NULL
            GROUP BY m.id, m.title
            ORDER BY total_revenue DESC
       \s""";

        return jdbcTemplate.query(sql, new SalesReportRowMapper());
    }


    /**
     * Fetches daily sales data including date, tickets sold, and total revenue.
     * @return list of DailySalesDto
     */
    public List<DailySalesDto> fetchDailySales() {
        log.info("Fetching daily sales report");

        String sql = """
            SELECT 
                CAST(r.created_at AS DATE) as sale_date,
                COUNT(t.id) as tickets_sold,
                COALESCE(SUM(t.price), 0) as total_revenue
            FROM reservation r
            JOIN ticket t ON t.reservation_id = r.id
            WHERE r.status = 'PAID'
            GROUP BY CAST(r.created_at AS DATE)
            ORDER BY sale_date DESC
        """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> new DailySalesDto(
                rs.getDate("sale_date").toLocalDate(),
                rs.getLong("tickets_sold"),
                rs.getDouble("total_revenue")
        ));
    }

    /**
     * Internal RowMapper for SalesReportDto
     */
    private static class SalesReportRowMapper implements RowMapper<SalesReportDto> {

        @Override
        public SalesReportDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new SalesReportDto(
                    rs.getString("movie_title"),
                    rs.getLong("tickets_count"),
                    rs.getDouble("total_revenue")
            );
        }
    }
}
