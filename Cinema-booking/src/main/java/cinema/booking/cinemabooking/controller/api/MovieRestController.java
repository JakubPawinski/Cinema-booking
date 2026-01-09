package cinema.booking.cinemabooking.controller.api;

import cinema.booking.cinemabooking.dto.response.MovieDto;
import cinema.booking.cinemabooking.dto.request.MovieRequestDto;
import cinema.booking.cinemabooking.service.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * REST API controller for movies
 */
@RequiredArgsConstructor
@RequestMapping("/api/v1/movies")
@RestController
@Slf4j
public class MovieRestController {
    private final MovieService movieService;

    /**
     * Get all movies with pagination
     * @param pageable pagination parameters (page, size)
     * @return page of MovieDto
     */
    @GetMapping
    public ResponseEntity<Page<MovieDto>> getAllMovies(Pageable pageable) {
        log.info("API: Fetching all movies with pagination: page {}, size {}", pageable.getPageNumber(), pageable.getPageSize());

        return ResponseEntity.ok(movieService.getAllMovies(pageable));
    }

    /**
     * Get movie by ID
     * @param id movie ID
     * @return MovieDto
     */
    @GetMapping("/{id}")
    public ResponseEntity<MovieDto> getMovieById(Long id) {
        log.info("API: Fetching movie by ID: {}", id);

        return ResponseEntity.ok(movieService.getMovieById(id));
    }

    /**
     * Add a new movie
     * @param dto movie data
     * @return HTTP 201 Created
     */
    @PostMapping
    public ResponseEntity<Void> addMovie(@Valid @RequestBody MovieRequestDto dto) {
        log.info("API: Adding new movie: {}", dto.getTitle());

        movieService.addMovie(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Update movie by ID
     * @param id movie ID
     * @return HTTP 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        log.info("API: Deleting movie by ID: {}", id);

        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }

}
