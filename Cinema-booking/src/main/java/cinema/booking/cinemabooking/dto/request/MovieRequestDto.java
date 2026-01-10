package cinema.booking.cinemabooking.dto.request;

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
    @Size(min = 2, message = "Title must be at least 2 characters long")
    private String title;

    /**
     * The description of the movie.
     */
    @NotBlank(message = "Description is required")
    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    /**
     * The genre of the movie.
     */
    @NotBlank(message = "Genre is required")
    private String genre;

    /**
     * The duration of the movie in minutes.
     */
    @Min(value = 1, message = "Duration must be at least 1 minute")
    private int durationMin;

    /**
     * The director of the movie.
     */
    @NotBlank(message = "Director is required")
    private String director;

    /**
     * The main cast of the movie.
     */
    @NotBlank(message = "Main cast is required")
    private String mainCast;

    /**
     * The age rating of the movie.
     */
    @NotBlank(message = "Age rating is required")
    private String ageRating;

    /**
     * The URL of the movie's poster image.
     */
    @URL(message = "Invalid image URL")
    private String imageUrl;

    /**
     * The poster image file of the movie.
     */
    private MultipartFile imageFile;

    /**
     * The URL of the movie's trailer.
     */
    @URL(message = "Invalid trailer URL")
    private String trailerUrl;
}
