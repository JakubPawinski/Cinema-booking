package cinema.booking.cinemabooking.repository;

import cinema.booking.cinemabooking.model.Movie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for MovieRepository.
 */
@DataJpaTest
public class MovieRepositoryTest {

    @Autowired
    private MovieRepository movieRepository;

    private Movie movie;

    @BeforeEach
    void setUp() {
        movie = new Movie();
        movie.setTitle("Inception");
        movie.setGenre("Science Fiction");
        movie.setDurationMin(148);
        movie.setDirector("Christopher Nolan");
        movie.setMainCast("Leonardo DiCaprio, Joseph Gordon-Levitt");
        movie.setAgeRating("PG-13");
    }

    @Test
    void testSaveMovieReturnsNotNull() {
        Movie saved = movieRepository.save(movie);
        assertThat(saved).isNotNull();
    }

    @Test
    void testSaveMovieGeneratesId() {
        Movie saved = movieRepository.save(movie);
        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void testSaveMoviePreservesTitle() {
        Movie saved = movieRepository.save(movie);
        assertThat(saved.getTitle()).isEqualTo("Inception");
    }

    @Test
    void testSaveMoviePreservesGenre() {
        Movie saved = movieRepository.save(movie);
        assertThat(saved.getGenre()).isEqualTo("Science Fiction");
    }

    @Test
    void testSaveMoviePreservesDurationMin() {
        Movie saved = movieRepository.save(movie);
        assertThat(saved.getDurationMin()).isEqualTo(148);
    }

    @Test
    void testFindByIdReturnsNotNull() {
        Movie saved = movieRepository.save(movie);
        Movie found = movieRepository.findById(saved.getId()).orElse(null);
        assertThat(found).isNotNull();
    }

    @Test
    void testFindByIdPreservesTitle() {
        Movie saved = movieRepository.save(movie);
        Movie found = movieRepository.findById(saved.getId()).orElse(null);
        assertThat(found.getTitle()).isEqualTo("Inception");
    }

    @Test
    void testFindByIdPreservesGenre() {
        Movie saved = movieRepository.save(movie);
        Movie found = movieRepository.findById(saved.getId()).orElse(null);
        assertThat(found.getGenre()).isEqualTo("Science Fiction");
    }

    @Test
    void testFindAllReturnsMultipleMovies() {
        movieRepository.save(movie);
        Movie movie2 = new Movie();
        movie2.setTitle("The Dark Knight");
        movie2.setGenre("Action");
        movie2.setDurationMin(152);
        movieRepository.save(movie2);

        assertThat(movieRepository.findAll()).hasSize(2);
    }

    @Test
    void testFindByGenreReturnsSingleMovie() {
        movieRepository.save(movie);
        Movie movie2 = new Movie();
        movie2.setTitle("The Dark Knight");
        movie2.setGenre("Action");
        movie2.setDurationMin(152);
        movieRepository.save(movie2);

        List<Movie> scienceFictionMovies = movieRepository.findByGenre("Science Fiction");
        assertThat(scienceFictionMovies).hasSize(1);
    }

    @Test
    void testFindByGenreReturnsCorrectMovie() {
        movieRepository.save(movie);
        Movie movie2 = new Movie();
        movie2.setTitle("The Dark Knight");
        movie2.setGenre("Action");
        movie2.setDurationMin(152);
        movieRepository.save(movie2);

        List<Movie> scienceFictionMovies = movieRepository.findByGenre("Science Fiction");
        assertThat(scienceFictionMovies.get(0).getTitle()).isEqualTo("Inception");
    }

    @Test
    void testFindByGenreNoResults() {
        movieRepository.save(movie);
        List<Movie> actionMovies = movieRepository.findByGenre("Action");
        assertThat(actionMovies).isEmpty();
    }

    @Test
    void testFindByTitleContainingIgnoreCaseReturnsMultipleMovies() {
        movieRepository.save(movie);
        Movie movie2 = new Movie();
        movie2.setTitle("Inception 2");
        movie2.setGenre("Science Fiction");
        movie2.setDurationMin(150);
        movieRepository.save(movie2);

        List<Movie> results = movieRepository.findByTitleContainingIgnoreCase("inception");
        assertThat(results).hasSize(2);
    }

    @Test
    void testFindByTitleContainingIgnoreCaseCaseInsensitive() {
        movieRepository.save(movie);
        Movie movie2 = new Movie();
        movie2.setTitle("Inception 2");
        movie2.setGenre("Science Fiction");
        movie2.setDurationMin(150);
        movieRepository.save(movie2);

        List<Movie> results = movieRepository.findByTitleContainingIgnoreCase("INCEPTION");
        assertThat(results).hasSize(2);
    }

    @Test
    void testFindByTitleContainingIgnoreCaseNoResults() {
        movieRepository.save(movie);
        List<Movie> results = movieRepository.findByTitleContainingIgnoreCase("NonExistent");
        assertThat(results).isEmpty();
    }

    @Test
    void testUpdateMovieTitle() {
        Movie saved = movieRepository.save(movie);
        saved.setTitle("Inception Updated");
        Movie updated = movieRepository.save(saved);
        assertThat(updated.getTitle()).isEqualTo("Inception Updated");
    }

    @Test
    void testUpdateMovieGenre() {
        Movie saved = movieRepository.save(movie);
        saved.setGenre("Fantasy");
        Movie updated = movieRepository.save(saved);
        assertThat(updated.getGenre()).isEqualTo("Fantasy");
    }

    @Test
    void testDeleteMovie() {
        Movie saved = movieRepository.save(movie);
        movieRepository.deleteById(saved.getId());
        assertThat(movieRepository.findById(saved.getId())).isEmpty();
    }
}
