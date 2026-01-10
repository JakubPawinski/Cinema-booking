package cinema.booking.cinemabooking.mapper;

import cinema.booking.cinemabooking.dto.response.SeanceDto;
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
                .build();
    }
}