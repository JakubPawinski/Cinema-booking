package cinema.booking.cinemabooking.mapper;

import cinema.booking.cinemabooking.dto.response.SeanceDto;
import cinema.booking.cinemabooking.model.Seance;
import org.springframework.stereotype.Component;

@Component
public class SeanceMapper {

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