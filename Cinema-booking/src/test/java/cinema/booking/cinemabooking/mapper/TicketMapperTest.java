package cinema.booking.cinemabooking.mapper;

import cinema.booking.cinemabooking.enums.TicketType;
import cinema.booking.cinemabooking.model.Reservation;
import cinema.booking.cinemabooking.model.Seance;
import cinema.booking.cinemabooking.model.Seat;
import cinema.booking.cinemabooking.model.Ticket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for TicketMapper.
 */
public class TicketMapperTest {

    private TicketMapper ticketMapper;
    private Reservation reservation;
    private Seance seance;
    private Seat seat;

    @BeforeEach
    void setUp() {
        ticketMapper = new TicketMapper();

        reservation = new Reservation();
        reservation.setId(1L);

        seance = new Seance();
        seance.setId(1L);

        seat = new Seat();
        seat.setId(1L);
        seat.setRowNumber(5);
        seat.setSeatNumber(12);
    }

    @Test
    void testToEntityReturnsNotNull() {
        Ticket result = ticketMapper.toEntity(reservation, seance, seat, TicketType.REGULAR, 15.0);
        assertThat(result).isNotNull();
    }

    @Test
    void testToEntityAssignsReservation() {
        Ticket result = ticketMapper.toEntity(reservation, seance, seat, TicketType.REGULAR, 15.0);
        assertThat(result.getReservation()).isEqualTo(reservation);
    }

    @Test
    void testToEntityAssignsSeance() {
        Ticket result = ticketMapper.toEntity(reservation, seance, seat, TicketType.REGULAR, 15.0);
        assertThat(result.getSeance()).isEqualTo(seance);
    }

    @Test
    void testToEntityAssignsSeat() {
        Ticket result = ticketMapper.toEntity(reservation, seance, seat, TicketType.REGULAR, 15.0);
        assertThat(result.getSeat()).isEqualTo(seat);
    }

    @Test
    void testToEntityAssignsTicketTypeRegular() {
        Ticket result = ticketMapper.toEntity(reservation, seance, seat, TicketType.REGULAR, 15.0);
        assertThat(result.getTicketType()).isEqualTo(TicketType.REGULAR);
    }

    @Test
    void testToEntityAssignsTicketTypeReduced() {
        Ticket result = ticketMapper.toEntity(reservation, seance, seat, TicketType.REDUCED, 10.0);
        assertThat(result.getTicketType()).isEqualTo(TicketType.REDUCED);
    }

    @Test
    void testToEntityAssignsPrice() {
        Ticket result = ticketMapper.toEntity(reservation, seance, seat, TicketType.REGULAR, 15.0);
        assertThat(result.getPrice()).isEqualTo(15.0);
    }

    @Test
    void testToEntityWithDifferentPrice() {
        Ticket result = ticketMapper.toEntity(reservation, seance, seat, TicketType.REDUCED, 12.5);
        assertThat(result.getPrice()).isEqualTo(12.5);
    }

    @Test
    void testToEntityWithZeroPrice() {
        Ticket result = ticketMapper.toEntity(reservation, seance, seat, TicketType.REGULAR, 0.0);
        assertThat(result.getPrice()).isEqualTo(0.0);
    }

    @Test
    void testToEntityWithHighPrice() {
        Ticket result = ticketMapper.toEntity(reservation, seance, seat, TicketType.REGULAR, 50.0);
        assertThat(result.getPrice()).isEqualTo(50.0);
    }

    @Test
    void testToEntityWithDifferentReservation() {
        Reservation reservation2 = new Reservation();
        reservation2.setId(2L);

        Ticket result = ticketMapper.toEntity(reservation2, seance, seat, TicketType.REGULAR, 15.0);
        assertThat(result.getReservation().getId()).isEqualTo(2L);
    }

    @Test
    void testToEntityWithDifferentSeance() {
        Seance seance2 = new Seance();
        seance2.setId(2L);

        Ticket result = ticketMapper.toEntity(reservation, seance2, seat, TicketType.REGULAR, 15.0);
        assertThat(result.getSeance().getId()).isEqualTo(2L);
    }

    @Test
    void testToEntityWithDifferentSeat() {
        Seat seat2 = new Seat();
        seat2.setId(2L);
        seat2.setRowNumber(10);
        seat2.setSeatNumber(20);

        Ticket result = ticketMapper.toEntity(reservation, seance, seat2, TicketType.REGULAR, 15.0);
        assertThat(result.getSeat().getId()).isEqualTo(2L);
    }

    @Test
    void testToEntityReservationIdCorrect() {
        Ticket result = ticketMapper.toEntity(reservation, seance, seat, TicketType.REGULAR, 15.0);
        assertThat(result.getReservation().getId()).isEqualTo(1L);
    }

    @Test
    void testToEntitySeanceIdCorrect() {
        Ticket result = ticketMapper.toEntity(reservation, seance, seat, TicketType.REGULAR, 15.0);
        assertThat(result.getSeance().getId()).isEqualTo(1L);
    }

    @Test
    void testToEntitySeatIdCorrect() {
        Ticket result = ticketMapper.toEntity(reservation, seance, seat, TicketType.REGULAR, 15.0);
        assertThat(result.getSeat().getId()).isEqualTo(1L);
    }

    @Test
    void testToEntitySeatRowNumberPreserved() {
        Ticket result = ticketMapper.toEntity(reservation, seance, seat, TicketType.REGULAR, 15.0);
        assertThat(result.getSeat().getRowNumber()).isEqualTo(5);
    }

    @Test
    void testToEntitySeatSeatNumberPreserved() {
        Ticket result = ticketMapper.toEntity(reservation, seance, seat, TicketType.REGULAR, 15.0);
        assertThat(result.getSeat().getSeatNumber()).isEqualTo(12);
    }

    @Test
    void testToEntityTicketCodeIsNull() {
        Ticket result = ticketMapper.toEntity(reservation, seance, seat, TicketType.REGULAR, 15.0);
        assertThat(result.getTicketCode()).isNull();
    }

    @Test
    void testToEntityTicketIdIsNull() {
        Ticket result = ticketMapper.toEntity(reservation, seance, seat, TicketType.REGULAR, 15.0);
        assertThat(result.getId()).isNull();
    }

    @Test
    void testToEntityWithAllReducedTickets() {
        Ticket result = ticketMapper.toEntity(reservation, seance, seat, TicketType.REDUCED, 10.0);
        assertThat(result.getTicketType()).isEqualTo(TicketType.REDUCED);
        assertThat(result.getPrice()).isEqualTo(10.0);
    }

    @Test
    void testToEntityMultipleCallsCreateDifferentInstances() {
        Ticket ticket1 = ticketMapper.toEntity(reservation, seance, seat, TicketType.REGULAR, 15.0);
        Ticket ticket2 = ticketMapper.toEntity(reservation, seance, seat, TicketType.REGULAR, 15.0);
        assertThat(ticket1).isNotSameAs(ticket2);
    }
}
