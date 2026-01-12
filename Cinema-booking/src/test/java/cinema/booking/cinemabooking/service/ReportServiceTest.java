package cinema.booking.cinemabooking.service;

import cinema.booking.cinemabooking.dao.SalesDao;
import cinema.booking.cinemabooking.dto.report.DailySalesDto;
import cinema.booking.cinemabooking.dto.report.SalesReportDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private SalesDao salesDao;

    @InjectMocks
    private ReportService reportService;

    private SalesReportDto salesReportDto;
    private DailySalesDto dailySalesDto;

    @BeforeEach
    void setUp() {
        salesReportDto = new SalesReportDto();
        salesReportDto.setMovieTitle("Inception");
        salesReportDto.setTotalRevenue(2000.00);
        salesReportDto.setTicketsSold(500L);

        dailySalesDto = new DailySalesDto();
        dailySalesDto.setDate(LocalDate.of(2024, 1, 15));
        dailySalesDto.setTotalRevenue(1500.00);
        dailySalesDto.setTicketsSold(150L);
    }

    @Test
    void testGetSalesReportSuccessfully() {
        // Arrange
        List<SalesReportDto> expectedReport = List.of(salesReportDto);
        when(salesDao.fetchSalesReport()).thenReturn(expectedReport);

        // Act
        List<SalesReportDto> result = reportService.getSalesReport();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMovieTitle()).isEqualTo("Inception");
        assertThat(result.get(0).getTotalRevenue()).isEqualTo(2000.00);
        assertThat(result.get(0).getTicketsSold()).isEqualTo(500);
        verify(salesDao, times(1)).fetchSalesReport();
    }

    @Test
    void testGetSalesReportReturnsEmptyList() {
        // Arrange
        when(salesDao.fetchSalesReport()).thenReturn(List.of());

        // Act
        List<SalesReportDto> result = reportService.getSalesReport();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(salesDao, times(1)).fetchSalesReport();
    }

    @Test
    void testGetSalesReportWithMultipleMovies() {
        // Arrange
        SalesReportDto secondReport = new SalesReportDto();
        secondReport.setMovieTitle("The Matrix");
        secondReport.setTotalRevenue(3000.00);
        secondReport.setTicketsSold(750L);

        List<SalesReportDto> expectedReport = List.of(salesReportDto, secondReport);
        when(salesDao.fetchSalesReport()).thenReturn(expectedReport);

        // Act
        List<SalesReportDto> result = reportService.getSalesReport();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(salesReportDto, secondReport);
        verify(salesDao, times(1)).fetchSalesReport();
    }

    @Test
    void testGetDailySalesReportSuccessfully() {
        // Arrange
        List<DailySalesDto> expectedReport = List.of(dailySalesDto);
        when(salesDao.fetchDailySales()).thenReturn(expectedReport);

        // Act
        List<DailySalesDto> result = reportService.getDailySalesReport();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDate()).isEqualTo(LocalDate.of(2024, 1, 15));
        assertThat(result.get(0).getTotalRevenue()).isEqualTo(1500.00);
        assertThat(result.get(0).getTicketsSold()).isEqualTo(150L);
        verify(salesDao, times(1)).fetchDailySales();
    }

    @Test
    void testGetDailySalesReportReturnsEmptyList() {
        // Arrange
        when(salesDao.fetchDailySales()).thenReturn(List.of());

        // Act
        List<DailySalesDto> result = reportService.getDailySalesReport();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(salesDao, times(1)).fetchDailySales();
    }

    @Test
    void testGetDailySalesReportWithMultipleDays() {
        // Arrange
        DailySalesDto secondDailySales = new DailySalesDto();
        secondDailySales.setDate(LocalDate.of(2024, 1, 16));
        secondDailySales.setTotalRevenue(2000.00);
        secondDailySales.setTicketsSold(200L);

        List<DailySalesDto> expectedReport = List.of(dailySalesDto, secondDailySales);
        when(salesDao.fetchDailySales()).thenReturn(expectedReport);

        // Act
        List<DailySalesDto> result = reportService.getDailySalesReport();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(dailySalesDto, secondDailySales);
        verify(salesDao, times(1)).fetchDailySales();
    }
}
