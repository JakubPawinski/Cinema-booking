package cinema.booking.cinemabooking.service;

import cinema.booking.cinemabooking.dto.request.SeanceRequestDto;
import cinema.booking.cinemabooking.dto.response.MovieWithSeancesDto;
import cinema.booking.cinemabooking.dto.response.SeanceDto;
import cinema.booking.cinemabooking.dto.response.SeatDto;
import cinema.booking.cinemabooking.exception.ResourceNotFoundException;
import cinema.booking.cinemabooking.exception.SeanceConflictException;
import cinema.booking.cinemabooking.mapper.MovieMapper;
import cinema.booking.cinemabooking.mapper.SeanceMapper;
import cinema.booking.cinemabooking.mapper.SeatMapper;
import cinema.booking.cinemabooking.model.*;
import cinema.booking.cinemabooking.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SeanceServiceTest {

    @Mock
    private SeanceRepository seanceRepository;

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private CinemaRoomRepository cinemaRoomRepository;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private SeanceMapper seanceMapper;

    @Mock
    private MovieMapper movieMapper;

    @Mock
    private SeatMapper seatMapper;

    @InjectMocks
    private SeanceService seanceService;

    private Movie movie;
    private CinemaRoom cinemaRoom;
    private Seance seance;
    private SeanceRequestDto seanceRequestDto;
    private Seat seat;
    private Ticket ticket;

    @BeforeEach
    void setUp() {
        movie = new Movie();
        movie.setId(1L);
        movie.setTitle("Inception");
        movie.setDurationMin(148);

        cinemaRoom = new CinemaRoom();
        cinemaRoom.setId(1L);
        cinemaRoom.setName("Hall A");

        seance = new Seance();
        seance.setId(1L);
        seance.setStartTime(LocalDateTime.of(2024, 5, 1, 18, 0));
        seance.setEndTime(LocalDateTime.of(2024, 5, 1, 20, 28));
        seance.setRegularTicketPrice(15.0);
        seance.setReducedTicketPrice(10.0);
        seance.setMovie(movie);
        seance.setCinemaRoom(cinemaRoom);

        seanceRequestDto = new SeanceRequestDto();
        seanceRequestDto.setMovieId(1L);
        seanceRequestDto.setRoomId(1L);
        seanceRequestDto.setStartTime(LocalDateTime.of(2024, 5, 1, 18, 0));
        seanceRequestDto.setRegularTicketPrice(15.0);
        seanceRequestDto.setReducedTicketPrice(10.0);

        seat = new Seat();
        seat.setId(1L);
        seat.setCinemaRoom(cinemaRoom);
        seat.setRowNumber(1);
        seat.setSeatNumber(1);

        ticket = new Ticket();
        ticket.setId(1L);
        ticket.setSeat(seat);
        ticket.setPrice(15.0);
    }

    private SeanceDto createSeanceDto() {
        return SeanceDto.builder()
                .id(1L)
                .startTime(LocalDateTime.of(2024, 5, 1, 18, 0))
                .endTime(LocalDateTime.of(2024, 5, 1, 20, 28))
                .regularTicketPrice(15.0)
                .reducedTicketPrice(10.0)
                .roomName("Hall A")
                .movieId(1L)
                .movieTitle("Inception")
                .build();
    }

    private SeanceDto createSeanceDto(Long id, LocalDateTime startTime, LocalDateTime endTime) {
        return SeanceDto.builder()
                .id(id)
                .startTime(startTime)
                .endTime(endTime)
                .regularTicketPrice(15.0)
                .reducedTicketPrice(10.0)
                .roomName("Hall A")
                .movieId(1L)
                .movieTitle("Inception")
                .build();
    }

    private MovieWithSeancesDto createMovieWithSeancesDto() {
        return MovieWithSeancesDto.builder()
                .movieId(1L)
                .title("Inception")
                .genre("Sci-Fi")
                .durationMin(148)
                .description("A mind-bending thriller")
                .imageUrl("https://example.com/inception.jpg")
                .seances(new ArrayList<>())
                .build();
    }

    private SeatDto createSeatDto(Long id, int rowNumber, int seatNumber, boolean isOccupied) {
        return SeatDto.builder()
                .id(id)
                .rowNumber(rowNumber)
                .seatNumber(seatNumber)
                .isOccupied(isOccupied)
                .build();
    }

    @Test
    void testGetRepertoireForDateSuccessfully() {
        LocalDate date = LocalDate.of(2024, 5, 1);
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<Seance> seances = List.of(seance);
        SeanceDto seanceDto = createSeanceDto();
        MovieWithSeancesDto movieWithSeancesDto = createMovieWithSeancesDto();

        when(seanceRepository.findByStartTimeBetween(startOfDay, endOfDay)).thenReturn(seances);
        when(seanceMapper.toDto(seance)).thenReturn(seanceDto);
        when(movieMapper.toMovieWithSeancesDto(movie, List.of(seanceDto))).thenReturn(movieWithSeancesDto);

        List<MovieWithSeancesDto> result = seanceService.getRepertoireForDate(date);

        assertThat(result).isNotEmpty();
        assertThat(result).contains(movieWithSeancesDto);
        verify(seanceRepository, times(1)).findByStartTimeBetween(startOfDay, endOfDay);
    }

    @Test
    void testGetRepertoireForDateReturnsEmptyList() {
        LocalDate date = LocalDate.of(2024, 5, 1);
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        when(seanceRepository.findByStartTimeBetween(startOfDay, endOfDay)).thenReturn(new ArrayList<>());

        List<MovieWithSeancesDto> result = seanceService.getRepertoireForDate(date);

        assertThat(result).isEmpty();
        verify(seanceRepository, times(1)).findByStartTimeBetween(startOfDay, endOfDay);
    }

    @Test
    void testGetRepertoireForDateGroupsByMovie() {
        LocalDate date = LocalDate.of(2024, 5, 1);
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        Seance seance2 = new Seance();
        seance2.setId(2L);
        seance2.setMovie(movie);
        seance2.setStartTime(LocalDateTime.of(2024, 5, 1, 20, 0));
        seance2.setEndTime(LocalDateTime.of(2024, 5, 1, 22, 28));
        seance2.setRegularTicketPrice(15.0);
        seance2.setReducedTicketPrice(10.0);
        seance2.setCinemaRoom(cinemaRoom);

        List<Seance> seances = List.of(seance, seance2);
        SeanceDto seanceDto1 = createSeanceDto(1L, LocalDateTime.of(2024, 5, 1, 18, 0), LocalDateTime.of(2024, 5, 1, 20, 28));
        SeanceDto seanceDto2 = createSeanceDto(2L, LocalDateTime.of(2024, 5, 1, 20, 0), LocalDateTime.of(2024, 5, 1, 22, 28));
        MovieWithSeancesDto movieWithSeancesDto = createMovieWithSeancesDto();

        when(seanceRepository.findByStartTimeBetween(startOfDay, endOfDay)).thenReturn(seances);
        when(seanceMapper.toDto(any(Seance.class))).thenReturn(seanceDto1, seanceDto2);
        when(movieMapper.toMovieWithSeancesDto(eq(movie), any())).thenReturn(movieWithSeancesDto);

        List<MovieWithSeancesDto> result = seanceService.getRepertoireForDate(date);

        assertThat(result).isNotEmpty();
        verify(seanceRepository, times(1)).findByStartTimeBetween(startOfDay, endOfDay);
    }

    @Test
    void testGetSeanceDetailsSuccessfully() {
        SeanceDto seanceDto = createSeanceDto();
        when(seanceRepository.findById(1L)).thenReturn(Optional.of(seance));
        when(seanceMapper.toDto(seance)).thenReturn(seanceDto);

        SeanceDto result = seanceService.getSeanceDetails(1L);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(seanceDto);
        verify(seanceRepository, times(1)).findById(1L);
        verify(seanceMapper, times(1)).toDto(seance);
    }

    @Test
    void testGetSeanceDetailsThrowsExceptionWhenNotFound() {
        when(seanceRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> seanceService.getSeanceDetails(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Seance not found");

        verify(seanceRepository, times(1)).findById(999L);
        verify(seanceMapper, never()).toDto(any());
    }

    @Test
    void testGetSeanceDetailsReturnsCorrectSeance() {
        SeanceDto seanceDto = createSeanceDto();
        when(seanceRepository.findById(1L)).thenReturn(Optional.of(seance));
        when(seanceMapper.toDto(seance)).thenReturn(seanceDto);

        SeanceDto result = seanceService.getSeanceDetails(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }



    @Test
    void testGetSeatsStatusForMovieThrowsExceptionWhenSeanceNotFound() {
        when(seanceRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> seanceService.getSeatsStatusForMovie(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Seance not found");

        verify(seanceRepository, times(1)).findById(999L);
        verify(seatRepository, never()).findAllByCinemaRoom_Id(any());
    }

    @Test
    void testGetSeatsStatusForMovieSuccessfully() {
        // Arrange
        List<Seat> allSeats = List.of(seat);
        List<Ticket> takenTickets = List.of(ticket);
        SeatDto seatDto = createSeatDto(1L, 1, 1, true);

        when(seanceRepository.findById(1L)).thenReturn(Optional.of(seance));
        when(seatRepository.findAllByCinemaRoom_Id(1L)).thenReturn(allSeats);
        when(ticketRepository.findAllTakenTickets(eq(1L), any(LocalDateTime.class))).thenReturn(takenTickets);
        when(seatMapper.toDto(eq(seat), eq(true))).thenReturn(seatDto);

        // Act
        List<SeatDto> result = seanceService.getSeatsStatusForMovie(1L);

        // Assert
        assertThat(result).isNotEmpty();
        assertThat(result).contains(seatDto);
        verify(seanceRepository, times(1)).findById(1L);
        verify(seatRepository, times(1)).findAllByCinemaRoom_Id(1L);
    }

    @Test
    void testGetSeatsStatusForMovieMarksTakenSeats() {
        // Arrange
        List<Seat> allSeats = List.of(seat);
        List<Ticket> takenTickets = List.of(ticket);
        SeatDto seatDto = createSeatDto(1L, 1, 1, true);

        when(seanceRepository.findById(1L)).thenReturn(Optional.of(seance));
        when(seatRepository.findAllByCinemaRoom_Id(1L)).thenReturn(allSeats);
        when(ticketRepository.findAllTakenTickets(eq(1L), any(LocalDateTime.class))).thenReturn(takenTickets);
        when(seatMapper.toDto(eq(seat), eq(true))).thenReturn(seatDto);

        // Act
        seanceService.getSeatsStatusForMovie(1L);

        // Assert
        verify(seatMapper, times(1)).toDto(eq(seat), eq(true));
    }

    @Test
    void testGetSeatsStatusForMovieMarksFreeSeats() {
        // Arrange
        Seat seat2 = new Seat();
        seat2.setId(2L);
        seat2.setCinemaRoom(cinemaRoom);
        seat2.setRowNumber(1);
        seat2.setSeatNumber(2);

        List<Seat> allSeats = List.of(seat, seat2);
        List<Ticket> takenTickets = List.of(ticket);
        SeatDto seatDto1 = createSeatDto(1L, 1, 1, true);
        SeatDto seatDto2 = createSeatDto(2L, 1, 2, false);

        when(seanceRepository.findById(1L)).thenReturn(Optional.of(seance));
        when(seatRepository.findAllByCinemaRoom_Id(1L)).thenReturn(allSeats);
        when(ticketRepository.findAllTakenTickets(eq(1L), any(LocalDateTime.class))).thenReturn(takenTickets);
        when(seatMapper.toDto(eq(seat), eq(true))).thenReturn(seatDto1);
        when(seatMapper.toDto(eq(seat2), eq(false))).thenReturn(seatDto2);

        // Act
        seanceService.getSeatsStatusForMovie(1L);

        // Assert
        verify(seatMapper, times(1)).toDto(eq(seat), eq(true));
        verify(seatMapper, times(1)).toDto(eq(seat2), eq(false));
    }


    @Test
    void testCreateSeanceSuccessfully() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(cinemaRoomRepository.findById(1L)).thenReturn(Optional.of(cinemaRoom));
        when(seanceRepository.findOverlappingSeances(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(new ArrayList<>());
        when(seanceMapper.toEntity(seanceRequestDto, movie, cinemaRoom)).thenReturn(seance);

        seanceService.createSeance(seanceRequestDto);

        verify(movieRepository, times(1)).findById(1L);
        verify(cinemaRoomRepository, times(1)).findById(1L);
        verify(seanceRepository, times(1)).findOverlappingSeances(eq(1L), any(), any());
        verify(seanceRepository, times(1)).save(seance);
    }

    @Test
    void testCreateSeanceThrowsExceptionWhenMovieNotFound() {
        when(movieRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> seanceService.createSeance(seanceRequestDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Movie does not exist");

        verify(movieRepository, times(1)).findById(1L);
        verify(seanceRepository, never()).save(any());
    }

    @Test
    void testCreateSeanceThrowsExceptionWhenRoomNotFound() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(cinemaRoomRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> seanceService.createSeance(seanceRequestDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Cinema room does not exist");

        verify(movieRepository, times(1)).findById(1L);
        verify(cinemaRoomRepository, times(1)).findById(1L);
        verify(seanceRepository, never()).save(any());
    }

    @Test
    void testCreateSeanceThrowsExceptionWhenOverlaps() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(cinemaRoomRepository.findById(1L)).thenReturn(Optional.of(cinemaRoom));
        when(seanceRepository.findOverlappingSeances(any(), any(), any()))
                .thenReturn(List.of(seance));

        assertThatThrownBy(() -> seanceService.createSeance(seanceRequestDto))
                .isInstanceOf(SeanceConflictException.class)
                .hasMessage("Seance overlaps with existing seance in the same room");

        verify(seanceRepository, never()).save(any());
    }

    @Test
    void testCreateSeanceCalculatesBusyTimeCorrectly() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(cinemaRoomRepository.findById(1L)).thenReturn(Optional.of(cinemaRoom));
        when(seanceRepository.findOverlappingSeances(any(), any(), any()))
                .thenReturn(new ArrayList<>());
        when(seanceMapper.toEntity(seanceRequestDto, movie, cinemaRoom)).thenReturn(seance);

        seanceService.createSeance(seanceRequestDto);

        LocalDateTime expectedBusyUntil = seanceRequestDto.getStartTime()
                .plusMinutes(movie.getDurationMin() + 20);

        verify(seanceRepository).findOverlappingSeances(1L, seanceRequestDto.getStartTime(), expectedBusyUntil);
    }

    @Test
    void testDeleteSeanceSuccessfully() {
        seanceService.deleteSeance(1L);

        verify(seanceRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteSeanceWithValidId() {
        seanceService.deleteSeance(5L);

        verify(seanceRepository, times(1)).deleteById(5L);
    }

    @Test
    void testGetAllSeancesSuccessfully() {
        List<Seance> seances = List.of(seance);
        SeanceDto seanceDto = createSeanceDto();

        when(seanceRepository.findAll(any(Sort.class))).thenReturn(seances);
        when(seanceMapper.toDto(seance)).thenReturn(seanceDto);

        List<SeanceDto> result = seanceService.getAllSeances();

        assertThat(result).isNotEmpty();
        assertThat(result).contains(seanceDto);
        verify(seanceRepository, times(1)).findAll(any(Sort.class));
    }

    @Test
    void testGetAllSeancesReturnsEmptyList() {
        when(seanceRepository.findAll(any(Sort.class))).thenReturn(new ArrayList<>());

        List<SeanceDto> result = seanceService.getAllSeances();

        assertThat(result).isEmpty();
        verify(seanceRepository, times(1)).findAll(any(Sort.class));
    }

    @Test
    void testGetAllSeancesSortsByStartTimeDescending() {
        List<Seance> seances = List.of(seance);
        SeanceDto seanceDto = createSeanceDto();

        when(seanceRepository.findAll(any(Sort.class))).thenReturn(seances);
        when(seanceMapper.toDto(seance)).thenReturn(seanceDto);

        seanceService.getAllSeances();

        verify(seanceRepository).findAll(Sort.by("startTime").descending());
    }
}
