package cinema.booking.cinemabooking.mapper;

import cinema.booking.cinemabooking.dto.response.MovieDto;
import cinema.booking.cinemabooking.dto.request.MovieRequestDto;
import cinema.booking.cinemabooking.model.Movie;
import cinema.booking.cinemabooking.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MovieMapper {

    private final FileStorageService fileStorageService;

    // Entity -> DTO (do wyświetlania)
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

    // DTO -> Entity (przy dodawaniu/edycji)
    // Zwróć uwagę: ta metoda modyfikuje istniejący obiekt Movie, zamiast tworzyć nowy
    public void updateEntity(MovieRequestDto dto, Movie movie) {
        movie.setTitle(dto.getTitle());
        movie.setDescription(dto.getDescription());
        movie.setGenre(dto.getGenre());
        movie.setDurationMin(dto.getDurationMin());
        movie.setTrailerUrl(dto.getTrailerUrl());
        movie.setDirector(dto.getDirector());
        movie.setMainCast(dto.getMainCast());
        movie.setAgeRating(dto.getAgeRating());

        // --- LOGIKA ZDJĘĆ PRZENIESIONA TUTAJ ---

        // 1. Upload pliku fizycznego
        if (dto.getImageFile() != null && !dto.getImageFile().isEmpty()) {
            // Usuń stary plik jeśli istnieje
            if (movie.getImageUrl() != null && movie.getImageUrl().startsWith("/uploads/")) {
                fileStorageService.deleteFile(movie.getImageUrl());
            }
            // Zapisz nowy
            String uploadedPath = fileStorageService.storeFile(dto.getImageFile(), dto.getTitle());
            movie.setImageUrl(uploadedPath);
        }
        // 2. Zmiana URL tekstowego
        else if (dto.getImageUrl() != null && !dto.getImageUrl().isEmpty()) {
            if (!dto.getImageUrl().equals(movie.getImageUrl())) {
                if (movie.getImageUrl() != null && movie.getImageUrl().startsWith("/uploads/")) {
                    fileStorageService.deleteFile(movie.getImageUrl());
                }
                movie.setImageUrl(dto.getImageUrl());
            }
        }
    }

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
}