package cinema.booking.cinemabooking.service;

import cinema.booking.cinemabooking.dto.report.DailySalesDto;
import cinema.booking.cinemabooking.dto.report.SalesReportDto;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale; // 1. Dodaj ten import

@Service
public class CsvExportService {

    public ByteArrayInputStream generateDailySalesCsv(List<DailySalesDto> data) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             PrintWriter writer = new PrintWriter(out, true, StandardCharsets.UTF_8)) {

            out.write(new byte[]{(byte)0xEF, (byte)0xBB, (byte)0xBF});
            writer.println("Data,Sprzedane Bilety,Przychód");

            for (DailySalesDto row : data) {
                // 2. Dodaj Locale.US jako pierwszy parametr
                writer.printf(Locale.US, "%s,%d,%.2f%n",
                        row.getDate(),
                        row.getTicketsSold(),
                        row.getTotalRevenue());
            }

            writer.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Błąd podczas generowania CSV: " + e.getMessage());
        }
    }

    public ByteArrayInputStream generateMovieSalesCsv(List<SalesReportDto> data) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             PrintWriter writer = new PrintWriter(out, true, StandardCharsets.UTF_8)) {

            out.write(new byte[]{(byte)0xEF, (byte)0xBB, (byte)0xBF});
            writer.println("Tytuł Filmu,Sprzedane Bilety,Przychód");

            for (SalesReportDto row : data) {
                // 3. Dodaj Locale.US jako pierwszy parametr
                writer.printf(Locale.US, "%s,%d,%.2f%n",
                        escapeSpecialCharacters(row.getMovieTitle()),
                        row.getTicketsSold(),
                        row.getTotalRevenue());
            }
            writer.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Błąd CSV: " + e.getMessage());
        }
    }

    private String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }
}