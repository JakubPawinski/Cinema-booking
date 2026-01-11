package cinema.booking.cinemabooking.service;

import cinema.booking.cinemabooking.dto.report.DailySalesDto;
import cinema.booking.cinemabooking.dto.report.SalesReportDto;
import cinema.booking.cinemabooking.exception.FileStorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;


/**
 * Service for exporting sales data to CSV format.
 */
@Slf4j
@Service
public class CsvExportService {
    private static final String DAILY_HEADER = "Date,Tickets Sold,Total Revenue";
    private static final String MOVIE_HEADER = "Movie Title,Tickets Sold,Total Revenue";

    /**
     * Generates a CSV file for daily sales data.
     *
     * @param data List of DailySalesDto containing daily sales information.
     * @return ByteArrayInputStream representing the generated CSV file.
     * @throws FileStorageException if an I/O error occurs during CSV generation.
     */
    public ByteArrayInputStream generateDailySalesCsv(List<DailySalesDto> data) {
        log.info("Generating daily sales CSV with {} records", data.size());

        // Using try-with-resources to ensure streams are closed properly
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             PrintWriter writer = new PrintWriter(out, true, StandardCharsets.UTF_8)) {

            out.write(new byte[]{(byte)0xEF, (byte)0xBB, (byte)0xBF});
            writer.println(DAILY_HEADER);

            // Write each data row
            for (DailySalesDto row : data) {
                log.debug("Writing row: {}", row);

                writer.printf(Locale.US, "%s,%d,%.2f%n",
                        row.getDate(),
                        row.getTicketsSold(),
                        row.getTotalRevenue());
            }

            writer.flush();
            log.info("Daily sales CSV generation completed");
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            log.error("Error generating daily sales CSV", e);
            throw new FileStorageException("Error during generating CSV: " + e.getMessage());
        }
    }

    /**
     * Generates a CSV file for movie sales data.
     *
     * @param data List of SalesReportDto containing movie sales information.
     * @return ByteArrayInputStream representing the generated CSV file.
     * @throws FileStorageException if an I/O error occurs during CSV generation.
     */
    public ByteArrayInputStream generateMovieSalesCsv(List<SalesReportDto> data) {
        log.info("Generating movie sales CSV with {} records", data.size());

        // Using try-with-resources to ensure streams are closed properly
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             PrintWriter writer = new PrintWriter(out, true, StandardCharsets.UTF_8)) {

            out.write(new byte[]{(byte)0xEF, (byte)0xBB, (byte)0xBF});
            writer.println(MOVIE_HEADER);

            // Write each data row
            for (SalesReportDto row : data) {
                log.debug("Writing row: {}", row);
                writer.printf(Locale.US, "%s,%d,%.2f%n",
                        escapeSpecialCharacters(row.getMovieTitle()),
                        row.getTicketsSold(),
                        row.getTotalRevenue());
            }
            writer.flush();
            log.info("Movie sales CSV generation completed");
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            log.error("Error generating movie sales CSV", e);
            throw new FileStorageException("Error CSV: " + e.getMessage());
        }
    }

    /**
     * Escapes special characters in CSV data.
     *
     * @param data The input string to escape.
     * @return The escaped string.
     */
    private String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }
}