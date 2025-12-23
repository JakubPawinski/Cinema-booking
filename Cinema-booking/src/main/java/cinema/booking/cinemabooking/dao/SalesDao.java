package cinema.booking.cinemabooking.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import cinema.booking.cinemabooking.dto.SalesReportDto;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class SalesDao {
    private final JdbcTemplate jdbcTemplate;

    public List<SalesReportDto> fetchSalesReport() {
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

    public int cancelExpiredReservations() {
        String sql = "UPDATE reservation SET status = 'CANCELLED' WHERE status = 'PENDING' AND created_at < (CURRENT_TIMESTAMP - INTERVAL '15 minutes')";
        return jdbcTemplate.update(sql);
    }


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
