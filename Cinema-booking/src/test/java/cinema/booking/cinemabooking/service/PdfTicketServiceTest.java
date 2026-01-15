package cinema.booking.cinemabooking.service;

import cinema.booking.cinemabooking.enums.TicketType;
import cinema.booking.cinemabooking.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PdfTicketServiceTest {

    @InjectMocks
    private PdfTicketService pdfTicketService;

    private Reservation reservation;
    private Ticket ticket;
    private Movie movie;
    private Seance seance;
    private CinemaRoom cinemaRoom;
    private Seat seat;

    @BeforeEach
    void setUp() {
        // Setup Movie
        movie = new Movie();
        movie.setId(1L);
        movie.setTitle("Inception");
        movie.setDurationMin(148);

        // Setup CinemaRoom
        cinemaRoom = new CinemaRoom();
        cinemaRoom.setId(1L);
        cinemaRoom.setName("Room A");

        // Setup Seat
        seat = new Seat();
        seat.setId(1L);
        seat.setRowNumber(5);
        seat.setSeatNumber(10);
        seat.setCinemaRoom(cinemaRoom);

        // Setup Seance
        seance = new Seance();
        seance.setId(1L);
        seance.setMovie(movie);
        seance.setCinemaRoom(cinemaRoom);
        seance.setStartTime(LocalDateTime.of(2024, 1, 20, 18, 30));
        seance.setEndTime(LocalDateTime.of(2024, 1, 20, 20, 18));
        seance.setRegularTicketPrice(25.0);
        seance.setReducedTicketPrice(15.0);

        // Setup Ticket
        ticket = new Ticket();
        ticket.setId(1L);
        ticket.setTicketCode("TICKET-001");
        ticket.setPrice(25.0);
        ticket.setTicketType(TicketType.REGULAR);
        ticket.setSeance(seance);
        ticket.setSeat(seat);

        // Setup Reservation
        reservation = new Reservation();
        reservation.setId(1L);
        reservation.setReservationCode("RES-001");
        reservation.setTickets(new ArrayList<>(List.of(ticket)));
    }

    @Test
    void testGenerateReservationPdfSuccessfully() {
        // Act
        ByteArrayInputStream result = pdfTicketService.generateReservationPdf(reservation);

        // Assert
        assertThat(result)
                .isNotNull()
                .satisfies(stream -> assertThat(stream.available()).isGreaterThan(0));
    }

    @Test
    void testGenerateReservationPdfWithSingleTicket() {
        // Arrange
        reservation.setTickets(List.of(ticket));

        // Act
        ByteArrayInputStream result = pdfTicketService.generateReservationPdf(reservation);

        // Assert
        assertThat(result)
                .isNotNull()
                .satisfies(stream -> assertThat(stream.available()).isGreaterThan(0));
    }

    @Test
    void testGenerateReservationPdfWithMultipleTickets() {
        // Arrange
        Ticket ticket2 = new Ticket();
        ticket2.setId(2L);
        ticket2.setTicketCode("TICKET-002");
        ticket2.setPrice(25.0);
        ticket2.setTicketType(TicketType.REGULAR);
        ticket2.setSeance(seance);

        Seat seat2 = new Seat();
        seat2.setId(2L);
        seat2.setRowNumber(5);
        seat2.setSeatNumber(11);
        seat2.setCinemaRoom(cinemaRoom);
        ticket2.setSeat(seat2);

        reservation.setTickets(List.of(ticket, ticket2));

        // Act
        ByteArrayInputStream result = pdfTicketService.generateReservationPdf(reservation);

        // Assert
        assertThat(result)
                .isNotNull()
                .satisfies(stream -> assertThat(stream.available()).isGreaterThan(0));
    }

    @Test
    void testGenerateReservationPdfWithReducedTicketType() {
        // Arrange
        ticket.setTicketType(TicketType.REDUCED);
        ticket.setPrice(15.0);

        // Act
        ByteArrayInputStream result = pdfTicketService.generateReservationPdf(reservation);

        // Assert
        assertThat(result)
                .isNotNull()
                .satisfies(stream -> assertThat(stream.available()).isGreaterThan(0));
    }

    @Test
    void testGenerateReservationPdfWithDifferentSeanceTimes() {
        // Arrange
        seance.setStartTime(LocalDateTime.of(2024, 12, 25, 20, 45));
        seance.setEndTime(LocalDateTime.of(2024, 12, 25, 22, 33));

        // Act
        ByteArrayInputStream result = pdfTicketService.generateReservationPdf(reservation);

        // Assert
        assertThat(result)
                .isNotNull()
                .satisfies(stream -> assertThat(stream.available()).isGreaterThan(0));
    }

    @Test
    void testGenerateReservationPdfWithDifferentRoomName() {
        // Arrange
        cinemaRoom.setName("IMAX Screen");

        // Act
        ByteArrayInputStream result = pdfTicketService.generateReservationPdf(reservation);

        // Assert
        assertThat(result)
                .isNotNull()
                .satisfies(stream -> assertThat(stream.available()).isGreaterThan(0));
    }

    @Test
    void testGenerateReservationPdfWithDifferentSeatPosition() {
        // Arrange
        seat.setRowNumber(1);
        seat.setSeatNumber(1);

        // Act
        ByteArrayInputStream result = pdfTicketService.generateReservationPdf(reservation);

        // Assert
        assertThat(result)
                .isNotNull()
                .satisfies(stream -> assertThat(stream.available()).isGreaterThan(0));
    }

    @Test
    void testGenerateReservationPdfWithLongMovieTitle() {
        // Arrange
        movie.setTitle("The Lord of the Rings: The Fellowship of the Ring Extended Edition");

        // Act
        ByteArrayInputStream result = pdfTicketService.generateReservationPdf(reservation);

        // Assert
        assertThat(result)
                .isNotNull()
                .satisfies(stream -> assertThat(stream.available()).isGreaterThan(0));
    }

    @Test
    void testGenerateReservationPdfWithEmptyReservationCode() {
        // Arrange
        reservation.setReservationCode("");

        // Act
        ByteArrayInputStream result = pdfTicketService.generateReservationPdf(reservation);

        // Assert
        assertThat(result)
                .isNotNull()
                .satisfies(stream -> assertThat(stream.available()).isGreaterThan(0));
    }

    @Test
    void testGenerateReservationPdfPdfContentNotEmpty() {
        // Act
        ByteArrayInputStream result = pdfTicketService.generateReservationPdf(reservation);

        // Assert
        assertThat(result.available()).isGreaterThan(100);
    }

    @Test
    void testGenerateReservationPdfCanReadMultipleTimes() {
        // Act
        ByteArrayInputStream result = pdfTicketService.generateReservationPdf(reservation);
        byte[] firstRead = result.readAllBytes();

        // Reset stream and read again
        result = pdfTicketService.generateReservationPdf(reservation);
        byte[] secondRead = result.readAllBytes();

        // Assert
        assertThat(firstRead)
                .isNotEmpty();
        assertThat(secondRead)
                .isNotEmpty();
    }

    @Test
    void testGenerateReservationPdfWithThreeTickets() {
        // Arrange
        Ticket ticket2 = new Ticket();
        ticket2.setId(2L);
        ticket2.setTicketCode("TICKET-002");
        ticket2.setPrice(25.0);
        ticket2.setTicketType(TicketType.REGULAR);
        ticket2.setSeance(seance);

        Seat seat2 = new Seat();
        seat2.setId(2L);
        seat2.setRowNumber(5);
        seat2.setSeatNumber(11);
        seat2.setCinemaRoom(cinemaRoom);
        ticket2.setSeat(seat2);

        Ticket ticket3 = new Ticket();
        ticket3.setId(3L);
        ticket3.setTicketCode("TICKET-003");
        ticket3.setPrice(25.0);
        ticket3.setTicketType(TicketType.REGULAR);
        ticket3.setSeance(seance);

        Seat seat3 = new Seat();
        seat3.setId(3L);
        seat3.setRowNumber(5);
        seat3.setSeatNumber(12);
        seat3.setCinemaRoom(cinemaRoom);
        ticket3.setSeat(seat3);

        reservation.setTickets(List.of(ticket, ticket2, ticket3));

        // Act
        ByteArrayInputStream result = pdfTicketService.generateReservationPdf(reservation);

        // Assert
        assertThat(result)
                .isNotNull()
                .satisfies(stream -> assertThat(stream.available()).isGreaterThan(0));
    }
}
