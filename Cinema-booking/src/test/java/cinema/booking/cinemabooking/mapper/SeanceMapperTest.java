package cinema.booking.cinemabooking.mapper;

import cinema.booking.cinemabooking.dto.request.SeanceRequestDto;
import cinema.booking.cinemabooking.dto.response.SeanceDto;
import cinema.booking.cinemabooking.model.CinemaRoom;
import cinema.booking.cinemabooking.model.Movie;
import cinema.booking.cinemabooking.model.Seance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for SeanceMapper.
 */
public class SeanceMapperTest {

    private SeanceMapper seanceMapper;
    private Seance seance;
    private Movie movie;
    private CinemaRoom cinemaRoom;

    @BeforeEach
    void setUp() {
        seanceMapper = new SeanceMapper();

        movie = new Movie();
        movie.setId(1L);
        movie.setTitle("Inception");
        movie.setDurationMin(148);

        cinemaRoom = new CinemaRoom();
        cinemaRoom.setId(1L);
        cinemaRoom.setName("Screen A");

        seance = new Seance();
        seance.setId(1L);
        seance.setStartTime(LocalDateTime.of(2025, 3, 15, 19, 0));
        seance.setEndTime(LocalDateTime.of(2025, 3, 15, 21, 28));
        seance.setRegularTicketPrice(15.0);
        seance.setReducedTicketPrice(10.0);
        seance.setMovie(movie);
        seance.setCinemaRoom(cinemaRoom);
    }

    @Test
    void testToDtoReturnsNotNull() {
        SeanceDto result = seanceMapper.toDto(seance);
        assertThat(result).isNotNull();
    }

    @Test
    void testToDtoPreservesId() {
        SeanceDto result = seanceMapper.toDto(seance);
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void testToDtoPreservesStartTime() {
        SeanceDto result = seanceMapper.toDto(seance);
        assertThat(result.getStartTime()).isEqualTo(LocalDateTime.of(2025, 3, 15, 19, 0));
    }

    @Test
    void testToDtoPreservesEndTime() {
        SeanceDto result = seanceMapper.toDto(seance);
        assertThat(result.getEndTime()).isEqualTo(LocalDateTime.of(2025, 3, 15, 21, 28));
    }

    @Test
    void testToDtoPreservesRegularTicketPrice() {
        SeanceDto result = seanceMapper.toDto(seance);
        assertThat(result.getRegularTicketPrice()).isEqualTo(15.0);
    }

    @Test
    void testToDtoPreservesReducedTicketPrice() {
        SeanceDto result = seanceMapper.toDto(seance);
        assertThat(result.getReducedTicketPrice()).isEqualTo(10.0);
    }

    @Test
    void testToDtoPreservesRoomName() {
        SeanceDto result = seanceMapper.toDto(seance);
        assertThat(result.getRoomName()).isEqualTo("Screen A");
    }

    @Test
    void testToDtoPreservesMovieId() {
        SeanceDto result = seanceMapper.toDto(seance);
        assertThat(result.getMovieId()).isEqualTo(1L);
    }

    @Test
    void testToDtoPreservesMovieTitle() {
        SeanceDto result = seanceMapper.toDto(seance);
        assertThat(result.getMovieTitle()).isEqualTo("Inception");
    }

    @Test
    void testToEntityReturnsNotNull() {
        SeanceRequestDto requestDto = new SeanceRequestDto();
        requestDto.setStartTime(LocalDateTime.of(2025, 3, 15, 19, 0));
        requestDto.setRegularTicketPrice(15.0);
        requestDto.setReducedTicketPrice(10.0);

        Seance result = seanceMapper.toEntity(requestDto, movie, cinemaRoom);
        assertThat(result).isNotNull();
    }

    @Test
    void testToEntityAssignsMovie() {
        SeanceRequestDto requestDto = new SeanceRequestDto();
        requestDto.setStartTime(LocalDateTime.of(2025, 3, 15, 19, 0));
        requestDto.setRegularTicketPrice(15.0);
        requestDto.setReducedTicketPrice(10.0);

        Seance result = seanceMapper.toEntity(requestDto, movie, cinemaRoom);
        assertThat(result.getMovie()).isEqualTo(movie);
    }

    @Test
    void testToEntityAssignsCinemaRoom() {
        SeanceRequestDto requestDto = new SeanceRequestDto();
        requestDto.setStartTime(LocalDateTime.of(2025, 3, 15, 19, 0));
        requestDto.setRegularTicketPrice(15.0);
        requestDto.setReducedTicketPrice(10.0);

        Seance result = seanceMapper.toEntity(requestDto, movie, cinemaRoom);
        assertThat(result.getCinemaRoom()).isEqualTo(cinemaRoom);
    }

    @Test
    void testToEntityPreservesStartTime() {
        SeanceRequestDto requestDto = new SeanceRequestDto();
        requestDto.setStartTime(LocalDateTime.of(2025, 3, 15, 19, 0));
        requestDto.setRegularTicketPrice(15.0);
        requestDto.setReducedTicketPrice(10.0);

        Seance result = seanceMapper.toEntity(requestDto, movie, cinemaRoom);
        assertThat(result.getStartTime()).isEqualTo(LocalDateTime.of(2025, 3, 15, 19, 0));
    }

    @Test
    void testToEntityCalculatesEndTimeBasedOnMovieDuration() {
        SeanceRequestDto requestDto = new SeanceRequestDto();
        requestDto.setStartTime(LocalDateTime.of(2025, 3, 15, 19, 0));
        requestDto.setRegularTicketPrice(15.0);
        requestDto.setReducedTicketPrice(10.0);

        Seance result = seanceMapper.toEntity(requestDto, movie, cinemaRoom);
        assertThat(result.getEndTime()).isEqualTo(LocalDateTime.of(2025, 3, 15, 21, 28));
    }

    @Test
    void testToEntityPreservesRegularTicketPrice() {
        SeanceRequestDto requestDto = new SeanceRequestDto();
        requestDto.setStartTime(LocalDateTime.of(2025, 3, 15, 19, 0));
        requestDto.setRegularTicketPrice(15.0);
        requestDto.setReducedTicketPrice(10.0);

        Seance result = seanceMapper.toEntity(requestDto, movie, cinemaRoom);
        assertThat(result.getRegularTicketPrice()).isEqualTo(15.0);
    }

    @Test
    void testToEntityPreservesReducedTicketPrice() {
        SeanceRequestDto requestDto = new SeanceRequestDto();
        requestDto.setStartTime(LocalDateTime.of(2025, 3, 15, 19, 0));
        requestDto.setRegularTicketPrice(15.0);
        requestDto.setReducedTicketPrice(10.0);

        Seance result = seanceMapper.toEntity(requestDto, movie, cinemaRoom);
        assertThat(result.getReducedTicketPrice()).isEqualTo(10.0);
    }

    @Test
    void testToEntityWithDifferentMovieDuration() {
        Movie shortMovie = new Movie();
        shortMovie.setId(2L);
        shortMovie.setTitle("Short Film");
        shortMovie.setDurationMin(90);

        SeanceRequestDto requestDto = new SeanceRequestDto();
        requestDto.setStartTime(LocalDateTime.of(2025, 3, 15, 14, 0));
        requestDto.setRegularTicketPrice(12.0);
        requestDto.setReducedTicketPrice(8.0);

        Seance result = seanceMapper.toEntity(requestDto, shortMovie, cinemaRoom);
        assertThat(result.getEndTime()).isEqualTo(LocalDateTime.of(2025, 3, 15, 15, 30));
    }

    @Test
    void testToEntityWithDifferentCinemaRoom() {
        CinemaRoom screenB = new CinemaRoom();
        screenB.setId(2L);
        screenB.setName("Screen B");

        SeanceRequestDto requestDto = new SeanceRequestDto();
        requestDto.setStartTime(LocalDateTime.of(2025, 3, 15, 19, 0));
        requestDto.setRegularTicketPrice(15.0);
        requestDto.setReducedTicketPrice(10.0);

        Seance result = seanceMapper.toEntity(requestDto, movie, screenB);
        assertThat(result.getCinemaRoom().getName()).isEqualTo("Screen B");
    }

    @Test
    void testToDtoWithDifferentPrices() {
        seance.setRegularTicketPrice(20.0);
        seance.setReducedTicketPrice(12.0);

        SeanceDto result = seanceMapper.toDto(seance);
        assertThat(result.getRegularTicketPrice()).isEqualTo(20.0);
        assertThat(result.getReducedTicketPrice()).isEqualTo(12.0);
    }

    @Test
    void testToDtoWithDifferentRoom() {
        cinemaRoom.setName("IMAX Hall");
        seance.setCinemaRoom(cinemaRoom);

        SeanceDto result = seanceMapper.toDto(seance);
        assertThat(result.getRoomName()).isEqualTo("IMAX Hall");
    }
}
