package cinema.booking.cinemabooking.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MovieDto {
    private Long id;
    private String title;
    private String genre;
    private int durationMin;
    private String description;
    private String imageUrl;
    private String trailerUrl;

    private String director;
    private String mainCast;
    private String ageRating;
}
