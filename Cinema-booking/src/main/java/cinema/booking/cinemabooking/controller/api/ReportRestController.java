package cinema.booking.cinemabooking.controller.api;

import cinema.booking.cinemabooking.service.CsvExportService;
import cinema.booking.cinemabooking.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportRestController {

    private final ReportService reportService;
    private final CsvExportService csvExportService;

    // Endpoint: Pobierz CSV sprzedaży dziennej
    @GetMapping("/daily/csv")
    public ResponseEntity<Resource> downloadDailyReportCsv() {
        var data = reportService.getDailySalesReport();
        var file = new InputStreamResource(csvExportService.generateDailySalesCsv(data));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=sprzedaz_dzienna.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(file);
    }

    // Endpoint: Pobierz CSV sprzedaży wg filmów
    @GetMapping("/movies/csv")
    public ResponseEntity<Resource> downloadMovieReportCsv() {
        var data = reportService.getSalesReport();
        var file = new InputStreamResource(csvExportService.generateMovieSalesCsv(data));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=sprzedaz_filmy.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(file);
    }
}