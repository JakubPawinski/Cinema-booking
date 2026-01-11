package cinema.booking.cinemabooking.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import java.util.List;

/**
 * DTO representing movie details in responses.
 */
@Data
@Builder
public class MovieDto {

    @NotNull(message = "Movie ID cannot be null")
    @Schema(description = "Unique identifier of the movie", example = "1")
    private Long id;

    @NotBlank(message = "Movie title cannot be blank")
    @Schema(description = "Title of the movie", example = "Inception")
    private String title;

    @NotBlank(message = "Genre cannot be blank")
    @Schema(description = "Genre of the movie", example = "Science Fiction")
    private String genre;

    @Min(value = 1, message = "Duration must be at least 1 minute")
    @Schema(description = "Duration of the movie in minutes", example = "148")
    private int durationMin;

    @NotBlank(message = "Description cannot be blank")
    @Schema(description = "Description of the movie", example = "A mind-bending thriller about dream invasion.")
    private String description;

    @NotBlank(message = "Image URL cannot be blank")
    @Schema(description = "URL of the movie poster image", example = "http://example.com/inception.jpg")
    @URL(message = "Invalid image URL")
    private String imageUrl;

    @NotBlank(message = "Trailer URL cannot be blank")
    @URL(message = "Invalid trailer URL")
    @Schema(description = "URL of the movie trailer", example = "http://example.com/inception-trailer.mp4")
    private String trailerUrl;

    @NotBlank(message = "Director cannot be blank")
    @Schema(description = "Director of the movie", example = "Christopher Nolan")
    private String director;

    @NotBlank(message = "Main cast cannot be blank")
    @Schema(description = "Main cast of the movie", example = "Leonardo DiCaprio, Joseph Gordon-Levitt, Ellen Page")
    private String mainCast;

    @NotBlank(message = "Age rating cannot be blank")
    @Schema(description = "Age rating of the movie", example = "PG-13")
    private String ageRating;

    @Schema(description = "Gallery images of the movie", example = "[\"http://example.com/image1.jpg\", \"http://example.com/image2.jpg\"]")
    private List<String> galleryImages;
}
