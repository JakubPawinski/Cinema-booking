package cinema.booking.cinemabooking.repository;

import cinema.booking.cinemabooking.enums.ReservationStatus;
import cinema.booking.cinemabooking.enums.TicketType;
import cinema.booking.cinemabooking.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for TicketRepository.
 */
@DataJpaTest
public class TicketRepositoryTest {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private CinemaRoomRepository cinemaRoomRepository;

    @Autowired
    private SeanceRepository seanceRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    private User user;
    private Movie movie;
    private CinemaRoom cinemaRoom;
    private Seance seance;
    private Seat seat;
    private Reservation reservation;
    private Ticket ticket;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setUsername("testuser");
        user = userRepository.save(user);

        movie = new Movie();
        movie.setTitle("Inception");
        movie.setGenre("Science Fiction");
        movie.setDurationMin(148);
        movie.setDirector("Christopher Nolan");
        movie.setMainCast("Leonardo DiCaprio");
        movie.setAgeRating("PG-13");
        movie = movieRepository.save(movie);

        cinemaRoom = new CinemaRoom();
        cinemaRoom.setName("Sala 1");
        cinemaRoom = cinemaRoomRepository.save(cinemaRoom);

        LocalDateTime startTime = LocalDateTime.of(2025, 1, 15, 19, 0);
        LocalDateTime endTime = LocalDateTime.of(2025, 1, 15, 21, 28);

        seance = new Seance();
        seance.setMovie(movie);
        seance.setCinemaRoom(cinemaRoom);
        seance.setStartTime(startTime);
        seance.setEndTime(endTime);
        seance.setRegularTicketPrice(25.0);
        seance.setReducedTicketPrice(15.0);
        seance = seanceRepository.save(seance);

        seat = new Seat();
        seat.setRowNumber(1);
        seat.setSeatNumber(1);
        seat.setCinemaRoom(cinemaRoom);
        seat = seatRepository.save(seat);

        reservation = new Reservation();
        reservation.setReservationCode("RES001");
        reservation.setCreatedAt(LocalDateTime.now());
        reservation.setExpiresAt(LocalDateTime.now().plusHours(2));
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setTotalPrice(25.0);
        reservation.setUser(user);
        reservation = reservationRepository.save(reservation);

        ticket = new Ticket();
        ticket.setTicketCode("TICK001");
        ticket.setTicketType(TicketType.REGULAR);
        ticket.setPrice(25.0);
        ticket.setReservation(reservation);
        ticket.setSeance(seance);
        ticket.setSeat(seat);
    }

    @Test
    void testSaveTicketReturnsNotNull() {
        Ticket saved = ticketRepository.save(ticket);
        assertThat(saved).isNotNull();
    }

    @Test
    void testSaveTicketGeneratesId() {
        Ticket saved = ticketRepository.save(ticket);
        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void testSaveTicketPreservesCode() {
        Ticket saved = ticketRepository.save(ticket);
        assertThat(saved.getTicketCode()).isEqualTo("TICK001");
    }

    @Test
    void testSaveTicketPreservesType() {
        Ticket saved = ticketRepository.save(ticket);
        assertThat(saved.getTicketType()).isEqualTo(TicketType.REGULAR);
    }

    @Test
    void testFindByIdReturnsTicket() {
        Ticket saved = ticketRepository.save(ticket);
        Ticket found = ticketRepository.findById(saved.getId()).orElse(null);
        assertThat(found).isNotNull();
    }

    @Test
    void testFindByIdPreservesCode() {
        Ticket saved = ticketRepository.save(ticket);
        Ticket found = ticketRepository.findById(saved.getId()).orElse(null);
        assertThat(found.getTicketCode()).isEqualTo("TICK001");
    }

    @Test
    void testFindByIdPreservesPrice() {
        Ticket saved = ticketRepository.save(ticket);
        Ticket found = ticketRepository.findById(saved.getId()).orElse(null);
        assertThat(found.getPrice()).isEqualTo(25.0);
    }

    @Test
    void testFindAllTakenTicketsWithPaidReservation() {
        reservation.setStatus(ReservationStatus.PAID);
        reservationRepository.save(reservation);
        ticketRepository.save(ticket);

        LocalDateTime now = LocalDateTime.now();
        List<Ticket> takenTickets = ticketRepository.findAllTakenTickets(seance.getId(), now);

        assertThat(takenTickets).hasSize(1);
    }

    @Test
    void testFindAllTakenTicketsWithPaidReservationReturnsCorrectTicket() {
        reservation.setStatus(ReservationStatus.PAID);
        reservationRepository.save(reservation);
        Ticket saved = ticketRepository.save(ticket);

        LocalDateTime now = LocalDateTime.now();
        List<Ticket> takenTickets = ticketRepository.findAllTakenTickets(seance.getId(), now);

        assertThat(takenTickets.get(0).getId()).isEqualTo(saved.getId());
    }

    @Test
    void testFindAllTakenTicketsWithPendingReservation() {
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setExpiresAt(LocalDateTime.now().plusHours(1));
        reservationRepository.save(reservation);
        ticketRepository.save(ticket);

        LocalDateTime now = LocalDateTime.now();
        List<Ticket> takenTickets = ticketRepository.findAllTakenTickets(seance.getId(), now);

        assertThat(takenTickets).hasSize(1);
    }

    @Test
    void testFindAllTakenTicketsWithExpiredReservation() {
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setExpiresAt(LocalDateTime.now().minusMinutes(1));
        reservationRepository.save(reservation);
        ticketRepository.save(ticket);

        LocalDateTime now = LocalDateTime.now();
        List<Ticket> takenTickets = ticketRepository.findAllTakenTickets(seance.getId(), now);

        assertThat(takenTickets).isEmpty();
    }

    @Test
    void testFindAllTakenTicketsWithCancelledReservation() {
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
        ticketRepository.save(ticket);

        LocalDateTime now = LocalDateTime.now();
        List<Ticket> takenTickets = ticketRepository.findAllTakenTickets(seance.getId(), now);

        assertThat(takenTickets).isEmpty();
    }

    @Test
    void testFindAllTakenTicketsMultipleTickets() {
        reservation.setStatus(ReservationStatus.PAID);
        reservationRepository.save(reservation);

        Ticket ticket1 = ticketRepository.save(ticket);

        Seat seat2 = new Seat();
        seat2.setRowNumber(1);
        seat2.setSeatNumber(2);
        seat2.setCinemaRoom(cinemaRoom);
        seat2 = seatRepository.save(seat2);

        Ticket ticket2 = new Ticket();
        ticket2.setTicketCode("TICK002");
        ticket2.setTicketType(TicketType.REDUCED);
        ticket2.setPrice(15.0);
        ticket2.setReservation(reservation);
        ticket2.setSeance(seance);
        ticket2.setSeat(seat2);
        Ticket saved2 = ticketRepository.save(ticket2);

        LocalDateTime now = LocalDateTime.now();
        List<Ticket> takenTickets = ticketRepository.findAllTakenTickets(seance.getId(), now);

        assertThat(takenTickets).hasSize(2);
    }

    @Test
    void testFindAllTakenTicketsMultipleTicketsContainsCorrectIds() {
        reservation.setStatus(ReservationStatus.PAID);
        reservationRepository.save(reservation);

        Ticket ticket1 = ticketRepository.save(ticket);

        Seat seat2 = new Seat();
        seat2.setRowNumber(1);
        seat2.setSeatNumber(2);
        seat2.setCinemaRoom(cinemaRoom);
        seat2 = seatRepository.save(seat2);

        Ticket ticket2 = new Ticket();
        ticket2.setTicketCode("TICK002");
        ticket2.setTicketType(TicketType.REDUCED);
        ticket2.setPrice(15.0);
        ticket2.setReservation(reservation);
        ticket2.setSeance(seance);
        ticket2.setSeat(seat2);
        Ticket saved2 = ticketRepository.save(ticket2);

        LocalDateTime now = LocalDateTime.now();
        List<Ticket> takenTickets = ticketRepository.findAllTakenTickets(seance.getId(), now);

        assertThat(takenTickets.stream().map(Ticket::getId)).containsExactlyInAnyOrder(ticket1.getId(), saved2.getId());
    }

    @Test
    void testFindByReservationUserId() {
        reservation.setStatus(ReservationStatus.PAID);
        reservationRepository.save(reservation);
        Ticket saved = ticketRepository.save(ticket);

        List<Ticket> userTickets = ticketRepository.findByReservationUserId(user.getId());

        assertThat(userTickets).hasSize(1);
    }

    @Test
    void testFindByReservationUserIdReturnsCorrectTicket() {
        reservation.setStatus(ReservationStatus.PAID);
        reservationRepository.save(reservation);
        Ticket saved = ticketRepository.save(ticket);

        List<Ticket> userTickets = ticketRepository.findByReservationUserId(user.getId());

        assertThat(userTickets.get(0).getId()).isEqualTo(saved.getId());
    }

    @Test
    void testFindByReservationUserIdContainsCorrectUser() {
        reservation.setStatus(ReservationStatus.PAID);
        reservationRepository.save(reservation);
        ticketRepository.save(ticket);

        List<Ticket> userTickets = ticketRepository.findByReservationUserId(user.getId());

        assertThat(userTickets.get(0).getReservation().getUser().getId()).isEqualTo(user.getId());
    }

    @Test
    void testFindByReservationUserIdMultipleTickets() {
        reservation.setStatus(ReservationStatus.PAID);
        reservationRepository.save(reservation);

        Ticket ticket1 = ticketRepository.save(ticket);

        Seat seat2 = new Seat();
        seat2.setRowNumber(1);
        seat2.setSeatNumber(2);
        seat2.setCinemaRoom(cinemaRoom);
        seat2 = seatRepository.save(seat2);

        Ticket ticket2 = new Ticket();
        ticket2.setTicketCode("TICK002");
        ticket2.setTicketType(TicketType.REDUCED);
        ticket2.setPrice(15.0);
        ticket2.setReservation(reservation);
        ticket2.setSeance(seance);
        ticket2.setSeat(seat2);
        Ticket saved2 = ticketRepository.save(ticket2);

        List<Ticket> userTickets = ticketRepository.findByReservationUserId(user.getId());

        assertThat(userTickets).hasSize(2);
    }

    @Test
    void testFindByReservationUserIdMultipleTicketsContainsCorrectIds() {
        reservation.setStatus(ReservationStatus.PAID);
        reservationRepository.save(reservation);

        Ticket ticket1 = ticketRepository.save(ticket);

        Seat seat2 = new Seat();
        seat2.setRowNumber(1);
        seat2.setSeatNumber(2);
        seat2.setCinemaRoom(cinemaRoom);
        seat2 = seatRepository.save(seat2);

        Ticket ticket2 = new Ticket();
        ticket2.setTicketCode("TICK002");
        ticket2.setTicketType(TicketType.REDUCED);
        ticket2.setPrice(15.0);
        ticket2.setReservation(reservation);
        ticket2.setSeance(seance);
        ticket2.setSeat(seat2);
        Ticket saved2 = ticketRepository.save(ticket2);

        List<Ticket> userTickets = ticketRepository.findByReservationUserId(user.getId());

        assertThat(userTickets.stream().map(Ticket::getId)).containsExactlyInAnyOrder(ticket1.getId(), saved2.getId());
    }

    @Test
    void testUpdateTicketType() {
        Ticket saved = ticketRepository.save(ticket);
        saved.setTicketType(TicketType.REDUCED);
        Ticket updated = ticketRepository.save(saved);

        assertThat(updated.getTicketType()).isEqualTo(TicketType.REDUCED);
    }

    @Test
    void testUpdateTicketPrice() {
        Ticket saved = ticketRepository.save(ticket);
        saved.setPrice(15.0);
        Ticket updated = ticketRepository.save(saved);

        assertThat(updated.getPrice()).isEqualTo(15.0);
    }

    @Test
    void testDeleteTicket() {
        Ticket saved = ticketRepository.save(ticket);
        ticketRepository.deleteById(saved.getId());

        assertThat(ticketRepository.findById(saved.getId())).isEmpty();
    }
}
