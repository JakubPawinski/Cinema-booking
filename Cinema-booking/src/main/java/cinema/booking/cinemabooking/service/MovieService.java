package cinema.booking.cinemabooking.service;

import cinema.booking.cinemabooking.dto.MovieDto;
import cinema.booking.cinemabooking.dto.MovieRequestDto;
import cinema.booking.cinemabooking.dto.SeatDto;
import cinema.booking.cinemabooking.model.Movie;
import cinema.booking.cinemabooking.model.Seance;
import cinema.booking.cinemabooking.model.Seat;
import cinema.booking.cinemabooking.model.Ticket;
import cinema.booking.cinemabooking.repository.MovieRepository;
import cinema.booking.cinemabooking.repository.SeanceRepository;
import cinema.booking.cinemabooking.repository.SeatRepository;
import cinema.booking.cinemabooking.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class MovieService {
    private final MovieRepository movieRepository;
    private final SeanceRepository seanceRepository;
    private final TicketRepository ticketRepository;
    private final SeatRepository seatRepository;

    /*
     * Add a new movie
     */
    @Transactional
    public void addMovie(MovieRequestDto dto) {
        Movie movie = new Movie();

        movie.setTitle(dto.getTitle());
        movie.setDescription(dto.getDescription());
        movie.setGenre(dto.getGenre());
        movie.setDurationMin(dto.getDurationMin());
        movie.setImageUrl(dto.getImageUrl());
        movie.setTrailerUrl(dto.getTrailerUrl());
        movie.setDirector(dto.getDirector());
        movie.setMainCast(dto.getMainCast());
        movie.setAgeRating(dto.getAgeRating());

        movieRepository.save(movie);

    }

    /*
     * Get all movies
     */
    @Transactional(readOnly = true)
    public Page<MovieDto> getAllMovies(Pageable pageable) {
        return movieRepository.findAll(pageable)
                .map(m -> MovieDto.builder()
                        .id(m.getId())
                        .title(m.getTitle())
                        .genre(m.getGenre())
                        .durationMin(m.getDurationMin())
                        .description(m.getDescription())
                        .imageUrl(m.getImageUrl())
                        .trailerUrl(m.getTrailerUrl())
                        .director(m.getDirector() != null ? m.getDirector() : "Unknown")
                        .mainCast(m.getMainCast() != null ? m.getMainCast() : "Various")
                        .ageRating(m.getAgeRating() != null ? m.getAgeRating() : "Not Rated")
                        .build());
    }

    /*
     * Get movie by ID
     */
    @Transactional(readOnly = true)
    public MovieDto getMovieById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found"));
        return MovieDto.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .genre(movie.getGenre())
                .durationMin(movie.getDurationMin())
                .description(movie.getDescription())
                .imageUrl(movie.getImageUrl())
                .trailerUrl(movie.getTrailerUrl())
                .director(movie.getDirector() != null ? movie.getDirector() : "Unknown")
                .mainCast(movie.getMainCast() != null ? movie.getMainCast() : "Various")
                .ageRating(movie.getAgeRating() != null ? movie.getAgeRating() : "Not Rated")
                .build();
    }

    /*
     * Delete movie by ID
     */
    @Transactional
    public void deleteMovie(Long id) {
        movieRepository.deleteById(id);
    }
}
