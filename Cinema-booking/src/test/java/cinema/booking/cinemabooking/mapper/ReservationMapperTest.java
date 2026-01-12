package cinema.booking.cinemabooking.mapper;

import cinema.booking.cinemabooking.dto.response.ReservationSummaryDto;
import cinema.booking.cinemabooking.enums.ReservationStatus;
import cinema.booking.cinemabooking.model.Reservation;
import cinema.booking.cinemabooking.model.Seance;
import cinema.booking.cinemabooking.model.Ticket;
import cinema.booking.cinemabooking.model.Seat;
import cinema.booking.cinemabooking.model.Movie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for ReservationMapper.
 */
public class ReservationMapperTest {

    private ReservationMapper reservationMapper;
    private Reservation reservation;
    private Seance seance;
    private Movie movie;

    @BeforeEach
    void setUp() {
        reservationMapper = new ReservationMapper();

        movie = new Movie();
        movie.setId(1L);
        movie.setTitle("Inception");

        seance = new Seance();
        seance.setId(1L);
        seance.setStartTime(LocalDateTime.of(2025, 3, 15, 19, 0));
        seance.setMovie(movie);

        Seat seat = new Seat();
        seat.setId(1L);
        seat.setSeatNumber(5);
        seat.setRowNumber(3);

        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setSeat(seat);
        ticket.setSeance(seance);

        reservation = new Reservation();
        reservation.setId(1L);
        reservation.setStatus(ReservationStatus.PAID);
        reservation.setTotalPrice(40.0);
        reservation.setExpiresAt(LocalDateTime.of(2025, 3, 15, 20, 0));
        reservation.setTickets(List.of(ticket));
    }

    @Test
    void testToSummaryDtoReturnsNotNull() {
        ReservationSummaryDto result = reservationMapper.toSummaryDto(reservation);
        assertThat(result).isNotNull();
    }

    @Test
    void testToSummaryDtoPreservesId() {
        ReservationSummaryDto result = reservationMapper.toSummaryDto(reservation);
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void testToSummaryDtoPreservesStatus() {
        ReservationSummaryDto result = reservationMapper.toSummaryDto(reservation);
        assertThat(result.getStatus()).isEqualTo(ReservationStatus.PAID);
    }

    @Test
    void testToSummaryDtoPreservesTotalPrice() {
        ReservationSummaryDto result = reservationMapper.toSummaryDto(reservation);
        assertThat(result.getTotalPrice()).isEqualTo(40.0);
    }

    @Test
    void testToSummaryDtoPreservesExpiresAt() {
        ReservationSummaryDto result = reservationMapper.toSummaryDto(reservation);
        assertThat(result.getExpiresAt()).isEqualTo(LocalDateTime.of(2025, 3, 15, 20, 0));
    }

    @Test
    void testToSummaryDtoPreservesMovieTitle() {
        ReservationSummaryDto result = reservationMapper.toSummaryDto(reservation);
        assertThat(result.getMovieTitle()).isEqualTo("Inception");
    }

    @Test
    void testToSummaryDtoPreservesSeanceStartTime() {
        ReservationSummaryDto result = reservationMapper.toSummaryDto(reservation);
        assertThat(result.getSeanceStartTime()).isEqualTo(LocalDateTime.of(2025, 3, 15, 19, 0));
    }

    @Test
    void testToSummaryDtoPreservesTicketCount() {
        ReservationSummaryDto result = reservationMapper.toSummaryDto(reservation);
        assertThat(result.getTicketCount()).isEqualTo(1);
    }

    @Test
    void testToSummaryDtoPreservesTickets() {
        ReservationSummaryDto result = reservationMapper.toSummaryDto(reservation);
        assertThat(result.getTickets()).hasSize(1);
    }

    @Test
    void testToSummaryDtoHandlesEmptyTickets() {
        reservation.setTickets(new ArrayList<>());
        ReservationSummaryDto result = reservationMapper.toSummaryDto(reservation);
        assertThat(result.getTickets()).isEmpty();
    }

    @Test
    void testToSummaryDtoHandlesEmptyTicketsReturnsDefaultMovieTitle() {
        reservation.setTickets(new ArrayList<>());
        ReservationSummaryDto result = reservationMapper.toSummaryDto(reservation);
        assertThat(result.getMovieTitle()).isEqualTo("No Tickets");
    }

    @Test
    void testToSummaryDtoHandlesEmptyTicketsReturnsNullSeanceStartTime() {
        reservation.setTickets(new ArrayList<>());
        ReservationSummaryDto result = reservationMapper.toSummaryDto(reservation);
        assertThat(result.getSeanceStartTime()).isNull();
    }

    @Test
    void testToSummaryDtoMapsTicketId() {
        ReservationSummaryDto result = reservationMapper.toSummaryDto(reservation);
        assertThat(result.getTickets().getFirst().getTicketId()).isEqualTo(1L);
    }

    @Test
    void testToSummaryDtoMapsTicketSeatNumber() {
        ReservationSummaryDto result = reservationMapper.toSummaryDto(reservation);
        assertThat(result.getTickets().getFirst().getSeatNumber()).isEqualTo(5);
    }

    @Test
    void testToSummaryDtoMapsTicketRowNumber() {
        ReservationSummaryDto result = reservationMapper.toSummaryDto(reservation);
        assertThat(result.getTickets().getFirst().getRowNumber()).isEqualTo(3);
    }

    @Test
    void testToSummaryDtoMapsMultipleTickets() {
        Seat seat2 = new Seat();
        seat2.setId(2L);
        seat2.setSeatNumber(6);
        seat2.setRowNumber(3);

        Ticket ticket2 = new Ticket();
        ticket2.setId(2L);
        ticket2.setSeat(seat2);
        ticket2.setSeance(seance);

        reservation.setTickets(List.of(
                reservation.getTickets().getFirst(),
                ticket2
        ));

        ReservationSummaryDto result = reservationMapper.toSummaryDto(reservation);
        assertThat(result.getTickets()).hasSize(2);
    }

    @Test
    void testToSummaryDtoEmptyTicketsCountIsZero() {
        reservation.setTickets(new ArrayList<>());
        ReservationSummaryDto result = reservationMapper.toSummaryDto(reservation);
        assertThat(result.getTicketCount()).isEqualTo(0);
    }

    @Test
    void testToSummaryDtoMultipleTicketsCountCorrect() {
        Seat seat2 = new Seat();
        seat2.setId(2L);
        seat2.setSeatNumber(6);
        seat2.setRowNumber(3);

        Ticket ticket2 = new Ticket();
        ticket2.setId(2L);
        ticket2.setSeat(seat2);
        ticket2.setSeance(seance);

        reservation.setTickets(List.of(
                reservation.getTickets().getFirst(),
                ticket2
        ));

        ReservationSummaryDto result = reservationMapper.toSummaryDto(reservation);
        assertThat(result.getTicketCount()).isEqualTo(2);
    }
}
