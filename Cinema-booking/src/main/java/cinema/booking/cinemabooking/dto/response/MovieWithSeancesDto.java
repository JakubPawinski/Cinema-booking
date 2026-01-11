package cinema.booking.cinemabooking.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import java.util.List;

/**
 * DTO representing a movie along with its seances.
 */
@Data
@Builder
public class MovieWithSeancesDto {

    @NotNull(message = "Movie ID cannot be null")
    @Schema(description = "Unique identifier of the movie", example = "1")
    private Long movieId;

    @NotBlank(message = "Movie title is required")
    @Schema(description = "Title of the movie", example = "Inception")
    private String title;

    @NotBlank(message = "Genre is required")
    @Schema(description = "Genre of the movie", example = "Science Fiction")
    private String genre;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    @Schema(description = "Duration of the movie in minutes", example = "148")
    private Integer durationMin;

    @NotBlank(message = "Description is required")
    @Schema(description = "Description of the movie", example = "A mind-bending thriller about dream invasion.")
    private String description;

    @NotBlank(message = "Image URL is required")
    @URL(message = "Invalid image URL")
    @Schema(description = "URL of the movie poster image", example = "http://example.com/inception.jpg")
    private String imageUrl;

    @NotNull(message = "Seances list cannot be null")
    @Schema(description = "List of seances for the movie")
    @Valid
    private List<SeanceDto> seances;
}
