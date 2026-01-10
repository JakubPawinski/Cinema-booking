package cinema.booking.cinemabooking.dto.response;

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
    private Long movieId;

    @NotBlank(message = "Movie title is required")
    private String title;

    @NotBlank(message = "Genre is required")
    private String genre;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    private Integer durationMin;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Image URL is required")
    @URL(message = "Invalid image URL")
    private String imageUrl;

    @NotNull(message = "Seances list cannot be null")
    @Valid
    private List<SeanceDto> seances;
}
