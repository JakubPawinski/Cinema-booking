package cinema.booking.cinemabooking.mapper;

import cinema.booking.cinemabooking.dto.response.ReservationSummaryDto;
import cinema.booking.cinemabooking.model.Reservation;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ReservationMapper {

    public ReservationSummaryDto toSummaryDto(Reservation r) {
        // Logika wyciągania tytułu i daty (zabezpieczenie przed brakiem biletów)
        String title = r.getTickets().isEmpty() ? "Brak biletów" : r.getTickets().get(0).getSeance().getMovie().getTitle();
        LocalDateTime time = r.getTickets().isEmpty() ? null : r.getTickets().get(0).getSeance().getStartTime();

        return ReservationSummaryDto.builder()
                .id(r.getId())
                .status(r.getStatus())
                .totalPrice(r.getTotalPrice())
                .expiresAt(r.getExpiresAt())
                .ticketCount(r.getTickets().size())
                .movieTitle(title)
                .seanceStartTime(time)
                .build();
    }
}