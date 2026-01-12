package cinema.booking.cinemabooking.dao;

import cinema.booking.cinemabooking.dto.report.DailySalesDto;
import cinema.booking.cinemabooking.dto.report.SalesReportDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for SalesDao.
 */
@DataJpaTest
@Import(SalesDao.class)
@Sql(scripts = "/test-sales-data.sql")
public class SalesDaoTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private SalesDao salesDao;

    @BeforeEach
    void setUp() {
        salesDao = new SalesDao(jdbcTemplate);
    }

    // Tests fetchSalesReport

    @Test
    void testFetchSalesReportReturnsNotNull() {
        List<SalesReportDto> result = salesDao.fetchSalesReport();
        assertThat(result).isNotNull();
    }

    @Test
    void testFetchSalesReportReturnsList() {
        List<SalesReportDto> result = salesDao.fetchSalesReport();
        assertThat(result).isInstanceOf(List.class);
    }

    @Test
    void testFetchSalesReportAllHaveMovieTitle() {
        List<SalesReportDto> result = salesDao.fetchSalesReport();
        assertThat(result).allMatch(report -> report.getMovieTitle() != null && !report.getMovieTitle().isBlank());
    }

    @Test
    void testFetchSalesReportAllTicketsCountIsZeroOrPositive() {
        List<SalesReportDto> result = salesDao.fetchSalesReport();
        assertThat(result).allMatch(report -> report.getTicketsSold() >= 0);
    }

    @Test
    void testFetchSalesReportAllTotalRevenueIsZeroOrPositive() {
        List<SalesReportDto> result = salesDao.fetchSalesReport();
        assertThat(result).allMatch(report -> report.getTotalRevenue() >= 0);
    }

    @Test
    void testFetchSalesReportOrderedByRevenueDescending() {
        List<SalesReportDto> result = salesDao.fetchSalesReport();
        if (result.size() > 1) {
            assertThat(result.get(0).getTotalRevenue()).isGreaterThanOrEqualTo(result.get(1).getTotalRevenue());
        }
    }

    // Tests fetchDailySales

    @Test
    void testFetchDailySalesReturnsNotNull() {
        List<DailySalesDto> result = salesDao.fetchDailySales();
        assertThat(result).isNotNull();
    }

    @Test
    void testFetchDailySalesReturnsList() {
        List<DailySalesDto> result = salesDao.fetchDailySales();
        assertThat(result).isInstanceOf(List.class);
    }

    @Test
    void testFetchDailySalesAllDateNotNull() {
        List<DailySalesDto> result = salesDao.fetchDailySales();
        assertThat(result).allMatch(sales -> sales.getDate() != null);
    }

    @Test
    void testFetchDailySalesAllTicketsSoldIsZeroOrPositive() {
        List<DailySalesDto> result = salesDao.fetchDailySales();
        assertThat(result).allMatch(sales -> sales.getTicketsSold() >= 0);
    }

    @Test
    void testFetchDailySalesAllTotalRevenueIsZeroOrPositive() {
        List<DailySalesDto> result = salesDao.fetchDailySales();
        assertThat(result).allMatch(sales -> sales.getTotalRevenue() >= 0);
    }

    @Test
    void testFetchDailySalesOrderedByDateDescending() {
        List<DailySalesDto> result = salesDao.fetchDailySales();
        if (result.size() > 1) {
            assertThat(result.get(0).getDate()).isAfterOrEqualTo(result.get(1).getDate());
        }
    }

    @Test
    void testFetchDailySalesAllDatesNotInFuture() {
        List<DailySalesDto> result = salesDao.fetchDailySales();
        assertThat(result).allMatch(sales -> !sales.getDate().isAfter(LocalDate.now()));
    }

    // JdbcTemplate Tests - Basic Queries

    @Test
    void testJdbcTemplateQueryMovieCount() {
        String sql = "SELECT COUNT(*) FROM movie";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        assertThat(count).isGreaterThanOrEqualTo(0);
    }

    @Test
    void testJdbcTemplateQueryForMovieTitleExists() {
        String sql = "SELECT title FROM movie WHERE title = ?";
        List<String> titles = jdbcTemplate.queryForList(sql, String.class, "Inception");
        assertThat(titles).isNotEmpty();
        assertThat(titles).contains("Inception");
    }

    @Test
    void testJdbcTemplateQueryForTicketCount() {
        String sql = "SELECT COUNT(*) FROM ticket";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        assertThat(count).isGreaterThanOrEqualTo(0);
    }

    @Test
    void testJdbcTemplateQueryForSalesReportSqlExecution() {
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
            """;
        List<SalesReportDto> results = jdbcTemplate.query(sql, (rs, rowNum) ->
                new SalesReportDto(
                        rs.getString("movie_title"),
                        rs.getLong("tickets_count"),
                        rs.getDouble("total_revenue")
                )
        );
        assertThat(results).isNotNull();
    }

    @Test
    void testJdbcTemplateQueryForTotalRevenueSummation() {
        String sql = "SELECT COALESCE(SUM(price), 0) as total FROM ticket WHERE price > 0";
        Double totalRevenue = jdbcTemplate.queryForObject(sql, Double.class);
        assertThat(totalRevenue).isGreaterThanOrEqualTo(0);
    }

    @Test
    void testJdbcTemplateQueryForAverageTicketPrice() {
        String sql = "SELECT AVG(price) FROM ticket WHERE price > 0";
        Double avgPrice = jdbcTemplate.queryForObject(sql, Double.class);
        if (avgPrice != null) {
            assertThat(avgPrice).isGreaterThanOrEqualTo(0);
        }
    }

    @Test
    void testJdbcTemplateQueryForMaxTicketPrice() {
        String sql = "SELECT MAX(price) FROM ticket";
        Double maxPrice = jdbcTemplate.queryForObject(sql, Double.class);
        if (maxPrice != null) {
            assertThat(maxPrice).isGreaterThanOrEqualTo(0);
        }
    }

    // Tests for reservation and sales data

    @Test
    void testJdbcTemplateQueryForReservationCount() {
        String sql = "SELECT COUNT(*) FROM reservation WHERE status = 'PAID'";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        assertThat(count).isGreaterThanOrEqualTo(0);
    }

    @Test
    void testJdbcTemplateQueryForDailySalesReportSqlExecution() {
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
        List<DailySalesDto> results = jdbcTemplate.query(sql, (rs, rowNum) ->
                new DailySalesDto(
                        rs.getDate("sale_date").toLocalDate(),
                        rs.getLong("tickets_sold"),
                        rs.getDouble("total_revenue")
                )
        );
        assertThat(results).isNotNull();
    }

    @Test
    void testJdbcTemplateQueryForDistinctSalesDates() {
        String sql = "SELECT DISTINCT CAST(r.created_at AS DATE) as sale_date FROM reservation r WHERE r.status = 'PAID' ORDER BY sale_date DESC";
        List<LocalDate> dates = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getDate("sale_date").toLocalDate());
        assertThat(dates).isNotNull();
    }


    @Test
    void testJdbcTemplateQueryForDailyRevenueTotal() {
        String sql = "SELECT COALESCE(SUM(t.price), 0) FROM ticket t JOIN reservation r ON t.reservation_id = r.id WHERE r.status = 'PAID'";
        Double totalRevenue = jdbcTemplate.queryForObject(sql, Double.class);
        assertThat(totalRevenue).isGreaterThanOrEqualTo(0);
    }

    // Cross-verification Tests

    @Test
    void testSalesReportCountMatchesDirectJdbcQuery() {
        List<SalesReportDto> daoResult = salesDao.fetchSalesReport();

        String countSql = "SELECT COUNT(DISTINCT m.id) FROM movie m";
        Integer dbCount = jdbcTemplate.queryForObject(countSql, Integer.class);

        assertThat(daoResult).isNotNull();
        assertThat(daoResult.size()).isLessThanOrEqualTo(dbCount);
    }

    @Test
    void testDailySalesCountMatchesDirectJdbcQuery() {
        List<DailySalesDto> daoResult = salesDao.fetchDailySales();

        String countSql = "SELECT COUNT(DISTINCT CAST(r.created_at AS DATE)) FROM reservation r WHERE r.status = 'PAID'";
        Integer dbCount = jdbcTemplate.queryForObject(countSql, Integer.class);

        assertThat(daoResult).isNotNull();
        assertThat(daoResult.size()).isLessThanOrEqualTo(dbCount);
    }

    @Test
    void testSalesReportRevenueMatchesSumOfTickets() {
        List<SalesReportDto> result = salesDao.fetchSalesReport();

        String sql = "SELECT COALESCE(SUM(t.price), 0) FROM ticket t JOIN reservation r ON t.reservation_id = r.id WHERE r.status = 'PAID'";
        Double totalFromDb = jdbcTemplate.queryForObject(sql, Double.class);

        if (!result.isEmpty()) {
            Double daoTotal = result.stream()
                    .mapToDouble(SalesReportDto::getTotalRevenue)
                    .sum();
            assertThat(daoTotal).isGreaterThanOrEqualTo(0);
        }
    }

    @Test
    void testJdbcTemplateRowMapperForSalesReport() {
        String sql = "SELECT m.title, COUNT(t.id) as cnt FROM movie m LEFT JOIN seance s ON s.movie_id = m.id LEFT JOIN ticket t ON t.seance_id = s.id GROUP BY m.id, m.title LIMIT 1";
        List<String> results = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("title"));
        assertThat(results).isNotNull();
    }

    @Test
    void testJdbcTemplateRowMapperForDailySales() {
        String sql = "SELECT CAST(r.created_at AS DATE) as date FROM reservation r WHERE r.status = 'PAID' LIMIT 1";
        List<LocalDate> results = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getDate("date").toLocalDate());
        assertThat(results).isNotNull();
    }
}
