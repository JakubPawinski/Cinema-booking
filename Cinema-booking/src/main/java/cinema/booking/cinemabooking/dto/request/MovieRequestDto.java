package cinema.booking.cinemabooking.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;
import org.springframework.web.multipart.MultipartFile;

/**
 * DTO for creating or updating movie information.
 */
@Data
public class MovieRequestDto {
    /**
     * The title of the movie.
     */
    @NotBlank(message = "Title could not be empty")
    @Schema(description = "The title of the movie", example = "Inception")
    @Size(min = 2, message = "Title must be at least 2 characters long")
    private String title;

    /**
     * The description of the movie.
     */
    @NotBlank(message = "Description is required")
    @Schema(description = "The description of the movie", example = "A mind-bending thriller about dream invasion.")
    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    /**
     * The genre of the movie.
     */
    @NotBlank(message = "Genre is required")
    @Schema(description = "The genre of the movie", example = "Science Fiction")
    private String genre;

    /**
     * The duration of the movie in minutes.
     */
    @Min(value = 1, message = "Duration must be at least 1 minute")
    @Schema(description = "The duration of the movie in minutes", example = "148")
    private int durationMin;

    /**
     * The director of the movie.
     */
    @NotBlank(message = "Director is required")
    @Schema(description = "The director of the movie", example = "Christopher Nolan")
    private String director;

    /**
     * The main cast of the movie.
     */
    @NotBlank(message = "Main cast is required")
    @Schema(description = "The main cast of the movie", example = "Leonardo DiCaprio, Joseph Gordon-Levitt, Ellen Page")
    private String mainCast;

    /**
     * The age rating of the movie.
     */
    @NotBlank(message = "Age rating is required")
    @Schema(description = "The age rating of the movie", example = "PG-13")
    private String ageRating;

    /**
     * The URL of the movie's poster image.
     */
    @URL(message = "Invalid image URL")
    @Schema(description = "The URL of the movie's poster image", example = "http://example.com/poster.jpg")
    private String imageUrl;

    /**
     * The poster image file of the movie.
     */
    private MultipartFile imageFile;

    /**
     * The URL of the movie's trailer.
     */
    @URL(message = "Invalid trailer URL")
    @Schema(description = "The URL of the movie's trailer", example = "http://example.com/trailer.mp4")
    private String trailerUrl;
}
