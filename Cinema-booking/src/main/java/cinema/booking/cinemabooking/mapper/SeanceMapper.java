package cinema.booking.cinemabooking.mapper;

import cinema.booking.cinemabooking.dto.request.SeanceRequestDto;
import cinema.booking.cinemabooking.dto.response.SeanceDto;
import cinema.booking.cinemabooking.model.CinemaRoom;
import cinema.booking.cinemabooking.model.Movie;
import cinema.booking.cinemabooking.model.Seance;
import org.springframework.stereotype.Component;

/**
 * Mapper class for converting Seance entities to SeanceDto objects.
 */
@Component
public class SeanceMapper {

    /**
     * Converts a Seance entity to a SeanceDto.
     *
     * @param seance the Seance entity
     * @return the corresponding SeanceDto
     */
    public SeanceDto toDto(Seance seance) {
        return SeanceDto.builder()
                .id(seance.getId())
                .startTime(seance.getStartTime())
                .endTime(seance.getEndTime())
                .regularTicketPrice(seance.getRegularTicketPrice())
                .reducedTicketPrice(seance.getReducedTicketPrice())
                .roomName(seance.getCinemaRoom().getName())
                .movieId(seance.getMovie().getId())
                .movieTitle(seance.getMovie().getTitle())
                .build();
    }

    /**
     * Converts a SeanceRequestDto to a Seance entity.
     *
     * @param dto the SeanceRequestDto
     * @param movie the Movie entity associated with the seance
     * @param cinemaRoom the CinemaRoom entity associated with the seance
     * @return the Seance entity
     */
    public Seance toEntity(SeanceRequestDto dto, Movie movie, CinemaRoom cinemaRoom) {
        Seance seance = new Seance();
        seance.setMovie(movie);
        seance.setCinemaRoom(cinemaRoom);
        seance.setStartTime(dto.getStartTime());
        seance.setEndTime(dto.getStartTime().plusMinutes(movie.getDurationMin()));
        seance.setRegularTicketPrice(dto.getRegularTicketPrice());
        seance.setReducedTicketPrice(dto.getReducedTicketPrice());
        return seance;
    }
}