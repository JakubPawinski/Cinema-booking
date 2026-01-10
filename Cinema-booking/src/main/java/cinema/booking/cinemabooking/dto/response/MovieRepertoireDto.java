package cinema.booking.cinemabooking.dto.response;

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
    private Long movieId;

    @NotBlank(message = "Movie title is required for repertoire")
    private String title;

    @NotBlank(message = "Genre is required for repertoire")
    private String genre;

    @NotBlank(message = "Image URL is required for repertoire display")
    private String imageUrl;
}
