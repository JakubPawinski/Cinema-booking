package cinema.booking.cinemabooking.dto.response;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

/**
 * DTO representing movie details in responses.
 */
@Data
@Builder
public class MovieDto {

    @NotNull(message = "Movie ID cannot be null")
    private Long id;

    @NotBlank(message = "Movie title cannot be blank")
    private String title;

    @NotBlank(message = "Genre cannot be blank")
    private String genre;

    @Min(value = 1, message = "Duration must be at least 1 minute")
    private int durationMin;

    @NotBlank(message = "Description cannot be blank")
    private String description;

    @NotBlank(message = "Image URL cannot be blank")
    @URL(message = "Invalid image URL")
    private String imageUrl;

    @NotBlank(message = "Trailer URL cannot be blank")
    @URL(message = "Invalid trailer URL")
    private String trailerUrl;

    @NotBlank(message = "Director cannot be blank")
    private String director;

    @NotBlank(message = "Main cast cannot be blank")
    private String mainCast;

    @NotBlank(message = "Age rating cannot be blank")
    private String ageRating;
}
