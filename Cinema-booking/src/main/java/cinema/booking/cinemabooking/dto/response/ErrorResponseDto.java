package cinema.booking.cinemabooking.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorResponseDto {
    @Schema(description = "Timestamp of when the error occurred", example = "2024-06-15T14:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "HTTP status code of the error", example = "404")
    private int status;

    @Schema(description = "Error type", example = "Not Found")
    private String error;

    @Schema(description = "Detailed error message", example = "The requested resource was not found.")
    private String message;

    @Schema(description = "Path of the request that caused the error", example = "/api/movies/1")
    private String path;
}
