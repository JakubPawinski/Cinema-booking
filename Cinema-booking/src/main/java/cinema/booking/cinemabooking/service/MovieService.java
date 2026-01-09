package cinema.booking.cinemabooking.service;

import cinema.booking.cinemabooking.dto.response.MovieDto;
import cinema.booking.cinemabooking.dto.request.MovieRequestDto;
import cinema.booking.cinemabooking.model.Movie;
import cinema.booking.cinemabooking.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MovieService {
    private final MovieRepository movieRepository;
    private final FileStorageService fileStorageService; // 1. Wstrzykujemy serwis plików

    /*
     * Add a new movie
     */
    @Transactional
    public void addMovie(MovieRequestDto dto) {
        Movie movie = new Movie();
        mapDtoToMovie(dto, movie); // Używamy metody pomocniczej
        movieRepository.save(movie);
    }

    /*
     * Update movie by ID
     */
    @Transactional
    public void updateMovie(Long id, MovieRequestDto dto) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found"));

        mapDtoToMovie(dto, movie); // Aktualizujemy istniejący obiekt
        movieRepository.save(movie);
    }

    /*
     * Metoda pomocnicza do mapowania DTO -> Entity
     * Zawiera logikę obsługi zdjęć (File vs URL)
     */
    private void mapDtoToMovie(MovieRequestDto dto, Movie movie) {
        // Podstawowe pola
        movie.setTitle(dto.getTitle());
        movie.setDescription(dto.getDescription());
        movie.setGenre(dto.getGenre());
        movie.setDurationMin(dto.getDurationMin());
        movie.setTrailerUrl(dto.getTrailerUrl());
        movie.setDirector(dto.getDirector());
        movie.setMainCast(dto.getMainCast());
        movie.setAgeRating(dto.getAgeRating());

        // --- LOGIKA ZDJĘCIA ---

        // SCENARIUSZ 1: Użytkownik przesyła NOWY plik fizyczny
        if (dto.getImageFile() != null && !dto.getImageFile().isEmpty()) {

            // A. Sprzątanie: Jeśli film miał wcześniej plik lokalny, usuń go
            if (movie.getImageUrl() != null && movie.getImageUrl().startsWith("/uploads/")) {
                fileStorageService.deleteFile(movie.getImageUrl());
            }

            // B. Zapisz nowy plik i ustaw ścieżkę
            String uploadedPath = fileStorageService.storeFile(dto.getImageFile(), dto.getTitle());
            movie.setImageUrl(uploadedPath);
        }

        // SCENARIUSZ 2: Użytkownik podaje/zmienia URL tekstowy (i nie przesyła pliku)
        else if (dto.getImageUrl() != null && !dto.getImageUrl().isEmpty()) {

            // Sprawdzamy czy URL faktycznie się różni od tego co mamy
            if (!dto.getImageUrl().equals(movie.getImageUrl())) {

                // A. Sprzątanie: Jeśli stary obrazek był plikiem lokalnym, a teraz zmieniamy na zewnętrzny URL -> usuń plik z dysku
                if (movie.getImageUrl() != null && movie.getImageUrl().startsWith("/uploads/")) {
                    fileStorageService.deleteFile(movie.getImageUrl());
                }

                // B. Ustaw nowy URL
                movie.setImageUrl(dto.getImageUrl());
            }
        }
        // SCENARIUSZ 3: Oba pola puste -> Zostawiamy stary obrazek bez zmian (nic nie robimy)
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
         Movie m = movieRepository.findById(id).orElse(null);
         if(m != null && m.getImageUrl() != null && m.getImageUrl().startsWith("/uploads/")) {
             fileStorageService.deleteFile(m.getImageUrl());
         }

        movieRepository.deleteById(id);
    }
}