package cinema.booking.cinemabooking.repository;
import cinema.booking.cinemabooking.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Movie entity
 */
@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    /**
     * Find movies by genre
     * @param genre the category of the movie (e.g., Action, Comedy, Drama)
     * @return a list of movies matching the specified genre
     */
    List<Movie> findByGenre(String genre);

    /**
     * Find movies with titles containing the specified keyword (case-insensitive)
     * @param title the keyword to search for in movie titles
     * @return a list of movies whose titles contain the specified keyword
     */
    List<Movie> findByTitleContainingIgnoreCase(String title);
}
