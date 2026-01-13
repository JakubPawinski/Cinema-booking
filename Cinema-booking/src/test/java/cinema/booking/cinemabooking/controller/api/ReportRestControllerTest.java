package cinema.booking.cinemabooking.controller.api;

import cinema.booking.cinemabooking.config.SecurityConfig;
import cinema.booking.cinemabooking.dto.report.DailySalesDto;
import cinema.booking.cinemabooking.dto.report.SalesReportDto;
import cinema.booking.cinemabooking.service.CsvExportService;
import cinema.booking.cinemabooking.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReportRestController.class)
@Import(SecurityConfig.class)
@DisplayName("REST API Tests for ReportRestController")
class ReportRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReportService reportService;

    @MockitoBean
    private CsvExportService csvExportService;

    private DailySalesDto dailySalesDto;
    private SalesReportDto salesReportDto;

    @BeforeEach
    void setUp() {
        dailySalesDto = new DailySalesDto();
        dailySalesDto.setDate(LocalDate.of(2025, 1, 15));
        dailySalesDto.setTicketsSold(150L);
        dailySalesDto.setTotalRevenue(1800.00);

        salesReportDto = new SalesReportDto();
        salesReportDto.setMovieTitle("Test Movie");
        salesReportDto.setTicketsSold(75L);
        salesReportDto.setTotalRevenue(900.00);
    }

    // ============= DOWNLOAD DAILY REPORT CSV =============

    @Test
    @DisplayName("Scenario 1: Download daily report CSV - admin only")
    @WithMockUser(roles = "ADMIN")
    void testDownloadDailyReportCsv_AdminOnly_Success() throws Exception {
        String csvContent = "Date,Total Tickets,Revenue\n2025-01-15,150,1800.00";
        InputStream csvStream = new ByteArrayInputStream(csvContent.getBytes());

        when(reportService.getDailySalesReport()).thenReturn(List.of(dailySalesDto));
        doReturn(csvStream).when(csvExportService).generateDailySalesCsv(List.of(dailySalesDto));

        mockMvc.perform(get("/api/v1/reports/daily/csv")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=daily_sales_report.csv"))
                .andExpect(header().string("Content-Type", "text/csv"));

        verify(reportService, times(1)).getDailySalesReport();
        verify(csvExportService, times(1)).generateDailySalesCsv(List.of(dailySalesDto));
    }

    @Test
    @DisplayName("Scenario 2: Download daily report CSV - access denied for users")
    @WithMockUser(roles = "USER")
    void testDownloadDailyReportCsv_UserDenied() throws Exception {
        mockMvc.perform(get("/api/v1/reports/daily/csv")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(reportService, never()).getDailySalesReport();
        verify(csvExportService, never()).generateDailySalesCsv(any());
    }

    @Test
    @DisplayName("Scenario 3: Download daily report CSV - no authentication")
    void testDownloadDailyReportCsv_Unauthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/reports/daily/csv")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection());

        verify(reportService, never()).getDailySalesReport();
        verify(csvExportService, never()).generateDailySalesCsv(any());
    }

    @Test
    @DisplayName("Scenario 4: Download daily report CSV - empty report")
    @WithMockUser(roles = "ADMIN")
    void testDownloadDailyReportCsv_EmptyReport() throws Exception {
        String csvContent = "Date,Total Tickets,Revenue\n";
        InputStream csvStream = new ByteArrayInputStream(csvContent.getBytes());

        when(reportService.getDailySalesReport()).thenReturn(List.of());
        doReturn(csvStream).when(csvExportService).generateDailySalesCsv(List.of());

        mockMvc.perform(get("/api/v1/reports/daily/csv")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=daily_sales_report.csv"))
                .andExpect(header().string("Content-Type", "text/csv"));

        verify(reportService, times(1)).getDailySalesReport();
        verify(csvExportService, times(1)).generateDailySalesCsv(List.of());
    }

    @Test
    @DisplayName("Scenario 5: Download daily report CSV - multiple entries")
    @WithMockUser(roles = "ADMIN")
    void testDownloadDailyReportCsv_MultipleEntries() throws Exception {
        DailySalesDto dailySalesDto2 = new DailySalesDto();
        dailySalesDto2.setDate(LocalDate.of(2025, 1, 16));
        dailySalesDto2.setTicketsSold(200L);
        dailySalesDto2.setTotalRevenue(2380.00);

        String csvContent = "Date,Total Tickets,Revenue\n2025-01-15,150,1800.00\n2025-01-16,200,2380.00";
        InputStream csvStream = new ByteArrayInputStream(csvContent.getBytes());

        when(reportService.getDailySalesReport()).thenReturn(List.of(dailySalesDto, dailySalesDto2));
        doReturn(csvStream).when(csvExportService).generateDailySalesCsv(List.of(dailySalesDto, dailySalesDto2));

        mockMvc.perform(get("/api/v1/reports/daily/csv")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=daily_sales_report.csv"));

        verify(reportService, times(1)).getDailySalesReport();
        verify(csvExportService, times(1)).generateDailySalesCsv(List.of(dailySalesDto, dailySalesDto2));
    }

    // ============= DOWNLOAD MOVIE REPORT CSV =============

    @Test
    @DisplayName("Scenario 6: Download movie report CSV - admin only")
    @WithMockUser(roles = "ADMIN")
    void testDownloadMovieReportCsv_AdminOnly_Success() throws Exception {
        String csvContent = "Title,Total Tickets,Revenue\nTest Movie,75,900.00";
        InputStream csvStream = new ByteArrayInputStream(csvContent.getBytes());

        when(reportService.getSalesReport()).thenReturn(List.of(salesReportDto));
        doReturn(csvStream).when(csvExportService).generateMovieSalesCsv(List.of(salesReportDto));

        mockMvc.perform(get("/api/v1/reports/movies/csv")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=movie_sales_report.csv"))
                .andExpect(header().string("Content-Type", "text/csv"));

        verify(reportService, times(1)).getSalesReport();
        verify(csvExportService, times(1)).generateMovieSalesCsv(List.of(salesReportDto));
    }

    @Test
    @DisplayName("Scenario 7: Download movie report CSV - access denied for users")
    @WithMockUser(roles = "USER")
    void testDownloadMovieReportCsv_UserDenied() throws Exception {
        mockMvc.perform(get("/api/v1/reports/movies/csv")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(reportService, never()).getSalesReport();
        verify(csvExportService, never()).generateMovieSalesCsv(any());
    }

    @Test
    @DisplayName("Scenario 8: Download movie report CSV - no authentication")
    void testDownloadMovieReportCsv_Unauthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/reports/movies/csv")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection());

        verify(reportService, never()).getSalesReport();
        verify(csvExportService, never()).generateMovieSalesCsv(any());
    }

    @Test
    @DisplayName("Scenario 9: Download movie report CSV - empty report")
    @WithMockUser(roles = "ADMIN")
    void testDownloadMovieReportCsv_EmptyReport() throws Exception {
        String csvContent = "Title,Total Tickets,Revenue\n";
        InputStream csvStream = new ByteArrayInputStream(csvContent.getBytes());

        when(reportService.getSalesReport()).thenReturn(List.of());
        doReturn(csvStream).when(csvExportService).generateMovieSalesCsv(List.of());

        mockMvc.perform(get("/api/v1/reports/movies/csv")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=movie_sales_report.csv"));

        verify(reportService, times(1)).getSalesReport();
        verify(csvExportService, times(1)).generateMovieSalesCsv(List.of());
    }

    @Test
    @DisplayName("Scenario 10: Download movie report CSV - multiple movies")
    @WithMockUser(roles = "ADMIN")
    void testDownloadMovieReportCsv_MultipleMovies() throws Exception {
        SalesReportDto salesReportDto2 = new SalesReportDto();
        salesReportDto2.setMovieTitle("Another Movie");
        salesReportDto2.setTicketsSold(120L);
        salesReportDto2.setTotalRevenue(1440.00);

        String csvContent = "Title,Total Tickets,Revenue\nTest Movie,75,900.00\nAnother Movie,120,1440.00";
        InputStream csvStream = new ByteArrayInputStream(csvContent.getBytes());

        when(reportService.getSalesReport()).thenReturn(List.of(salesReportDto, salesReportDto2));
        doReturn(csvStream).when(csvExportService).generateMovieSalesCsv(List.of(salesReportDto, salesReportDto2));

        mockMvc.perform(get("/api/v1/reports/movies/csv")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=movie_sales_report.csv"));

        verify(reportService, times(1)).getSalesReport();
        verify(csvExportService, times(1)).generateMovieSalesCsv(List.of(salesReportDto, salesReportDto2));
    }

    @Test
    @DisplayName("Scenario 11: Download movie report CSV - verify content type")
    @WithMockUser(roles = "ADMIN")
    void testDownloadMovieReportCsv_VerifyContentType() throws Exception {
        String csvContent = "Title,Total Tickets,Revenue\nTest Movie,75,900.00";
        InputStream csvStream = new ByteArrayInputStream(csvContent.getBytes());

        when(reportService.getSalesReport()).thenReturn(List.of(salesReportDto));
        doReturn(csvStream).when(csvExportService).generateMovieSalesCsv(List.of(salesReportDto));

        mockMvc.perform(get("/api/v1/reports/movies/csv")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(header().exists("Content-Type"));

        verify(reportService, times(1)).getSalesReport();
    }

    @Test
    @DisplayName("Scenario 12: Download daily report CSV - verify content type")
    @WithMockUser(roles = "ADMIN")
    void testDownloadDailyReportCsv_VerifyContentType() throws Exception {
        String csvContent = "Date,Total Tickets,Revenue\n2025-01-15,150,1800.00";
        InputStream csvStream = new ByteArrayInputStream(csvContent.getBytes());

        when(reportService.getDailySalesReport()).thenReturn(List.of(dailySalesDto));
        doReturn(csvStream).when(csvExportService).generateDailySalesCsv(List.of(dailySalesDto));

        mockMvc.perform(get("/api/v1/reports/daily/csv")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(header().exists("Content-Type"));

        verify(reportService, times(1)).getDailySalesReport();
    }
}
