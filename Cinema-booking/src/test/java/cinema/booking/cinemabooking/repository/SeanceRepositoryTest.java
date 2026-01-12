package cinema.booking.cinemabooking.repository;

import cinema.booking.cinemabooking.model.CinemaRoom;
import cinema.booking.cinemabooking.model.Movie;
import cinema.booking.cinemabooking.model.Seance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Test class for SeanceRepository.
 */
@DataJpaTest
public class SeanceRepositoryTest {

    @Autowired
    private SeanceRepository seanceRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private CinemaRoomRepository cinemaRoomRepository;

    private Movie movie;
    private CinemaRoom cinemaRoom;
    private Seance seance;

    @BeforeEach
    void setUp() {
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
    }

    @Test
    void testSaveSeanceReturnsNotNull() {
        Seance saved = seanceRepository.save(seance);
        assertThat(saved).isNotNull();
    }

    @Test
    void testSaveSeanceGeneratesId() {
        Seance saved = seanceRepository.save(seance);
        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void testSaveSeancePreservesMovieId() {
        Seance saved = seanceRepository.save(seance);
        assertThat(saved.getMovie().getId()).isEqualTo(movie.getId());
    }

    @Test
    void testSaveSeancePreservesCinemaRoomId() {
        Seance saved = seanceRepository.save(seance);
        assertThat(saved.getCinemaRoom().getId()).isEqualTo(cinemaRoom.getId());
    }

    @Test
    void testFindByIdReturnsNotNull() {
        Seance saved = seanceRepository.save(seance);
        Seance found = seanceRepository.findById(saved.getId()).orElse(null);
        assertThat(found).isNotNull();
    }

    @Test
    void testFindByIdPreservesStartTime() {
        Seance saved = seanceRepository.save(seance);
        Seance found = seanceRepository.findById(saved.getId()).orElse(null);
        assertThat(found.getStartTime()).isEqualTo(seance.getStartTime());
    }

    @Test
    void testFindByMovieIdReturnsMultipleSeances() {
        seanceRepository.save(seance);

        Seance seance2 = new Seance();
        seance2.setMovie(movie);
        seance2.setCinemaRoom(cinemaRoom);
        seance2.setStartTime(LocalDateTime.of(2025, 1, 15, 21, 30));
        seance2.setEndTime(LocalDateTime.of(2025, 1, 15, 23, 58));
        seance2.setRegularTicketPrice(25.0);
        seance2.setReducedTicketPrice(15.0);
        seanceRepository.save(seance2);

        List<Seance> seances = seanceRepository.findByMovieId(movie.getId());
        assertThat(seances).hasSize(2);
    }

    @Test
    void testFindByMovieIdAllSeancesMatchMovie() {
        seanceRepository.save(seance);

        Seance seance2 = new Seance();
        seance2.setMovie(movie);
        seance2.setCinemaRoom(cinemaRoom);
        seance2.setStartTime(LocalDateTime.of(2025, 1, 15, 21, 30));
        seance2.setEndTime(LocalDateTime.of(2025, 1, 15, 23, 58));
        seance2.setRegularTicketPrice(25.0);
        seance2.setReducedTicketPrice(15.0);
        seanceRepository.save(seance2);

        List<Seance> seances = seanceRepository.findByMovieId(movie.getId());
        assertThat(seances.stream().allMatch(s -> s.getMovie().getId().equals(movie.getId()))).isTrue();
    }

    @Test
    void testFindByStartTimeBetweenReturnsMultipleSeances() {
        seanceRepository.save(seance);

        Seance seance2 = new Seance();
        seance2.setMovie(movie);
        seance2.setCinemaRoom(cinemaRoom);
        seance2.setStartTime(LocalDateTime.of(2025, 1, 15, 21, 30));
        seance2.setEndTime(LocalDateTime.of(2025, 1, 15, 23, 58));
        seance2.setRegularTicketPrice(25.0);
        seance2.setReducedTicketPrice(15.0);
        seanceRepository.save(seance2);

        LocalDateTime rangeStart = LocalDateTime.of(2025, 1, 15, 18, 0);
        LocalDateTime rangeEnd = LocalDateTime.of(2025, 1, 15, 22, 0);

        List<Seance> seances = seanceRepository.findByStartTimeBetween(rangeStart, rangeEnd);
        assertThat(seances).hasSize(2);
    }

    @Test
    void testFindByCinemaRoomIdReturnsMultipleSeances() {
        seanceRepository.save(seance);

        Seance seance2 = new Seance();
        seance2.setMovie(movie);
        seance2.setCinemaRoom(cinemaRoom);
        seance2.setStartTime(LocalDateTime.of(2025, 1, 15, 21, 30));
        seance2.setEndTime(LocalDateTime.of(2025, 1, 15, 23, 58));
        seance2.setRegularTicketPrice(25.0);
        seance2.setReducedTicketPrice(15.0);
        seanceRepository.save(seance2);

        List<Seance> seances = seanceRepository.findByCinemaRoomId(cinemaRoom.getId());
        assertThat(seances).hasSize(2);
    }

    @Test
    void testFindByCinemaRoomIdAllSeancesMatchRoom() {
        seanceRepository.save(seance);

        Seance seance2 = new Seance();
        seance2.setMovie(movie);
        seance2.setCinemaRoom(cinemaRoom);
        seance2.setStartTime(LocalDateTime.of(2025, 1, 15, 21, 30));
        seance2.setEndTime(LocalDateTime.of(2025, 1, 15, 23, 58));
        seance2.setRegularTicketPrice(25.0);
        seance2.setReducedTicketPrice(15.0);
        seanceRepository.save(seance2);

        List<Seance> seances = seanceRepository.findByCinemaRoomId(cinemaRoom.getId());
        assertThat(seances.stream().allMatch(s -> s.getCinemaRoom().getId().equals(cinemaRoom.getId()))).isTrue();
    }

    @Test
    void testFindOverlappingSeancesReturnsOverlappingSeance() {
        seanceRepository.save(seance);

        LocalDateTime overlapStart = LocalDateTime.of(2025, 1, 15, 20, 0);
        LocalDateTime overlapEnd = LocalDateTime.of(2025, 1, 15, 22, 0);

        List<Seance> overlappingSeances = seanceRepository.findOverlappingSeances(cinemaRoom.getId(), overlapStart, overlapEnd);
        assertThat(overlappingSeances).hasSize(1);
    }

    @Test
    void testFindOverlappingSeancesReturnsCorrectId() {
        seanceRepository.save(seance);

        LocalDateTime overlapStart = LocalDateTime.of(2025, 1, 15, 20, 0);
        LocalDateTime overlapEnd = LocalDateTime.of(2025, 1, 15, 22, 0);

        List<Seance> overlappingSeances = seanceRepository.findOverlappingSeances(cinemaRoom.getId(), overlapStart, overlapEnd);
        assertThat(overlappingSeances.get(0).getId()).isEqualTo(seance.getId());
    }

    @Test
    void testFindOverlappingSeancesNoOverlap() {
        seanceRepository.save(seance);

        LocalDateTime noOverlapStart = LocalDateTime.of(2025, 1, 15, 22, 0);
        LocalDateTime noOverlapEnd = LocalDateTime.of(2025, 1, 15, 23, 0);

        List<Seance> overlappingSeances = seanceRepository.findOverlappingSeances(cinemaRoom.getId(), noOverlapStart, noOverlapEnd);
        assertThat(overlappingSeances).isEmpty();
    }

    @Test
    void testUpdateSeanceRegularTicketPrice() {
        Seance saved = seanceRepository.save(seance);
        saved.setRegularTicketPrice(30.0);
        Seance updated = seanceRepository.save(saved);
        assertThat(updated.getRegularTicketPrice()).isEqualTo(30.0);
    }

    @Test
    void testDeleteSeance() {
        Seance saved = seanceRepository.save(seance);
        seanceRepository.deleteById(saved.getId());
        assertThat(seanceRepository.findById(saved.getId())).isEmpty();
    }
}
