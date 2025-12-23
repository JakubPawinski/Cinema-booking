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

        movieRepository.save(movie);

    }

    /*
     * Get all movies
     */
    @Transactional(readOnly = true)
    public List<MovieDto> getAllMovies() {
        return movieRepository.findAll().stream()
                .map(m -> MovieDto.builder()
                        .id(m.getId())
                        .title(m.getTitle())
                        .genre(m.getGenre())
                        .durationMin(m.getDurationMin())
                        .description(m.getDescription())
                        .imageUrl(m.getImageUrl())
                        .trailerUrl(m.getTrailerUrl())
                        .build())
                .collect(Collectors.toList());
    }

}
