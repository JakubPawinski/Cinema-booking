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
        cinemaRoom.setName("Room A");

        seance = new Seance();
        seance.setId(1L);
        seance.setMovie(movie);
        seance.setCinemaRoom(cinemaRoom);
        seance.setStartTime(LocalDateTime.of(2024, 5, 1, 18, 0));
        seance.setEndTime(LocalDateTime.of(2024, 5, 1, 20, 28));
        seance.setRegularTicketPrice(25.0);
        seance.setReducedTicketPrice(15.0);

        seanceRequestDto = new SeanceRequestDto();
        seanceRequestDto.setMovieId(1L);
        seanceRequestDto.setRoomId(1L);
        seanceRequestDto.setStartTime(LocalDateTime.of(2024, 5, 1, 18, 0));
        seanceRequestDto.setRegularTicketPrice(25.0);
        seanceRequestDto.setReducedTicketPrice(15.0);

        seat = new Seat();
        seat.setId(1L);
        seat.setCinemaRoom(cinemaRoom);
        seat.setRowNumber(1);
        seat.setSeatNumber(1);

        ticket = new Ticket();
        ticket.setId(1L);
        ticket.setSeance(seance);
        ticket.setSeat(seat);
    }

    private SeanceDto createSeanceDto() {
        return SeanceDto.builder()
                .id(1L)
                .movieId(1L)
                .movieTitle("Inception")
                .roomName("Room A")
                .startTime(LocalDateTime.of(2024, 5, 1, 18, 0))
                .endTime(LocalDateTime.of(2024, 5, 1, 20, 28))
                .regularTicketPrice(25.0)
                .reducedTicketPrice(15.0)
                .build();
    }

    private SeanceDto createSeanceDto(Long id, LocalDateTime startTime, LocalDateTime endTime) {
        return SeanceDto.builder()
                .id(id)
                .movieId(1L)
                .movieTitle("Inception")
                .roomName("Room A")
                .startTime(startTime)
                .endTime(endTime)
                .regularTicketPrice(25.0)
                .reducedTicketPrice(15.0)
                .build();
    }

    private MovieWithSeancesDto createMovieWithSeancesDto() {
        SeanceDto seanceDto = createSeanceDto();
        return MovieWithSeancesDto.builder()
                .title("Inception")
                .genre("Sci-Fi")
                .durationMin(148)
                .seances(List.of(seanceDto))
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
        MovieWithSeancesDto movieWithSeancesDto = createMovieWithSeancesDto();

        when(seanceRepository.findByStartTimeBetween(startOfDay, endOfDay))
                .thenReturn(List.of(seance));
        when(movieMapper.toMovieWithSeancesDto(eq(movie), any()))
                .thenReturn(movieWithSeancesDto);

        List<MovieWithSeancesDto> result = seanceService.getRepertoireForDate(date);

        assertThat(result)
                .isNotNull()
                .isNotEmpty()
                .contains(movieWithSeancesDto);
        verify(seanceRepository, times(1)).findByStartTimeBetween(startOfDay, endOfDay);
    }

    @Test
    void testGetRepertoireForDateReturnsEmptyList() {
        LocalDate date = LocalDate.of(2024, 5, 1);
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        when(seanceRepository.findByStartTimeBetween(startOfDay, endOfDay))
                .thenReturn(List.of());

        List<MovieWithSeancesDto> result = seanceService.getRepertoireForDate(date);

        assertThat(result)
                .isNotNull()
                .isEmpty();
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
        seance2.setCinemaRoom(cinemaRoom);
        seance2.setStartTime(LocalDateTime.of(2024, 5, 1, 20, 30));
        seance2.setEndTime(LocalDateTime.of(2024, 5, 1, 22, 58));

        List<Seance> seances = List.of(seance, seance2);
        SeanceDto seanceDto1 = createSeanceDto();
        SeanceDto seanceDto2 = createSeanceDto(2L, LocalDateTime.of(2024, 5, 1, 20, 30),
                LocalDateTime.of(2024, 5, 1, 22, 58));
        MovieWithSeancesDto movieWithSeancesDto = createMovieWithSeancesDto();

        when(seanceRepository.findByStartTimeBetween(startOfDay, endOfDay)).thenReturn(seances);
        when(seanceMapper.toDto(any(Seance.class))).thenReturn(seanceDto1, seanceDto2);
        when(movieMapper.toMovieWithSeancesDto(eq(movie), any())).thenReturn(movieWithSeancesDto);

        List<MovieWithSeancesDto> result = seanceService.getRepertoireForDate(date);

        assertThat(result)
                .isNotNull()
                .isNotEmpty();
        verify(seanceRepository, times(1)).findByStartTimeBetween(startOfDay, endOfDay);
    }

    @Test
    void testGetSeanceDetailsSuccessfully() {
        SeanceDto seanceDto = createSeanceDto();
        when(seanceRepository.findById(1L)).thenReturn(Optional.of(seance));
        when(seanceMapper.toDto(seance)).thenReturn(seanceDto);

        SeanceDto result = seanceService.getSeanceDetails(1L);

        assertThat(result)
                .isNotNull()
                .isEqualTo(seanceDto);
        verify(seanceRepository, times(1)).findById(1L);
        verify(seanceMapper, times(1)).toDto(seance);
    }

    @Test
    void testGetSeanceDetailsThrowsExceptionWhenNotFound() {
        when(seanceRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> seanceService.getSeanceDetails(999L))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(seanceRepository, times(1)).findById(999L);
        verify(seanceMapper, never()).toDto(any());
    }

    @Test
    void testGetSeanceDetailsReturnsCorrectSeance() {
        SeanceDto seanceDto = createSeanceDto();
        when(seanceRepository.findById(1L)).thenReturn(Optional.of(seance));
        when(seanceMapper.toDto(seance)).thenReturn(seanceDto);

        SeanceDto result = seanceService.getSeanceDetails(1L);

        assertThat(result)
                .isNotNull()
                .satisfies(dto -> assertThat(dto.getId()).isEqualTo(1L));
    }

    @Test
    void testGetSeatsStatusForMovieThrowsExceptionWhenSeanceNotFound() {
        when(seanceRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> seanceService.getSeatsStatusForMovie(999L))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(seanceRepository, times(1)).findById(999L);
        verify(seatRepository, never()).findAllByCinemaRoom_Id(any());
    }

    @Test
    void testGetSeatsStatusForMovieSuccessfully() {
        List<Seat> allSeats = List.of(seat);
        List<Ticket> takenTickets = List.of(ticket);
        SeatDto seatDto = createSeatDto(1L, 1, 1, true);

        when(seanceRepository.findById(1L)).thenReturn(Optional.of(seance));
        when(seatRepository.findAllByCinemaRoom_Id(1L)).thenReturn(allSeats);
        when(ticketRepository.findAllTakenTickets(eq(1L), any(LocalDateTime.class))).thenReturn(takenTickets);
        when(seatMapper.toDto(eq(seat), eq(true))).thenReturn(seatDto);

        List<SeatDto> result = seanceService.getSeatsStatusForMovie(1L);

        assertThat(result)
                .isNotNull()
                .isNotEmpty()
                .contains(seatDto);
        verify(seanceRepository, times(1)).findById(1L);
        verify(seatRepository, times(1)).findAllByCinemaRoom_Id(1L);
    }

    @Test
    void testGetSeatsStatusForMovieMarksTakenSeats() {
        List<Seat> allSeats = List.of(seat);
        List<Ticket> takenTickets = List.of(ticket);
        SeatDto seatDto = createSeatDto(1L, 1, 1, true);

        when(seanceRepository.findById(1L)).thenReturn(Optional.of(seance));
        when(seatRepository.findAllByCinemaRoom_Id(1L)).thenReturn(allSeats);
        when(ticketRepository.findAllTakenTickets(eq(1L), any(LocalDateTime.class))).thenReturn(takenTickets);
        when(seatMapper.toDto(eq(seat), eq(true))).thenReturn(seatDto);

        seanceService.getSeatsStatusForMovie(1L);

        assertThat(seatDto)
                .isNotNull()
                .satisfies(dto -> {
                    verify(seatMapper, times(1)).toDto(eq(seat), eq(true));
                });
    }

    @Test
    void testGetSeatsStatusForMovieMarksFreeSeats() {
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

        seanceService.getSeatsStatusForMovie(1L);

        assertThat(seatDto1)
                .isNotNull()
                .satisfies(dto -> {
                    verify(seatMapper, times(1)).toDto(eq(seat), eq(true));
                    verify(seatMapper, times(1)).toDto(eq(seat2), eq(false));
                });
    }

    @Test
    void testCreateSeanceSuccessfully() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(cinemaRoomRepository.findById(1L)).thenReturn(Optional.of(cinemaRoom));
        when(seanceRepository.findOverlappingSeances(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(new ArrayList<>());
        when(seanceMapper.toEntity(seanceRequestDto, movie, cinemaRoom)).thenReturn(seance);

        seanceService.createSeance(seanceRequestDto);

        assertThat(seance)
                .isNotNull();
        verify(movieRepository, times(1)).findById(1L);
        verify(cinemaRoomRepository, times(1)).findById(1L);
        verify(seanceRepository, times(1)).findOverlappingSeances(eq(1L), any(), any());
        verify(seanceRepository, times(1)).save(seance);
    }

    @Test
    void testCreateSeanceThrowsExceptionWhenMovieNotFound() {
        when(movieRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> seanceService.createSeance(seanceRequestDto))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(movieRepository, times(1)).findById(1L);
        verify(seanceRepository, never()).save(any());
    }

    @Test
    void testCreateSeanceThrowsExceptionWhenRoomNotFound() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(cinemaRoomRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> seanceService.createSeance(seanceRequestDto))
                .isInstanceOf(ResourceNotFoundException.class);
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
                .isInstanceOf(SeanceConflictException.class);
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

        assertThat(1L)
                .isNotNull()
                .satisfies(id -> verify(seanceRepository, times(1)).deleteById(id));
    }

    @Test
    void testDeleteSeanceWithValidId() {
        seanceService.deleteSeance(5L);

        assertThat(5L)
                .isNotNull()
                .satisfies(id -> verify(seanceRepository, times(1)).deleteById(id));
    }

    @Test
    void testGetAllSeancesSuccessfully() {
        List<Seance> seances = List.of(seance);
        SeanceDto seanceDto = createSeanceDto();

        when(seanceRepository.findAll(any(Sort.class))).thenReturn(seances);
        when(seanceMapper.toDto(seance)).thenReturn(seanceDto);

        List<SeanceDto> result = seanceService.getAllSeances();

        assertThat(result)
                .isNotNull()
                .isNotEmpty()
                .contains(seanceDto);
        verify(seanceRepository, times(1)).findAll(any(Sort.class));
    }

    @Test
    void testGetAllSeancesReturnsEmptyList() {
        when(seanceRepository.findAll(any(Sort.class))).thenReturn(new ArrayList<>());

        List<SeanceDto> result = seanceService.getAllSeances();

        assertThat(result)
                .isNotNull()
                .isEmpty();
        verify(seanceRepository, times(1)).findAll(any(Sort.class));
    }

    @Test
    void testGetAllSeancesSortsByStartTimeDescending() {
        List<Seance> seances = List.of(seance);
        SeanceDto seanceDto = createSeanceDto();

        when(seanceRepository.findAll(any(Sort.class))).thenReturn(seances);
        when(seanceMapper.toDto(seance)).thenReturn(seanceDto);

        seanceService.getAllSeances();

        assertThat(seanceDto)
                .isNotNull()
                .satisfies(dto -> verify(seanceRepository).findAll(Sort.by("startTime").descending()));
    }
}
