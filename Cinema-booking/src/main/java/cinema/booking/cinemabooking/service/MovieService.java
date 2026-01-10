package cinema.booking.cinemabooking.service;

import cinema.booking.cinemabooking.dto.response.MovieDto;
import cinema.booking.cinemabooking.dto.request.MovieRequestDto;
import cinema.booking.cinemabooking.exception.ResourceNotFoundException;
import cinema.booking.cinemabooking.mapper.MovieMapper;
import cinema.booking.cinemabooking.model.Movie;
import cinema.booking.cinemabooking.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing movie catalog.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MovieService {
    private final MovieRepository movieRepository;
    private final FileStorageService fileStorageService; // 1. Wstrzykujemy serwis plików
    private final MovieMapper movieMapper;

    /**
     * Add new movie
     * @param dto MovieRequestDto
     */
    @Transactional
    public void addMovie(MovieRequestDto dto) {
        log.info("Adding new movie: {}", dto.getTitle());

        Movie movie = new Movie();

        movieMapper.updateEntityFromDto(dto, movie);

        handleImageUpdate(dto, movie);

        movieRepository.save(movie);
        log.info("Movie added successfully with ID: {}", movie.getId());
    }

    /**
     * Update existing movie
     * @param id Movie ID
     * @param dto MovieRequestDto
     */
    @Transactional
    public void updateMovie(Long id, MovieRequestDto dto) {
        log.info("Updating movie with ID: {}", id);

        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Movie with ID {} not found", id);
                    return new ResourceNotFoundException("Movie not found");
                });

        movieMapper.updateEntityFromDto(dto, movie);
        handleImageUpdate(dto, movie);

        movieRepository.save(movie);
        log.info("Movie with ID {} updated successfully", id);
    }

    /**
     * Get all movies with pagination
     * @param pageable Pageable object
     * @return Page of MovieDto
     */
    @Transactional(readOnly = true)
    public Page<MovieDto> getAllMovies(Pageable pageable) {
        log.debug("Fetching all movies with pagination: {}", pageable);
        return movieRepository.findAll(pageable)
                .map(movieMapper::toDto);
    }

    /**
     * Get movie by ID
     * @param id Movie ID
     * @return MovieDto
     */
    @Transactional(readOnly = true)
    public MovieDto getMovieById(Long id) {
        log.debug("Fetching movie with ID: {}", id);
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Movie with ID {} not found", id);
                    return new ResourceNotFoundException("Movie not found");
                });
        return movieMapper.toDto(movie);
    }

    /**
     * Delete movie by ID
     * @param id Movie ID
     */
    @Transactional
    public void deleteMovie(Long id) {
        log.info("Deleting movie with ID: {}", id);
        Movie m = movieRepository.findById(id).orElse(null);

        if (m != null) {
            if (isLocalImage(m.getImageUrl())) {
                log.debug("Deleting local image for movie ID: {}", id);
                fileStorageService.deleteFile(m.getImageUrl());
            }
            movieRepository.deleteById(id);
            log.info("Movie with ID: {} deleted successfully", id);
        } else {
            log.warn("Movie with ID: {} not found for deletion", id);
        }
    }


    /**
     * Handles image update logic for adding or updating a movie.
     * @param dto the MovieRequestDto containing new image data
     * @param movie the Movie entity to update
     */
    private void handleImageUpdate(MovieRequestDto dto, Movie movie) {
        // Local image upload
        if (dto.getImageFile() != null && !dto.getImageFile().isEmpty()) {
            log.debug("Handling local image upload for movie: {}", dto.getTitle());
            // Check if there's an old image to delete
            if (isLocalImage(movie.getImageUrl())) {
                log.debug("Deleting old local image for movie: {}", dto.getTitle());
                fileStorageService.deleteFile(movie.getImageUrl());
            }

            // Store new image file
            String path = fileStorageService.storeFile(dto.getImageFile(), dto.getTitle());
            movie.setImageUrl(path);
            log.debug("New local image stored at: {}", path);
        }
        // External image URL
        else if (dto.getImageUrl() != null) {
            // If imageUrl has changed, update it and delete old local image if necessary
            if (!dto.getImageUrl().equals(movie.getImageUrl())) {
                log.debug("Updating image URL for movie: {}", dto.getTitle());
                // Delete old image if it was stored locally
                if (isLocalImage(movie.getImageUrl())) {
                    log.debug("Deleting old local image for movie: {}", dto.getTitle());
                    fileStorageService.deleteFile(movie.getImageUrl());
                }
                movie.setImageUrl(dto.getImageUrl());
            }
        }
    }

    /**
     * Checks if the image URL points to a locally stored image
     * @param imageUrl the image URL to check
     * @return true if local, false otherwise
     */
    private boolean isLocalImage(String imageUrl) {
        return imageUrl != null && imageUrl.startsWith("/uploads/");
    }
}