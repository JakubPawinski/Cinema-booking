package cinema.booking.cinemabooking.controller.api;

import cinema.booking.cinemabooking.service.CsvExportService;
import cinema.booking.cinemabooking.service.ReportService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST API controller for reports
 */
@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Reports", description = "Endpoints for generating sales reports")
public class ReportRestController {

    private final ReportService reportService;
    private final CsvExportService csvExportService;

    /**
     * Downloads daily sales report as CSV.
     * @return CSV file containing daily sales report
     */
    @GetMapping("/daily/csv")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully downloaded daily sales report as CSV"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Resource> downloadDailyReportCsv() {
        log.info("API: Downloading daily sales report as CSV");

        // Fetching daily sales report data
        var data = reportService.getDailySalesReport();
        var file = new InputStreamResource(csvExportService.generateDailySalesCsv(data));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=daily_sales_report.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(file);
    }

    /**
     * Downloads movie sales report as CSV
     * @return CSV file containing movie sales report
     */
    @GetMapping("/movies/csv")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully downloaded movie sales report as CSV"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Resource> downloadMovieReportCsv() {
        log.info("API: Downloading movie sales report as CSV");

        // Fetching movie sales report data
        var data = reportService.getSalesReport();
        var file = new InputStreamResource(csvExportService.generateMovieSalesCsv(data));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=movie_sales_report.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(file);
    }
}