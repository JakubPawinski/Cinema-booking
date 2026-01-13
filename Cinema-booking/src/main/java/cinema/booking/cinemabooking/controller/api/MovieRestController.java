package cinema.booking.cinemabooking.controller.api;

import cinema.booking.cinemabooking.dto.response.MovieDto;
import cinema.booking.cinemabooking.dto.request.MovieRequestDto;
import cinema.booking.cinemabooking.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
@Tag(name = "Movies", description = "Endpoints for managing movies")
public class MovieRestController {
    private final MovieService movieService;

    /**
     * Get all movies with pagination
     * @param pageable pagination parameters (page, size)
     * @return page of MovieDto
     */
    @GetMapping
    @Operation(summary = "Get all movies", description = "Retrieve a paginated list of all movies")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of movies"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
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
    @Operation(summary = "Get movie by ID", description = "Retrieve a movie by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the movie"),
            @ApiResponse(responseCode = "404", description = "Movie not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<MovieDto> getMovieById(@PathVariable Long id) {
        log.info("API: Fetching movie by ID: {}", id);

        return ResponseEntity.ok(movieService.getMovieById(id));
    }

    /**
     * Add a new movie
     * @param dto movie data
     * @return HTTP 201 Created
     */
    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @Operation(summary = "Add new movie", description = "Add a new movie to the catalog")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Movie created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid movie data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> addMovie(@Valid @ModelAttribute MovieRequestDto dto) {
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
    @Operation(summary = "Delete movie by ID", description = "Delete a movie from the catalog by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Movie deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Movie not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        log.info("API: Deleting movie by ID: {}", id);

        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }

}
