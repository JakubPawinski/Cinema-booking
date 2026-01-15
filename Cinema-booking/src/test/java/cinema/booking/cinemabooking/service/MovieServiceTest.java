package cinema.booking.cinemabooking.service;

import cinema.booking.cinemabooking.dto.request.MovieRequestDto;
import cinema.booking.cinemabooking.dto.response.MovieDto;
import cinema.booking.cinemabooking.exception.ResourceNotFoundException;
import cinema.booking.cinemabooking.mapper.MovieMapper;
import cinema.booking.cinemabooking.model.Movie;
import cinema.booking.cinemabooking.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private MovieMapper movieMapper;

    @InjectMocks
    private MovieService movieService;

    private Movie movie;
    private MovieDto movieDto;
    private MovieRequestDto movieRequestDto;

    @BeforeEach
    void setUp() {
        movie = new Movie();
        movie.setId(1L);
        movie.setTitle("Inception");
        movie.setDescription("A mind-bending thriller");
        movie.setGenre("Sci-Fi");
        movie.setDurationMin(148);
        movie.setImageUrl("https://example.com/inception.jpg");
        movie.setTrailerUrl("https://youtube.com/watch?v=inception");
        movie.setDirector("Christopher Nolan");
        movie.setMainCast("Leonardo DiCaprio, Marion Cotillard");
        movie.setAgeRating("PG-13");
        movie.setGalleryImages(new ArrayList<>());

        movieDto = MovieDto.builder()
                .id(1L)
                .title("Inception")
                .description("A mind-bending thriller")
                .genre("Sci-Fi")
                .durationMin(148)
                .imageUrl("https://example.com/inception.jpg")
                .trailerUrl("https://youtube.com/watch?v=inception")
                .director("Christopher Nolan")
                .mainCast("Leonardo DiCaprio, Marion Cotillard")
                .ageRating("PG-13")
                .build();

        movieRequestDto = new MovieRequestDto();
        movieRequestDto.setTitle("Inception");
        movieRequestDto.setDescription("A mind-bending thriller");
        movieRequestDto.setGenre("Sci-Fi");
        movieRequestDto.setDurationMin(148);
        movieRequestDto.setImageUrl("https://example.com/inception.jpg");
        movieRequestDto.setTrailerUrl("https://youtube.com/watch?v=inception");
        movieRequestDto.setDirector("Christopher Nolan");
        movieRequestDto.setMainCast("Leonardo DiCaprio, Marion Cotillard");
        movieRequestDto.setAgeRating("PG-13");
    }

    @Test
    void testAddMovieSuccessfully() {
        // Act
        movieService.addMovie(movieRequestDto);

        // Assert
        verify(movieMapper, times(1)).updateEntityFromDto(eq(movieRequestDto), any(Movie.class));
        verify(movieRepository, times(1)).save(any(Movie.class));
    }

    @Test
    void testAddMovieWithLocalImageFile() {
        // Arrange
        MultipartFile imageFile = new MockMultipartFile("imageFile", "inception.jpg", "image/jpeg", "image content".getBytes());
        movieRequestDto.setImageFile(imageFile);
        movieRequestDto.setImageUrl(null);

        when(fileStorageService.storeFile(eq(imageFile), eq("Inception"))).thenReturn("/uploads/inception.jpg");

        // Act
        movieService.addMovie(movieRequestDto);

        // Assert
        verify(fileStorageService, times(1)).storeFile(eq(imageFile), eq("Inception"));
        verify(movieRepository, times(1)).save(any(Movie.class));
    }

    @Test
    void testAddMovieWithGalleryFiles() {
        // Arrange
        MultipartFile galleryFile1 = new MockMultipartFile("gallery", "gallery1.jpg", "image/jpeg", "image1".getBytes());
        MultipartFile galleryFile2 = new MockMultipartFile("gallery", "gallery2.jpg", "image/jpeg", "image2".getBytes());
        movieRequestDto.setGalleryFiles(List.of(galleryFile1, galleryFile2));

        when(fileStorageService.storeFile(eq(galleryFile1), eq("Inception"))).thenReturn("/uploads/gallery1.jpg");
        when(fileStorageService.storeFile(eq(galleryFile2), eq("Inception"))).thenReturn("/uploads/gallery2.jpg");

        // Act
        movieService.addMovie(movieRequestDto);

        // Assert
        verify(fileStorageService, times(2)).storeFile(any(MultipartFile.class), eq("Inception"));
        verify(movieRepository, times(1)).save(any(Movie.class));
    }

    @Test
    void testAddMovieWithGalleryUrls() {
        // Arrange
        String galleryUrls = "https://example.com/gallery1.jpg\nhttps://example.com/gallery2.jpg";
        movieRequestDto.setGalleryUrlsText(galleryUrls);

        // Act
        movieService.addMovie(movieRequestDto);

        // Assert
        verify(movieRepository, times(1)).save(any(Movie.class));
    }

    @Test
    void testUpdateMovieSuccessfully() {
        // Arrange
        when(movieRepository.findById(eq(1L))).thenReturn(Optional.of(movie));

        // Act
        movieService.updateMovie(1L, movieRequestDto);

        // Assert
        verify(movieRepository, times(1)).findById(eq(1L));
        verify(movieMapper, times(1)).updateEntityFromDto(eq(movieRequestDto), eq(movie));
        verify(movieRepository, times(1)).save(eq(movie));
    }

    @Test
    void testUpdateMovieNotFound() {
        // Arrange
        when(movieRepository.findById(eq(999L))).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> movieService.updateMovie(999L, movieRequestDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Movie not found");
        verify(movieRepository, times(1)).findById(eq(999L));
    }

    @Test
    void testUpdateMovieWithNewLocalImage() {
        // Arrange
        movie.setImageUrl("/uploads/old_image.jpg");
        movieRequestDto.setImageUrl(null);
        MultipartFile newImageFile = new MockMultipartFile("imageFile", "new_inception.jpg", "image/jpeg", "new image".getBytes());
        movieRequestDto.setImageFile(newImageFile);

        when(movieRepository.findById(eq(1L))).thenReturn(Optional.of(movie));
        when(fileStorageService.storeFile(eq(newImageFile), eq("Inception"))).thenReturn("/uploads/new_inception.jpg");

        // Act
        movieService.updateMovie(1L, movieRequestDto);

        // Assert
        verify(fileStorageService, times(1)).deleteFile(eq("/uploads/old_image.jpg"));
        verify(fileStorageService, times(1)).storeFile(eq(newImageFile), eq("Inception"));
        verify(movieRepository, times(1)).save(eq(movie));
    }

    @Test
    void testGetAllMoviesSuccessfully() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Movie> moviePage = new PageImpl<>(List.of(movie), pageable, 1);

        when(movieRepository.findAll(eq(pageable))).thenReturn(moviePage);
        when(movieMapper.toDto(eq(movie))).thenReturn(movieDto);

        // Act
        Page<MovieDto> result = movieService.getAllMovies(pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Inception");
        verify(movieRepository, times(1)).findAll(eq(pageable));


    }

    @Test
    void testGetAllMoviesEmptyList() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Movie> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(movieRepository.findAll(eq(pageable))).thenReturn(emptyPage);

        // Act
        Page<MovieDto> result = movieService.getAllMovies(pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        verify(movieRepository, times(1)).findAll(eq(pageable));
    }

    @Test
    void testGetMovieByIdSuccessfully() {
        // Arrange
        when(movieRepository.findById(eq(1L))).thenReturn(Optional.of(movie));
        when(movieMapper.toDto(eq(movie))).thenReturn(movieDto);

        // Act
        MovieDto result = movieService.getMovieById(1L);

        // Assert
        assertThat(result)
                .isNotNull()
                .hasFieldOrPropertyWithValue("title", "Inception")
                .hasFieldOrPropertyWithValue("durationMin", 148);
        verify(movieRepository, times(1)).findById(eq(1L));
    }

    @Test
    void testGetMovieByIdNotFound() {
        // Arrange
        when(movieRepository.findById(eq(999L))).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> movieService.getMovieById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Movie not found");
        verify(movieRepository, times(1)).findById(eq(999L));
    }

    @Test
    void testDeleteMovieSuccessfully() {
        // Arrange
        when(movieRepository.findById(eq(1L))).thenReturn(Optional.of(movie));

        // Act
        movieService.deleteMovie(1L);

        // Assert
        verify(movieRepository, times(1)).findById(eq(1L));
        verify(movieRepository, times(1)).deleteById(eq(1L));
    }

    @Test
    void testDeleteMovieWithLocalImage() {
        // Arrange
        movie.setImageUrl("/uploads/inception.jpg");
        when(movieRepository.findById(eq(1L))).thenReturn(Optional.of(movie));

        // Act
        movieService.deleteMovie(1L);

        // Assert
        verify(fileStorageService, times(1)).deleteFile(eq("/uploads/inception.jpg"));
        verify(movieRepository, times(1)).deleteById(eq(1L));
    }

    @Test
    void testDeleteMovieNotFound() {
        // Arrange
        when(movieRepository.findById(eq(999L))).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> movieService.deleteMovie(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Movie not found");
        verify(movieRepository, times(1)).findById(eq(999L));
    }

    @Test
    void testRemoveGalleryImageSuccessfully() {
        // Arrange
        movie.getGalleryImages().add("/uploads/gallery1.jpg");
        when(movieRepository.findById(eq(1L))).thenReturn(Optional.of(movie));

        // Act
        movieService.removeGalleryImage(1L, "/uploads/gallery1.jpg");

        // Assert
        assertThat(movie.getGalleryImages())
                .doesNotContain("/uploads/gallery1.jpg");
        verify(fileStorageService, times(1)).deleteFile(eq("/uploads/gallery1.jpg"));
        verify(movieRepository, times(1)).save(eq(movie));
    }

    @Test
    void testRemoveGalleryImageExternalUrl() {
        // Arrange
        movie.getGalleryImages().add("https://example.com/gallery1.jpg");
        when(movieRepository.findById(eq(1L))).thenReturn(Optional.of(movie));

        // Act
        movieService.removeGalleryImage(1L, "https://example.com/gallery1.jpg");

        // Assert
        assertThat(movie.getGalleryImages())
                .doesNotContain("https://example.com/gallery1.jpg");
        verify(fileStorageService, never()).deleteFile(anyString());
        verify(movieRepository, times(1)).save(eq(movie));
    }

    @Test
    void testRemoveGalleryImageNotFound() {
        // Arrange
        when(movieRepository.findById(eq(999L))).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> movieService.removeGalleryImage(999L, "/uploads/gallery1.jpg"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Movie not found");
    }

    @Test
    void testRemoveGalleryImageNotExisting() {
        // Arrange
        when(movieRepository.findById(eq(1L))).thenReturn(Optional.of(movie));

        // Act
        movieService.removeGalleryImage(1L, "/uploads/nonexistent.jpg");

        // Assert
        verify(movieRepository, never()).save(any());
        verify(fileStorageService, never()).deleteFile(anyString());
    }

    @Test
    void testUpdateMovieWithExternalImageUrlChange() {
        // Arrange
        movie.setImageUrl("https://old.example.com/image.jpg");
        movieRequestDto.setImageUrl("https://new.example.com/image.jpg");
        when(movieRepository.findById(eq(1L))).thenReturn(Optional.of(movie));

        // Act
        movieService.updateMovie(1L, movieRequestDto);

        // Assert
        verify(fileStorageService, never()).deleteFile(anyString());
        verify(movieRepository, times(1)).save(eq(movie));
    }

    @Test
    void testUpdateMovieWithLocalImageUrlToExternalUrl() {
        // Arrange
        movie.setImageUrl("/uploads/old_image.jpg");
        movieRequestDto.setImageUrl("https://new.example.com/image.jpg");
        when(movieRepository.findById(eq(1L))).thenReturn(Optional.of(movie));

        // Act
        movieService.updateMovie(1L, movieRequestDto);

        // Assert
        verify(fileStorageService, times(1)).deleteFile(eq("/uploads/old_image.jpg"));
        verify(movieRepository, times(1)).save(eq(movie));
    }

    @Test
    void testUpdateMovieWithoutImageChange() {
        // Arrange
        when(movieRepository.findById(eq(1L))).thenReturn(Optional.of(movie));

        // Act
        movieService.updateMovie(1L, movieRequestDto);

        // Assert
        verify(fileStorageService, never()).deleteFile(anyString());
        verify(movieRepository, times(1)).save(eq(movie));
    }
}
