package cinema.booking.cinemabooking.dto.request;

import lombok.Data;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

@Data
public class MovieRequestDto {
    @NotBlank(message = "Title could not be empty")
    @Size(min = 2, message = "Title must be at least 2 characters long")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Genre is required")
    private String genre;

    @Min(value = 1, message = "Duration must be at least 1 minute")
    private int durationMin;

    private String director;
    private String mainCast;
    private String ageRating;

    private String imageUrl;

    private MultipartFile imageFile;

    private String trailerUrl;
}
