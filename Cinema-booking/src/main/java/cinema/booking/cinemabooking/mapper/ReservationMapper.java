package cinema.booking.cinemabooking.mapper;

import cinema.booking.cinemabooking.dto.response.ReservationSummaryDto;
import cinema.booking.cinemabooking.dto.response.TicketDto;
import cinema.booking.cinemabooking.model.Reservation;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Mapper for converting Reservation entities to ReservationSummaryDto.
 */
@Component
public class ReservationMapper {

    /**
     * Converts a Reservation entity to a ReservationSummaryDto.
     *
     * @param reservation the Reservation entity
     * @return the corresponding ReservationSummaryDto
     */
    public ReservationSummaryDto toSummaryDto(Reservation reservation) {
        // Default values in case there are no tickets
        String movieTitle = "No Tickets";
        LocalDateTime seanceStartTime = null;

        // If there are tickets, get the movie title and seance start time from the first ticket
        if (reservation.getTickets() != null && !reservation.getTickets().isEmpty()) {
            movieTitle = reservation.getTickets().getFirst().getSeance().getMovie().getTitle();
            seanceStartTime = reservation.getTickets().getFirst().getSeance().getStartTime();
        }

        List<TicketDto> ticketDtos = reservation.getTickets().stream()
                .map(ticket -> new TicketDto(
                        ticket.getId(),
                        ticket.getSeat().getId(),
                        ticket.getSeat().getSeatNumber(),
                        ticket.getSeat().getRowNumber()
                ))
                .toList();

        return ReservationSummaryDto.builder()
                .id(reservation.getId())
                .status(reservation.getStatus())
                .totalPrice(reservation.getTotalPrice())
                .expiresAt(reservation.getExpiresAt())
                .ticketCount(reservation.getTickets().size())
                .movieTitle(movieTitle)
                .seanceStartTime(seanceStartTime)
                .tickets(ticketDtos)
                .build();
    }
}