package cinema.booking.cinemabooking.mapper;

import cinema.booking.cinemabooking.dto.response.MovieDto;
import cinema.booking.cinemabooking.dto.request.MovieRequestDto;
import cinema.booking.cinemabooking.dto.response.MovieWithSeancesDto;
import cinema.booking.cinemabooking.dto.response.SeanceDto;
import cinema.booking.cinemabooking.model.Movie;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper for Movie entity and DTOs
 */
@Component
public class MovieMapper {

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
                .galleryImages(movie.getGalleryImages())
                .build();
    }

    /**
     * Updates Movie entity from MovieRequestDto
     * @param dto the request DTO with new data
     * @param movie the existing Movie entity to update
     */
    public void updateEntityFromDto(MovieRequestDto dto, Movie movie) {
        movie.setTitle(dto.getTitle());
        movie.setDescription(dto.getDescription());
        movie.setGenre(dto.getGenre());
        movie.setDurationMin(dto.getDurationMin());
        movie.setTrailerUrl(dto.getTrailerUrl());
        movie.setDirector(dto.getDirector());
        movie.setMainCast(dto.getMainCast());
        movie.setAgeRating(dto.getAgeRating());


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
     * Converts Movie entity to MovieWithSeancesDto
     * @param movie the Movie entity
     * @param seanceDtos list of SeanceDto associated with the movie
     * @return the MovieWithSeancesDto
     */
    public MovieWithSeancesDto toMovieWithSeancesDto(Movie movie, List<SeanceDto> seanceDtos) {
        return MovieWithSeancesDto.builder()
                .movieId(movie.getId())
                .title(movie.getTitle())
                .genre(movie.getGenre())
                .durationMin(movie.getDurationMin())
                .description(movie.getDescription())
                .imageUrl(movie.getImageUrl())
                .seances(seanceDtos)
                .build();
    }
}