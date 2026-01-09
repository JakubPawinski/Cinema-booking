package cinema.booking.cinemabooking.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class MovieWithSeancesDto {
    private Long movieId;
    private String title;
    private String genre;
    private Integer durationMin;
    private String description;
    private String imageUrl;

    private List<SeanceDto> seances;
}
