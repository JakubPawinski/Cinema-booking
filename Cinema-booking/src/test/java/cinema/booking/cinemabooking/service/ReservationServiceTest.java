package cinema.booking.cinemabooking.service;

import cinema.booking.cinemabooking.dto.request.CreateReservationDto;
import cinema.booking.cinemabooking.dto.response.ReservationSummaryDto;
import cinema.booking.cinemabooking.enums.ReservationStatus;
import cinema.booking.cinemabooking.enums.TicketType;
import cinema.booking.cinemabooking.exception.InvalidReservationActionException;
import cinema.booking.cinemabooking.exception.ResourceNotFoundException;
import cinema.booking.cinemabooking.exception.SeatAlreadyOccupiedException;
import cinema.booking.cinemabooking.mapper.ReservationMapper;
import cinema.booking.cinemabooking.mapper.TicketMapper;
import cinema.booking.cinemabooking.model.*;
import cinema.booking.cinemabooking.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private SeanceRepository seanceRepository;

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private PdfTicketService pdfTicketService;

    @Mock
    private ReservationMapper reservationMapper;

    @Mock
    private TicketMapper ticketMapper;

    @InjectMocks
    private ReservationService reservationService;

    private User user;
    private Seance seance;
    private Seat seat;
    private CinemaRoom cinemaRoom;
    private Movie movie;
    private Reservation reservation;
    private Ticket ticket;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        movie = new Movie();
        movie.setId(1L);
        movie.setTitle("Test Movie");
        movie.setDurationMin(120);

        cinemaRoom = new CinemaRoom();
        cinemaRoom.setId(1L);
        cinemaRoom.setName("Hall A");

        seance = new Seance();
        seance.setId(1L);
        seance.setMovie(movie);
        seance.setCinemaRoom(cinemaRoom);
        seance.setStartTime(LocalDateTime.of(2024, 5, 1, 18, 0));
        seance.setEndTime(LocalDateTime.of(2024, 5, 1, 20, 0));
        seance.setRegularTicketPrice(15.0);
        seance.setReducedTicketPrice(10.0);

        seat = new Seat();
        seat.setId(1L);
        seat.setCinemaRoom(cinemaRoom);
        seat.setRowNumber(1);
        seat.setSeatNumber(1);

        reservation = new Reservation();
        reservation.setId(1L);
        reservation.setUser(user);
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setCreatedAt(LocalDateTime.now());
        reservation.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        reservation.setTotalPrice(15.0);

        ticket = new Ticket();
        ticket.setId(1L);
        ticket.setReservation(reservation);
        ticket.setSeance(seance);
        ticket.setSeat(seat);
        ticket.setTicketType(TicketType.REGULAR);
        ticket.setPrice(15.0);

        reservation.setTickets(new ArrayList<>(List.of(ticket)));
    }

    @Test
    void testCreateReservationSuccessfully() {
        // Arrange
        CreateReservationDto.TicketRequest ticketRequest = new CreateReservationDto.TicketRequest();
        ticketRequest.setSeatId(1L);
        ticketRequest.setTicketType(TicketType.REGULAR);

        CreateReservationDto request = new CreateReservationDto();
        request.setSeanceId(1L);
        request.setTickets(List.of(ticketRequest));

        ReservationSummaryDto summaryDto = mock(ReservationSummaryDto.class);
        when(summaryDto.getId()).thenReturn(1L);

        when(seanceRepository.findById(eq(1L))).thenReturn(Optional.of(seance));
        when(userRepository.findByUsername(eq("testuser"))).thenReturn(Optional.of(user));
        when(seatRepository.findAllByIdInWithLock(eq(List.of(1L)))).thenReturn(List.of(seat));
        when(ticketRepository.findAllTakenTickets(eq(1L), any(LocalDateTime.class))).thenReturn(new ArrayList<>());
        when(ticketMapper.toEntity(any(Reservation.class), eq(seance), eq(seat), eq(TicketType.REGULAR), eq(15.0)))
                .thenReturn(ticket);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);
        when(reservationMapper.toSummaryDto(eq(reservation))).thenReturn(summaryDto);

        // Act
        ReservationSummaryDto result = reservationService.createReservation(request, "testuser");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(seanceRepository, times(1)).findById(eq(1L));
        verify(userRepository, times(1)).findByUsername(eq("testuser"));
        verify(seatRepository, times(1)).findAllByIdInWithLock(eq(List.of(1L)));
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    void testCreateReservationThrowsExceptionWhenSeanceNotFound() {
        // Arrange
        CreateReservationDto.TicketRequest ticketRequest = new CreateReservationDto.TicketRequest();
        ticketRequest.setSeatId(1L);
        ticketRequest.setTicketType(TicketType.REGULAR);

        CreateReservationDto request = new CreateReservationDto();
        request.setSeanceId(999L);
        request.setTickets(List.of(ticketRequest));

        when(seanceRepository.findById(eq(999L))).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> reservationService.createReservation(request, "testuser"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Seance not found");

        verify(seanceRepository, times(1)).findById(eq(999L));
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void testCreateReservationThrowsExceptionWhenUserNotFound() {
        // Arrange
        CreateReservationDto.TicketRequest ticketRequest = new CreateReservationDto.TicketRequest();
        ticketRequest.setSeatId(1L);
        ticketRequest.setTicketType(TicketType.REGULAR);

        CreateReservationDto request = new CreateReservationDto();
        request.setSeanceId(1L);
        request.setTickets(List.of(ticketRequest));

        when(seanceRepository.findById(eq(1L))).thenReturn(Optional.of(seance));
        when(userRepository.findByUsername(eq("nonexistent"))).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> reservationService.createReservation(request, "nonexistent"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");

        verify(userRepository, times(1)).findByUsername(eq("nonexistent"));
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void testCreateReservationThrowsExceptionWhenSeatAlreadyTaken() {
        // Arrange
        CreateReservationDto.TicketRequest ticketRequest = new CreateReservationDto.TicketRequest();
        ticketRequest.setSeatId(1L);
        ticketRequest.setTicketType(TicketType.REGULAR);

        CreateReservationDto request = new CreateReservationDto();
        request.setSeanceId(1L);
        request.setTickets(List.of(ticketRequest));

        when(seanceRepository.findById(eq(1L))).thenReturn(Optional.of(seance));
        when(userRepository.findByUsername(eq("testuser"))).thenReturn(Optional.of(user));
        when(seatRepository.findAllByIdInWithLock(eq(List.of(1L)))).thenReturn(List.of(seat));
        when(ticketRepository.findAllTakenTickets(eq(1L), any(LocalDateTime.class))).thenReturn(List.of(ticket));

        // Act & Assert
        assertThatThrownBy(() -> reservationService.createReservation(request, "testuser"))
                .isInstanceOf(SeatAlreadyOccupiedException.class);

        verify(reservationRepository, never()).save(any());
    }

    @Test
    void testCreateReservationThrowsExceptionWhenInvalidSeatIds() {
        // Arrange
        CreateReservationDto.TicketRequest ticketRequest = new CreateReservationDto.TicketRequest();
        ticketRequest.setSeatId(999L);
        ticketRequest.setTicketType(TicketType.REGULAR);

        CreateReservationDto request = new CreateReservationDto();
        request.setSeanceId(1L);
        request.setTickets(List.of(ticketRequest));

        when(seanceRepository.findById(eq(1L))).thenReturn(Optional.of(seance));
        when(userRepository.findByUsername(eq("testuser"))).thenReturn(Optional.of(user));
        when(seatRepository.findAllByIdInWithLock(eq(List.of(999L)))).thenReturn(new ArrayList<>());

        // Act & Assert
        assertThatThrownBy(() -> reservationService.createReservation(request, "testuser"))
                .isInstanceOf(InvalidReservationActionException.class);

        verify(reservationRepository, never()).save(any());
    }

    @Test
    void testPayForReservationSuccessfully() {
        // Arrange
        when(reservationRepository.findById(eq(1L))).thenReturn(Optional.of(reservation));

        // Act
        reservationService.payForReservation(1L);

        // Assert
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.PAID);
        assertThat(reservation.getReservationCode()).isNotNull();
        assertThat(ticket.getTicketCode()).isNotNull();
        verify(reservationRepository, times(1)).findById(eq(1L));
        verify(reservationRepository, times(1)).save(eq(reservation));
    }

    @Test
    void testPayForReservationThrowsExceptionWhenNotFound() {
        // Arrange
        when(reservationRepository.findById(eq(999L))).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> reservationService.payForReservation(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Reservation not found");

        verify(reservationRepository, times(1)).findById(eq(999L));
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void testPayForReservationThrowsExceptionWhenNotPending() {
        // Arrange
        reservation.setStatus(ReservationStatus.PAID);
        when(reservationRepository.findById(eq(1L))).thenReturn(Optional.of(reservation));

        // Act & Assert
        assertThatThrownBy(() -> reservationService.payForReservation(1L))
                .isInstanceOf(InvalidReservationActionException.class)
                .hasMessageContaining("cannot be paid");

        verify(reservationRepository, never()).save(any());
    }

    @Test
    void testPayForReservationThrowsExceptionWhenExpired() {
        // Arrange
        reservation.setExpiresAt(LocalDateTime.now().minusMinutes(1));
        when(reservationRepository.findById(eq(1L))).thenReturn(Optional.of(reservation));

        // Act & Assert
        assertThatThrownBy(() -> reservationService.payForReservation(1L))
                .isInstanceOf(InvalidReservationActionException.class)
                .hasMessageContaining("expired");

        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
        verify(reservationRepository, times(1)).save(eq(reservation));
    }

    @Test
    void testGetUserReservationsSuccessfully() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Reservation> page = new PageImpl<>(List.of(reservation), pageable, 1);

        ReservationSummaryDto summaryDto = mock(ReservationSummaryDto.class);

        when(userRepository.findByUsername(eq("testuser"))).thenReturn(Optional.of(user));
        when(reservationRepository.findAllByUser(eq(user), any(Pageable.class))).thenReturn(page);
        when(reservationMapper.toSummaryDto(eq(reservation))).thenReturn(summaryDto);

        // Act
        Page<ReservationSummaryDto> result = reservationService.getUserReservations("testuser", 0, 10, null);

        // Assert
        assertThat(result).isNotEmpty();
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(userRepository, times(1)).findByUsername(eq("testuser"));
        verify(reservationRepository, times(1)).findAllByUser(eq(user), any(Pageable.class));
    }

    @Test
    void testGetUserReservationsWithStatus() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Reservation> page = new PageImpl<>(List.of(reservation), pageable, 1);

        ReservationSummaryDto summaryDto = mock(ReservationSummaryDto.class);

        when(userRepository.findByUsername(eq("testuser"))).thenReturn(Optional.of(user));
        when(reservationRepository.findAllByUserAndStatus(eq(user), eq(ReservationStatus.PENDING), any(Pageable.class)))
                .thenReturn(page);
        when(reservationMapper.toSummaryDto(eq(reservation))).thenReturn(summaryDto);

        // Act
        Page<ReservationSummaryDto> result = reservationService.getUserReservations("testuser", 0, 10, ReservationStatus.PENDING);

        // Assert
        assertThat(result).isNotEmpty();
        verify(reservationRepository, times(1)).findAllByUserAndStatus(eq(user), eq(ReservationStatus.PENDING), any(Pageable.class));
    }


    @Test
    void testGetUserReservationsThrowsExceptionWhenUserNotFound() {
        // Arrange
        when(userRepository.findByUsername(eq("nonexistent"))).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> reservationService.getUserReservations("nonexistent", 0, 10, null))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");

        verify(userRepository, times(1)).findByUsername(eq("nonexistent"));
    }

    @Test
    void testRemoveTicketSuccessfully() {
        // Arrange
        Ticket ticket2 = new Ticket();
        ticket2.setId(2L);
        ticket2.setPrice(10.0);
        reservation.getTickets().add(ticket2);

        ReservationSummaryDto summaryDto = mock(ReservationSummaryDto.class);

        when(reservationRepository.findById(eq(1L))).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);
        when(reservationMapper.toSummaryDto(eq(reservation))).thenReturn(summaryDto);

        // Act
        ReservationSummaryDto result = reservationService.removeTicket(1L, 1L);

        // Assert
        assertThat(result).isNotNull();
        verify(ticketRepository, times(1)).delete(eq(ticket));
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }


    @Test
    void testRemoveTicketThrowsExceptionWhenReservationNotFound() {
        // Arrange
        when(reservationRepository.findById(eq(999L))).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> reservationService.removeTicket(999L, 1L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(ticketRepository, never()).delete(any());
    }

    @Test
    void testRemoveTicketThrowsExceptionWhenReservationNotPending() {
        // Arrange
        reservation.setStatus(ReservationStatus.PAID);
        when(reservationRepository.findById(eq(1L))).thenReturn(Optional.of(reservation));

        // Act & Assert
        assertThatThrownBy(() -> reservationService.removeTicket(1L, 1L))
                .isInstanceOf(InvalidReservationActionException.class);

        verify(ticketRepository, never()).delete(any());
    }

    @Test
    void testRemoveTicketThrowsExceptionWhenTicketNotFound() {
        // Arrange
        when(reservationRepository.findById(eq(1L))).thenReturn(Optional.of(reservation));

        // Act & Assert
        assertThatThrownBy(() -> reservationService.removeTicket(1L, 999L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(ticketRepository, never()).delete(any());
    }

    @Test
    void testRemoveTicketCancelsReservationWhenNoTicketsRemain() {
        // Arrange
        ReservationSummaryDto summaryDto = mock(ReservationSummaryDto.class);

        when(reservationRepository.findById(eq(1L))).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);
        when(reservationMapper.toSummaryDto(eq(reservation))).thenReturn(summaryDto);

        // Act
        reservationService.removeTicket(1L, 1L);

        // Assert
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
        verify(reservationRepository, times(2)).save(any(Reservation.class));
    }


    @Test
    void testUpdateTicketTypeSuccessfully() {
        // Arrange
        when(reservationRepository.findById(eq(1L))).thenReturn(Optional.of(reservation));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        ReservationSummaryDto summaryDto = mock(ReservationSummaryDto.class);
        when(reservationMapper.toSummaryDto(eq(reservation))).thenReturn(summaryDto);

        // Act
        ReservationSummaryDto result = reservationService.updateTicketType(1L, 1L, TicketType.REDUCED);

        // Assert
        assertThat(result).isNotNull();
        assertThat(ticket.getTicketType()).isEqualTo(TicketType.REDUCED);
        assertThat(ticket.getPrice()).isEqualTo(10.0);
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }


    @Test
    void testUpdateTicketTypeThrowsExceptionWhenReservationNotFound() {
        // Arrange
        when(reservationRepository.findById(eq(999L))).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> reservationService.updateTicketType(999L, 1L, TicketType.REDUCED))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(ticketRepository, never()).save(any());
    }

    @Test
    void testUpdateTicketTypeThrowsExceptionWhenNotPending() {
        // Arrange
        reservation.setStatus(ReservationStatus.PAID);
        when(reservationRepository.findById(eq(1L))).thenReturn(Optional.of(reservation));

        // Act & Assert
        assertThatThrownBy(() -> reservationService.updateTicketType(1L, 1L, TicketType.REDUCED))
                .isInstanceOf(InvalidReservationActionException.class);

        verify(ticketRepository, never()).save(any());
    }

    @Test
    void testCancelReservationSuccessfully() {
        // Arrange
        when(reservationRepository.findById(eq(1L))).thenReturn(Optional.of(reservation));

        // Act
        reservationService.cancelReservation(1L);

        // Assert
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
        verify(reservationRepository, times(1)).save(eq(reservation));
    }

    @Test
    void testCancelReservationThrowsExceptionWhenNotFound() {
        // Arrange
        when(reservationRepository.findById(eq(999L))).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> reservationService.cancelReservation(999L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(reservationRepository, never()).save(any());
    }

    @Test
    void testCancelReservationThrowsExceptionWhenAlreadyPaid() {
        // Arrange
        reservation.setStatus(ReservationStatus.PAID);
        when(reservationRepository.findById(eq(1L))).thenReturn(Optional.of(reservation));

        // Act & Assert
        assertThatThrownBy(() -> reservationService.cancelReservation(1L))
                .isInstanceOf(InvalidReservationActionException.class);

        verify(reservationRepository, never()).save(any());
    }

    @Test
    void testGetReservationDetailsSuccessfully() {
        // Arrange
        when(reservationRepository.findById(eq(1L))).thenReturn(Optional.of(reservation));

        // Act
        Reservation result = reservationService.getReservationDetails(1L, "testuser");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(reservationRepository, times(1)).findById(eq(1L));
    }

    @Test
    void testGetReservationDetailsThrowsExceptionWhenNotFound() {
        // Arrange
        when(reservationRepository.findById(eq(999L))).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> reservationService.getReservationDetails(999L, "testuser"))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(reservationRepository, times(1)).findById(eq(999L));
    }

    @Test
    void testGetReservationDetailsThrowsExceptionWhenUnauthorized() {
        // Arrange
        when(reservationRepository.findById(eq(1L))).thenReturn(Optional.of(reservation));

        // Act & Assert
        assertThatThrownBy(() -> reservationService.getReservationDetails(1L, "otheruser"))
                .isInstanceOf(SecurityException.class);

        verify(reservationRepository, times(1)).findById(eq(1L));
    }

    @Test
    void testAddTicketToReservationSuccessfully() {
        // Arrange
        Seat seat2 = new Seat();
        seat2.setId(2L);
        seat2.setCinemaRoom(cinemaRoom);

        Ticket newTicket = new Ticket();
        newTicket.setId(2L);
        newTicket.setReservation(reservation);
        newTicket.setSeance(seance);
        newTicket.setSeat(seat2);
        newTicket.setTicketType(TicketType.REGULAR);
        newTicket.setPrice(15.0);

        ReservationSummaryDto summaryDto = mock(ReservationSummaryDto.class);

        when(reservationRepository.findById(eq(1L))).thenReturn(Optional.of(reservation));
        when(seatRepository.findAllByIdInWithLock(eq(List.of(2L)))).thenReturn(List.of(seat2));
        when(ticketRepository.findAllTakenTickets(eq(1L), any(LocalDateTime.class))).thenReturn(new ArrayList<>());
        when(ticketMapper.toEntity(eq(reservation), eq(seance), eq(seat2), eq(TicketType.REGULAR), eq(15.0)))
                .thenReturn(newTicket);
        when(ticketRepository.save(any(Ticket.class))).thenReturn(newTicket);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);
        when(reservationMapper.toSummaryDto(eq(reservation))).thenReturn(summaryDto);

        // Act
        ReservationSummaryDto result = reservationService.addTicketToReservation(1L, 2L);

        // Assert
        assertThat(result).isNotNull();
        verify(ticketRepository, times(1)).save(any(Ticket.class));
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }


    @Test
    void testAddTicketToReservationThrowsExceptionWhenReservationNotFound() {
        // Arrange
        when(reservationRepository.findById(eq(999L))).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> reservationService.addTicketToReservation(999L, 1L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(ticketRepository, never()).save(any());
    }

    @Test
    void testAddTicketToReservationThrowsExceptionWhenNotPending() {
        // Arrange
        reservation.setStatus(ReservationStatus.PAID);
        when(reservationRepository.findById(eq(1L))).thenReturn(Optional.of(reservation));

        // Act & Assert
        assertThatThrownBy(() -> reservationService.addTicketToReservation(1L, 2L))
                .isInstanceOf(InvalidReservationActionException.class);

        verify(ticketRepository, never()).save(any());
    }

    @Test
    void testAddTicketToReservationThrowsExceptionWhenSeatNotFound() {
        // Arrange
        when(reservationRepository.findById(eq(1L))).thenReturn(Optional.of(reservation));
        when(seatRepository.findAllByIdInWithLock(eq(List.of(999L)))).thenReturn(new ArrayList<>());

        // Act & Assert
        assertThatThrownBy(() -> reservationService.addTicketToReservation(1L, 999L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(ticketRepository, never()).save(any());
    }

    @Test
    void testAddTicketToReservationThrowsExceptionWhenSeatTaken() {
        // Arrange
        Seat seat2 = new Seat();
        seat2.setId(2L);

        Ticket takenTicket = new Ticket();
        takenTicket.setId(2L);
        takenTicket.setSeat(seat2);

        when(reservationRepository.findById(eq(1L))).thenReturn(Optional.of(reservation));
        when(seatRepository.findAllByIdInWithLock(eq(List.of(2L)))).thenReturn(List.of(seat2));
        when(ticketRepository.findAllTakenTickets(eq(1L), any(LocalDateTime.class))).thenReturn(List.of(takenTicket));

        // Act & Assert
        assertThatThrownBy(() -> reservationService.addTicketToReservation(1L, 2L))
                .isInstanceOf(SeatAlreadyOccupiedException.class);

        verify(ticketRepository, never()).save(any());
    }

    @Test
    void testGeneratePdfForReservationSuccessfully() {
        // Arrange
        reservation.setStatus(ReservationStatus.PAID);
        ByteArrayInputStream pdfStream = new ByteArrayInputStream(new byte[]{});

        when(reservationRepository.findById(eq(1L))).thenReturn(Optional.of(reservation));
        when(pdfTicketService.generateReservationPdf(eq(reservation))).thenReturn(pdfStream);

        // Act
        ByteArrayInputStream result = reservationService.generatePdfForReservation(1L, "testuser");

        // Assert
        assertThat(result).isNotNull();
        verify(pdfTicketService, times(1)).generateReservationPdf(eq(reservation));
    }

    @Test
    void testGeneratePdfForReservationThrowsExceptionWhenUnpaid() {
        // Arrange
        when(reservationRepository.findById(eq(1L))).thenReturn(Optional.of(reservation));

        // Act & Assert
        assertThatThrownBy(() -> reservationService.generatePdfForReservation(1L, "testuser"))
                .isInstanceOf(InvalidReservationActionException.class);

        verify(pdfTicketService, never()).generateReservationPdf(any());
    }

    @Test
    void testAutoCancelExpiredReservationsSuccessfully() {
        // Arrange
        Reservation expiredReservation = new Reservation();
        expiredReservation.setId(2L);
        expiredReservation.setStatus(ReservationStatus.PENDING);
        expiredReservation.setExpiresAt(LocalDateTime.now().minusMinutes(1));

        when(reservationRepository.findAllByStatusAndExpiresAtBefore(eq(ReservationStatus.PENDING), any(LocalDateTime.class)))
                .thenReturn(List.of(expiredReservation));

        // Act
        reservationService.autoCancelExpiredReservations();

        // Assert
        assertThat(expiredReservation.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
        verify(reservationRepository, times(1)).saveAll(any());
    }

    @Test
    void testAutoCancelExpiredReservationsWithNoExpiredReservations() {
        // Arrange
        when(reservationRepository.findAllByStatusAndExpiresAtBefore(eq(ReservationStatus.PENDING), any(LocalDateTime.class)))
                .thenReturn(new ArrayList<>());

        // Act
        reservationService.autoCancelExpiredReservations();

        // Assert
        verify(reservationRepository, never()).saveAll(any());
    }
}
