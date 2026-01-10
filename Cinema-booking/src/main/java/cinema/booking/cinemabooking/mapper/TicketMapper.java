package cinema.booking.cinemabooking.mapper;

import cinema.booking.cinemabooking.enums.TicketType;
import cinema.booking.cinemabooking.model.Reservation;
import cinema.booking.cinemabooking.model.Seance;
import cinema.booking.cinemabooking.model.Seat;
import cinema.booking.cinemabooking.model.Ticket;
import org.springframework.stereotype.Component;

/**
 * Mapper class for Ticket entity and DTOs.
 */
@Component
public class TicketMapper {

    /**
     * Converts Ticket details to Ticket entity.
     *
     * @param reservation the Reservation entity
     * @param seance      the Seance entity
     * @param seat        the Seat entity
     * @param ticketType  the TicketType enum
     * @param price       the price of the ticket
     * @return the Ticket entity
     */
    public Ticket toEntity(Reservation reservation, Seance seance, Seat seat, TicketType ticketType, double price) {
        Ticket ticket = new Ticket();
        ticket.setReservation(reservation);
        ticket.setSeance(seance);
        ticket.setSeat(seat);
        ticket.setTicketType(ticketType);
        ticket.setPrice(price);
        return ticket;
    }
}
