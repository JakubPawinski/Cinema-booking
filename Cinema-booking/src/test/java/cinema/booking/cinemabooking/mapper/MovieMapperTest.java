package cinema.booking.cinemabooking.mapper;

import cinema.booking.cinemabooking.dto.response.MovieDto;
import cinema.booking.cinemabooking.dto.request.MovieRequestDto;
import cinema.booking.cinemabooking.dto.response.MovieWithSeancesDto;
import cinema.booking.cinemabooking.dto.response.SeanceDto;
import cinema.booking.cinemabooking.model.Movie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the MovieMapper class.
 */
public class MovieMapperTest {

    private MovieMapper movieMapper;
    private Movie movie;

    @BeforeEach
    void setUp() {
        movieMapper = new MovieMapper();
        movie = new Movie();
        movie.setId(1L);
        movie.setTitle("Inception");
        movie.setGenre("Science Fiction");
        movie.setDurationMin(148);
        movie.setDescription("A thief who steals corporate secrets");
        movie.setImageUrl("http://example.com/inception.jpg");
        movie.setTrailerUrl("http://example.com/trailer");
        movie.setDirector("Christopher Nolan");
        movie.setMainCast("Leonardo DiCaprio, Joseph Gordon-Levitt");
        movie.setAgeRating("PG-13");
    }

    @Test
    void testToDtoReturnsNotNull() {
        MovieDto result = movieMapper.toDto(movie);
        assertThat(result).isNotNull();
    }

    @Test
    void testToDtoPreservesId() {
        MovieDto result = movieMapper.toDto(movie);
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void testToDtoPreservesTitle() {
        MovieDto result = movieMapper.toDto(movie);
        assertThat(result.getTitle()).isEqualTo("Inception");
    }

    @Test
    void testToDtoPreservesGenre() {
        MovieDto result = movieMapper.toDto(movie);
        assertThat(result.getGenre()).isEqualTo("Science Fiction");
    }

    @Test
    void testToDtoPreservesDurationMin() {
        MovieDto result = movieMapper.toDto(movie);
        assertThat(result.getDurationMin()).isEqualTo(148);
    }

    @Test
    void testToDtoPreservesDescription() {
        MovieDto result = movieMapper.toDto(movie);
        assertThat(result.getDescription()).isEqualTo("A thief who steals corporate secrets");
    }

    @Test
    void testToDtoPreservesImageUrl() {
        MovieDto result = movieMapper.toDto(movie);
        assertThat(result.getImageUrl()).isEqualTo("http://example.com/inception.jpg");
    }

    @Test
    void testToDtoPreservesTrailerUrl() {
        MovieDto result = movieMapper.toDto(movie);
        assertThat(result.getTrailerUrl()).isEqualTo("http://example.com/trailer");
    }

    @Test
    void testToDtoPreservesDirector() {
        MovieDto result = movieMapper.toDto(movie);
        assertThat(result.getDirector()).isEqualTo("Christopher Nolan");
    }

    @Test
    void testToDtoPreservesMainCast() {
        MovieDto result = movieMapper.toDto(movie);
        assertThat(result.getMainCast()).isEqualTo("Leonardo DiCaprio, Joseph Gordon-Levitt");
    }

    @Test
    void testToDtoPreservesAgeRating() {
        MovieDto result = movieMapper.toDto(movie);
        assertThat(result.getAgeRating()).isEqualTo("PG-13");
    }

    @Test
    void testToDtoHandlesNullDirector() {
        movie.setDirector(null);
        MovieDto result = movieMapper.toDto(movie);
        assertThat(result.getDirector()).isEqualTo("Unknown");
    }

    @Test
    void testToDtoHandlesNullMainCast() {
        movie.setMainCast(null);
        MovieDto result = movieMapper.toDto(movie);
        assertThat(result.getMainCast()).isEqualTo("Various");
    }

    @Test
    void testToDtoHandlesNullAgeRating() {
        movie.setAgeRating(null);
        MovieDto result = movieMapper.toDto(movie);
        assertThat(result.getAgeRating()).isEqualTo("Not Rated");
    }

    @Test
    void testUpdateEntityFromDtoUpdatesTitle() {
        MovieRequestDto dto = new MovieRequestDto();
        dto.setTitle("Inception 2");
        dto.setDescription("Updated description");
        dto.setGenre("Science Fiction");
        dto.setDurationMin(150);
        dto.setTrailerUrl("http://example.com/trailer2");
        dto.setDirector("Updated Director");
        dto.setMainCast("Updated Cast");
        dto.setAgeRating("PG");

        movieMapper.updateEntityFromDto(dto, movie);
        assertThat(movie.getTitle()).isEqualTo("Inception 2");
    }

    @Test
    void testUpdateEntityFromDtoUpdatesDescription() {
        MovieRequestDto dto = new MovieRequestDto();
        dto.setTitle("Inception");
        dto.setDescription("Updated description");
        dto.setGenre("Science Fiction");
        dto.setDurationMin(148);
        dto.setTrailerUrl("http://example.com/trailer");
        dto.setDirector("Christopher Nolan");
        dto.setMainCast("Leonardo DiCaprio");
        dto.setAgeRating("PG-13");

        movieMapper.updateEntityFromDto(dto, movie);
        assertThat(movie.getDescription()).isEqualTo("Updated description");
    }

    @Test
    void testUpdateEntityFromDtoUpdatesGenre() {
        MovieRequestDto dto = new MovieRequestDto();
        dto.setTitle("Inception");
        dto.setDescription("A thief who steals corporate secrets");
        dto.setGenre("Action");
        dto.setDurationMin(148);
        dto.setTrailerUrl("http://example.com/trailer");
        dto.setDirector("Christopher Nolan");
        dto.setMainCast("Leonardo DiCaprio");
        dto.setAgeRating("PG-13");

        movieMapper.updateEntityFromDto(dto, movie);
        assertThat(movie.getGenre()).isEqualTo("Action");
    }

    @Test
    void testUpdateEntityFromDtoUpdatesDurationMin() {
        MovieRequestDto dto = new MovieRequestDto();
        dto.setTitle("Inception");
        dto.setDescription("A thief who steals corporate secrets");
        dto.setGenre("Science Fiction");
        dto.setDurationMin(160);
        dto.setTrailerUrl("http://example.com/trailer");
        dto.setDirector("Christopher Nolan");
        dto.setMainCast("Leonardo DiCaprio");
        dto.setAgeRating("PG-13");

        movieMapper.updateEntityFromDto(dto, movie);
        assertThat(movie.getDurationMin()).isEqualTo(160);
    }

    @Test
    void testUpdateEntityFromDtoUpdatesTrailerUrl() {
        MovieRequestDto dto = new MovieRequestDto();
        dto.setTitle("Inception");
        dto.setDescription("A thief who steals corporate secrets");
        dto.setGenre("Science Fiction");
        dto.setDurationMin(148);
        dto.setTrailerUrl("http://example.com/new-trailer");
        dto.setDirector("Christopher Nolan");
        dto.setMainCast("Leonardo DiCaprio");
        dto.setAgeRating("PG-13");

        movieMapper.updateEntityFromDto(dto, movie);
        assertThat(movie.getTrailerUrl()).isEqualTo("http://example.com/new-trailer");
    }

    @Test
    void testUpdateEntityFromDtoUpdatesDirector() {
        MovieRequestDto dto = new MovieRequestDto();
        dto.setTitle("Inception");
        dto.setDescription("A thief who steals corporate secrets");
        dto.setGenre("Science Fiction");
        dto.setDurationMin(148);
        dto.setTrailerUrl("http://example.com/trailer");
        dto.setDirector("New Director");
        dto.setMainCast("Leonardo DiCaprio");
        dto.setAgeRating("PG-13");

        movieMapper.updateEntityFromDto(dto, movie);
        assertThat(movie.getDirector()).isEqualTo("New Director");
    }

    @Test
    void testUpdateEntityFromDtoUpdatesMainCast() {
        MovieRequestDto dto = new MovieRequestDto();
        dto.setTitle("Inception");
        dto.setDescription("A thief who steals corporate secrets");
        dto.setGenre("Science Fiction");
        dto.setDurationMin(148);
        dto.setTrailerUrl("http://example.com/trailer");
        dto.setDirector("Christopher Nolan");
        dto.setMainCast("Tom Hardy, Marion Cotillard");
        dto.setAgeRating("PG-13");

        movieMapper.updateEntityFromDto(dto, movie);
        assertThat(movie.getMainCast()).isEqualTo("Tom Hardy, Marion Cotillard");
    }

    @Test
    void testUpdateEntityFromDtoUpdatesAgeRating() {
        MovieRequestDto dto = new MovieRequestDto();
        dto.setTitle("Inception");
        dto.setDescription("A thief who steals corporate secrets");
        dto.setGenre("Science Fiction");
        dto.setDurationMin(148);
        dto.setTrailerUrl("http://example.com/trailer");
        dto.setDirector("Christopher Nolan");
        dto.setMainCast("Leonardo DiCaprio");
        dto.setAgeRating("R");

        movieMapper.updateEntityFromDto(dto, movie);
        assertThat(movie.getAgeRating()).isEqualTo("R");
    }

    @Test
    void testToRequestDtoReturnsNotNull() {
        MovieDto dto = movieMapper.toDto(movie);
        MovieRequestDto result = movieMapper.toRequestDto(dto);
        assertThat(result).isNotNull();
    }

    @Test
    void testToRequestDtoPreservesTitle() {
        MovieDto dto = movieMapper.toDto(movie);
        MovieRequestDto result = movieMapper.toRequestDto(dto);
        assertThat(result.getTitle()).isEqualTo("Inception");
    }

    @Test
    void testToRequestDtoPreservesDescription() {
        MovieDto dto = movieMapper.toDto(movie);
        MovieRequestDto result = movieMapper.toRequestDto(dto);
        assertThat(result.getDescription()).isEqualTo("A thief who steals corporate secrets");
    }

    @Test
    void testToRequestDtoPreservesGenre() {
        MovieDto dto = movieMapper.toDto(movie);
        MovieRequestDto result = movieMapper.toRequestDto(dto);
        assertThat(result.getGenre()).isEqualTo("Science Fiction");
    }

    @Test
    void testToRequestDtoPreservesDurationMin() {
        MovieDto dto = movieMapper.toDto(movie);
        MovieRequestDto result = movieMapper.toRequestDto(dto);
        assertThat(result.getDurationMin()).isEqualTo(148);
    }

    @Test
    void testToRequestDtoPreservesImageUrl() {
        MovieDto dto = movieMapper.toDto(movie);
        MovieRequestDto result = movieMapper.toRequestDto(dto);
        assertThat(result.getImageUrl()).isEqualTo("http://example.com/inception.jpg");
    }

    @Test
    void testToRequestDtoPreservesTrailerUrl() {
        MovieDto dto = movieMapper.toDto(movie);
        MovieRequestDto result = movieMapper.toRequestDto(dto);
        assertThat(result.getTrailerUrl()).isEqualTo("http://example.com/trailer");
    }

    @Test
    void testToRequestDtoPreservesDirector() {
        MovieDto dto = movieMapper.toDto(movie);
        MovieRequestDto result = movieMapper.toRequestDto(dto);
        assertThat(result.getDirector()).isEqualTo("Christopher Nolan");
    }

    @Test
    void testToRequestDtoPreservesMainCast() {
        MovieDto dto = movieMapper.toDto(movie);
        MovieRequestDto result = movieMapper.toRequestDto(dto);
        assertThat(result.getMainCast()).isEqualTo("Leonardo DiCaprio, Joseph Gordon-Levitt");
    }

    @Test
    void testToRequestDtoPreservesAgeRating() {
        MovieDto dto = movieMapper.toDto(movie);
        MovieRequestDto result = movieMapper.toRequestDto(dto);
        assertThat(result.getAgeRating()).isEqualTo("PG-13");
    }

    @Test
    void testToMovieWithSeancesDtoReturnsNotNull() {
        List<SeanceDto> seanceDtos = List.of();
        MovieWithSeancesDto result = movieMapper.toMovieWithSeancesDto(movie, seanceDtos);
        assertThat(result).isNotNull();
    }

    @Test
    void testToMovieWithSeancesDtoPreservesMovieId() {
        List<SeanceDto> seanceDtos = List.of();
        MovieWithSeancesDto result = movieMapper.toMovieWithSeancesDto(movie, seanceDtos);
        assertThat(result.getMovieId()).isEqualTo(1L);
    }

    @Test
    void testToMovieWithSeancesDtoPreservesTitle() {
        List<SeanceDto> seanceDtos = List.of();
        MovieWithSeancesDto result = movieMapper.toMovieWithSeancesDto(movie, seanceDtos);
        assertThat(result.getTitle()).isEqualTo("Inception");
    }

    @Test
    void testToMovieWithSeancesDtoPreservesGenre() {
        List<SeanceDto> seanceDtos = List.of();
        MovieWithSeancesDto result = movieMapper.toMovieWithSeancesDto(movie, seanceDtos);
        assertThat(result.getGenre()).isEqualTo("Science Fiction");
    }

    @Test
    void testToMovieWithSeancesDtoPreservesDurationMin() {
        List<SeanceDto> seanceDtos = List.of();
        MovieWithSeancesDto result = movieMapper.toMovieWithSeancesDto(movie, seanceDtos);
        assertThat(result.getDurationMin()).isEqualTo(148);
    }

    @Test
    void testToMovieWithSeancesDtoPreservesDescription() {
        List<SeanceDto> seanceDtos = List.of();
        MovieWithSeancesDto result = movieMapper.toMovieWithSeancesDto(movie, seanceDtos);
        assertThat(result.getDescription()).isEqualTo("A thief who steals corporate secrets");
    }

    @Test
    void testToMovieWithSeancesDtoPreservesImageUrl() {
        List<SeanceDto> seanceDtos = List.of();
        MovieWithSeancesDto result = movieMapper.toMovieWithSeancesDto(movie, seanceDtos);
        assertThat(result.getImageUrl()).isEqualTo("http://example.com/inception.jpg");
    }

    @Test
    void testToMovieWithSeancesDtoPreservesSeances() {
        List<SeanceDto> seanceDtos = List.of();
        MovieWithSeancesDto result = movieMapper.toMovieWithSeancesDto(movie, seanceDtos);
        assertThat(result.getSeances()).isEmpty();
    }

    @Test
    void testToMovieWithSeancesDtoWithMultipleSeances() {
        List<SeanceDto> seanceDtos = Arrays.asList(null, null);
        MovieWithSeancesDto result = movieMapper.toMovieWithSeancesDto(movie, seanceDtos);
        assertThat(result.getSeances()).hasSize(2);
    }

}
