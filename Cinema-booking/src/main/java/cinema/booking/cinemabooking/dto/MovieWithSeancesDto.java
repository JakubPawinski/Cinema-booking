package cinema.booking.cinemabooking.dto;

import lombok.Data;
import java.util.List;

@Data
public class MovieWithSeancesDto {
    private MovieDto movie;
    private List<SeanceDto> seances;

    public MovieWithSeancesDto(MovieDto movie, List<SeanceDto> seances) {
        this.movie = movie;
        this.seances = seances;
    }

}
