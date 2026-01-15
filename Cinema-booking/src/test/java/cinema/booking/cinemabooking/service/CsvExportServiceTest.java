package cinema.booking.cinemabooking.service;

import cinema.booking.cinemabooking.dto.report.DailySalesDto;
import cinema.booking.cinemabooking.dto.report.SalesReportDto;
import cinema.booking.cinemabooking.exception.FileStorageException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import static org.mockito.Mockito.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mockConstruction;

class CsvExportServiceTest {

    private CsvExportService csvExportService;

    @BeforeEach
    void setUp() {
        csvExportService = new CsvExportService();
    }

    @Test
    void testGenerateDailySalesCsvSuccessfully() throws IOException {
        // Arrange
        List<DailySalesDto> data = new ArrayList<>();
        data.add(new DailySalesDto(LocalDate.of(2024, 1, 1), 10L, 250.50));
        data.add(new DailySalesDto(LocalDate.of(2024, 1, 2), 15L, 375.75));

        // Act
        ByteArrayInputStream result = csvExportService.generateDailySalesCsv(data);

        // Assert
        String content = readContent(result);
        assertThat(content)
                .isNotNull()
                .contains("Date,Tickets Sold,Total Revenue")
                .contains("2024-01-01,10,250.50")
                .contains("2024-01-02,15,375.75");
    }

    @Test
    void testGenerateDailySalesCsvWithEmptyData() throws IOException {
        // Arrange
        List<DailySalesDto> data = new ArrayList<>();

        // Act
        ByteArrayInputStream result = csvExportService.generateDailySalesCsv(data);

        // Assert
        String content = readContent(result);
        assertThat(content)
                .isNotNull()
                .contains("Date,Tickets Sold,Total Revenue");
        assertThat(content.trim().split("\n"))
                .hasSize(1);
    }

    @Test
    void testGenerateDailySalesCsvIncludesBOM() throws IOException {
        // Arrange
        List<DailySalesDto> data = new ArrayList<>();
        data.add(new DailySalesDto(LocalDate.of(2024, 1, 1), 5L, 100.00));

        // Act
        ByteArrayInputStream result = csvExportService.generateDailySalesCsv(data);

        // Assert
        byte[] bytes = result.readAllBytes();
        assertThat(bytes[0])
                .isEqualTo((byte) 0xEF);
        assertThat(bytes[1])
                .isEqualTo((byte) 0xBB);
        assertThat(bytes[2])
                .isEqualTo((byte) 0xBF);
    }

    @Test
    void testGenerateMovieSalesCsvSuccessfully() throws IOException {
        // Arrange
        List<SalesReportDto> data = new ArrayList<>();
        data.add(new SalesReportDto("Inception", 50L, 1250.00));
        data.add(new SalesReportDto("Avatar", 75L, 1875.50));

        // Act
        ByteArrayInputStream result = csvExportService.generateMovieSalesCsv(data);

        // Assert
        String content = readContent(result);
        assertThat(content)
                .isNotNull()
                .contains("Movie Title,Tickets Sold,Total Revenue")
                .contains("Inception,50,1250.00")
                .contains("Avatar,75,1875.50");
    }

    @Test
    void testGenerateMovieSalesCsvWithEmptyData() throws IOException {
        // Arrange
        List<SalesReportDto> data = new ArrayList<>();

        // Act
        ByteArrayInputStream result = csvExportService.generateMovieSalesCsv(data);

        // Assert
        String content = readContent(result);
        assertThat(content)
                .isNotNull()
                .contains("Movie Title,Tickets Sold,Total Revenue");
        assertThat(content.trim().split("\n"))
                .hasSize(1);
    }

    @Test
    void testGenerateMovieSalesCsvEscapesSpecialCharacters() throws IOException {
        // Arrange
        List<SalesReportDto> data = new ArrayList<>();
        data.add(new SalesReportDto("Movie with \"Quotes\" and Commas, Inc.", 20L, 500.00));

        // Act
        ByteArrayInputStream result = csvExportService.generateMovieSalesCsv(data);

        // Assert
        String content = readContent(result);
        assertThat(content)
                .isNotNull()
                .contains("\"Movie with \"\"Quotes\"\" and Commas, Inc.\"");
    }

    @Test
    void testGenerateMovieSalesCsvHandlesMultilineText() throws IOException {
        // Arrange
        List<SalesReportDto> data = new ArrayList<>();
        data.add(new SalesReportDto("Movie\nWith\nNewlines", 10L, 250.00));

        // Act
        ByteArrayInputStream result = csvExportService.generateMovieSalesCsv(data);

        // Assert
        String content = readContent(result);
        assertThat(content)
                .isNotNull()
                .contains("Movie With Newlines");
    }

    @Test
    void testGenerateDailySalesCsvWithInternalIOException() {
        // Arrange
        List<DailySalesDto> data = List.of(new DailySalesDto(LocalDate.now(), 10L, 100.0));

        try (MockedConstruction<ByteArrayOutputStream> mocked = mockConstruction(ByteArrayOutputStream.class,
                (mock, context) -> {
                    doThrow(new IOException("Simulated IO error")).when(mock).write(any(byte[].class));
                })) {

            // Act & Assert
            assertThatThrownBy(() -> csvExportService.generateDailySalesCsv(data))
                    .isInstanceOf(FileStorageException.class)
                    .hasMessageContaining("Error during generating CSV: Simulated IO error");
        }
    }

    @Test
    void testGenerateMovieSalesCsvWithInternalIOException() {
        // Arrange
        List<SalesReportDto> data = List.of(new SalesReportDto("Title", 10L, 100.0));

        try (MockedConstruction<ByteArrayOutputStream> mocked = mockConstruction(ByteArrayOutputStream.class,
                (mock, context) -> {
                    doThrow(new IOException("Simulated IO error")).when(mock).write(any(byte[].class));
                })) {

            // Act & Assert
            assertThatThrownBy(() -> csvExportService.generateMovieSalesCsv(data))
                    .isInstanceOf(FileStorageException.class)
                    .hasMessageContaining("Error CSV: Simulated IO error");
        }
    }

    @Test
    void testGenerateDailySalesCsvWithLargeDataset() throws IOException {
        // Arrange
        List<DailySalesDto> data = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            data.add(new DailySalesDto(
                    LocalDate.of(2024, 1, 1).plusDays(i),
                    (long) (10 + i),
                    250.00 + (i * 10)
            ));
        }

        // Act
        ByteArrayInputStream result = csvExportService.generateDailySalesCsv(data);

        // Assert
        String content = readContent(result);
        String[] lines = content.trim().split("\n");
        assertThat(lines)
                .isNotNull()
                .hasSize(1001);
    }

    @Test
    void testGenerateMovieSalesCsvWithZeroRevenue() throws IOException {
        // Arrange
        List<SalesReportDto> data = new ArrayList<>();
        data.add(new SalesReportDto("Free Screening", 30L, 0.00));

        // Act
        ByteArrayInputStream result = csvExportService.generateMovieSalesCsv(data);

        // Assert
        String content = readContent(result);
        assertThat(content)
                .isNotNull()
                .contains("Free Screening,30,0.00");
    }

    @Test
    void testGenerateDailySalesCsvWithDecimalPrecision() throws IOException {
        // Arrange
        List<DailySalesDto> data = new ArrayList<>();
        data.add(new DailySalesDto(LocalDate.of(2024, 1, 1), 7L, 123.456));

        // Act
        ByteArrayInputStream result = csvExportService.generateDailySalesCsv(data);

        // Assert
        String content = readContent(result);
        assertThat(content)
                .isNotNull()
                .contains("2024-01-01,7,123.46");
    }

    private String readContent(ByteArrayInputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(stream, StandardCharsets.UTF_8)
        );
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }
}
