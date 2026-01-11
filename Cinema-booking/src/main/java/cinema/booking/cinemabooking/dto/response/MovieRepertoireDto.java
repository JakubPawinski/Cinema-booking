package cinema.booking.cinemabooking.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

/**
 * DTO representing movie details in the repertoire.
 */
@Data
@Builder
public class MovieRepertoireDto {

    @NotNull(message = "Movie ID cannot be null")
    @Schema(description = "Unique identifier of the movie in the repertoire", example = "1")
    private Long movieId;

    @NotBlank(message = "Movie title is required for repertoire")
    @Schema(description = "Title of the movie in the repertoire", example = "Inception")
    private String title;

    @NotBlank(message = "Genre is required for repertoire")
    @Schema(description = "Genre of the movie in the repertoire", example = "Science Fiction")
    private String genre;

    @NotBlank(message = "Image URL is required for repertoire display")
    @Schema(description = "URL of the movie poster image in the repertoire", example = "http://example.com/inception.jpg")
    private String imageUrl;
}
