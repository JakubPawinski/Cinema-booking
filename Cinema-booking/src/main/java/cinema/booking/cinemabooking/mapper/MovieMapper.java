package cinema.booking.cinemabooking.mapper;

import cinema.booking.cinemabooking.dto.response.MovieDto;
import cinema.booking.cinemabooking.dto.request.MovieRequestDto;
import cinema.booking.cinemabooking.model.Movie;
import cinema.booking.cinemabooking.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper for Movie entity and DTOs
 */
@Component
@RequiredArgsConstructor
public class MovieMapper {

    private final FileStorageService fileStorageService;

    /**
     * Converts Movie entity to MovieDto
     * @param movie the Movie entity
     * @return the MovieDto
     */
    public MovieDto toDto(Movie movie) {
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

    /**
     * Updates Movie entity from MovieRequestDto
     * @param dto the request DTO with new data
     * @param movie the existing Movie entity to update
     */
    public void updateEntity(MovieRequestDto dto, Movie movie) {
        movie.setTitle(dto.getTitle());
        movie.setDescription(dto.getDescription());
        movie.setGenre(dto.getGenre());
        movie.setDurationMin(dto.getDurationMin());
        movie.setTrailerUrl(dto.getTrailerUrl());
        movie.setDirector(dto.getDirector());
        movie.setMainCast(dto.getMainCast());
        movie.setAgeRating(dto.getAgeRating());


        // Handle image update
        if (dto.getImageFile() != null && !dto.getImageFile().isEmpty()) {
            // Delete old image if it was stored locally
            if (isLocalImage(movie.getImageUrl())) {
                fileStorageService.deleteFile(movie.getImageUrl());
            }
            // Store new image
            String uploadedPath = fileStorageService.storeFile(dto.getImageFile(), dto.getTitle());
            movie.setImageUrl(uploadedPath);
        }
        // Handle image URL update
        else if (dto.getImageUrl() != null && !dto.getImageUrl().isEmpty()) {
            // Update only if URL has changed
            if (!dto.getImageUrl().equals(movie.getImageUrl())) {
                // Delete old image if it was stored locally
                if (isLocalImage(movie.getImageUrl())) {
                    fileStorageService.deleteFile(movie.getImageUrl());
                }
                movie.setImageUrl(dto.getImageUrl());
            }
        }
    }

    /**
     * Converts MovieDto (response) to MovieRequestDto (request)
     * @param dto the MovieDto
     * @return the MovieRequestDto
     */
    public MovieRequestDto toRequestDto(MovieDto dto) {
        MovieRequestDto requestDto = new MovieRequestDto();
        requestDto.setTitle(dto.getTitle());
        requestDto.setDescription(dto.getDescription());
        requestDto.setGenre(dto.getGenre());
        requestDto.setDurationMin(dto.getDurationMin());
        requestDto.setImageUrl(dto.getImageUrl());
        requestDto.setTrailerUrl(dto.getTrailerUrl());
        requestDto.setDirector(dto.getDirector());
        requestDto.setMainCast(dto.getMainCast());
        requestDto.setAgeRating(dto.getAgeRating());
        return requestDto;
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