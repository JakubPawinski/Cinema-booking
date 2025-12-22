package cinema.booking.cinemabooking.repository;
import cinema.booking.cinemabooking.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    // Find movies by genre
    List<Movie> findByGenre(String genre);

    // Search movies by title (case insensitive)
    List<Movie> findByTitleContainingIgnoreCase(String title);
}
