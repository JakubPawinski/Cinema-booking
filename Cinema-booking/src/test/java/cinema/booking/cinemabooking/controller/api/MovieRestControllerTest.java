package cinema.booking.cinemabooking.controller.api;

import cinema.booking.cinemabooking.config.SecurityConfig;
import cinema.booking.cinemabooking.dto.response.MovieDto;
import cinema.booking.cinemabooking.dto.request.MovieRequestDto;
import cinema.booking.cinemabooking.exception.ResourceNotFoundException;
import cinema.booking.cinemabooking.service.MovieService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;

import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MovieRestController.class)
@Import(SecurityConfig.class)
@DisplayName("REST API Tests for MovieRestController")
class MovieRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MovieService movieService;

    private ObjectMapper objectMapper;
    private MovieDto movieDto;
    private MovieRequestDto movieRequestDto;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        movieDto = MovieDto.builder()
                .id(1L)
                .title("Test Movie")
                .description("Test description")
                .genre("Action")
                .durationMin(120)
                .director("Test Director")
                .mainCast("Test Actor")
                .ageRating("PG-13")
                .imageUrl("http://example.com/image.jpg")
                .trailerUrl("http://example.com/trailer.mp4")
                .build();

        movieRequestDto = new MovieRequestDto();
        movieRequestDto.setTitle("New Movie");
        movieRequestDto.setDescription("New description");
        movieRequestDto.setGenre("Comedy");
        movieRequestDto.setDurationMin(100);
        movieRequestDto.setDirector("New Director");
        movieRequestDto.setMainCast("New Actor");
        movieRequestDto.setAgeRating("PG");
        movieRequestDto.setImageUrl("http://example.com/image2.jpg");
        movieRequestDto.setTrailerUrl("http://example.com/trailer2.mp4");
    }

    // ============= GET ALL MOVIES =============

    @Test
    @DisplayName("Scenario 1: Get all movies - public access")
    @WithMockUser(roles = "USER")
    void testGetAllMovies_PublicAccess_Success() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<MovieDto> moviePage = new PageImpl<>(
                List.of(movieDto),
                pageable,
                1
        );

        when(movieService.getAllMovies(any(Pageable.class))).thenReturn(moviePage);

        mockMvc.perform(get("/api/v1/movies")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].title").value("Test Movie"));

        verify(movieService, times(1)).getAllMovies(any(Pageable.class));
    }

    @Test
    @DisplayName("Scenario 2: Get movies with pagination - second page")
    @WithMockUser(roles = "USER")
    void testGetAllMovies_SecondPage() throws Exception {
        MovieDto movieDto2 = MovieDto.builder()
                .id(2L)
                .title("Second Movie")
                .description("Description 2")
                .genre("Drama")
                .durationMin(140)
                .director("Director 2")
                .mainCast("Actor 2")
                .ageRating("PG-13")
                .imageUrl("http://example.com/image2.jpg")
                .trailerUrl("http://example.com/trailer2.mp4")
                .build();

        Pageable pageable = PageRequest.of(1, 10);
        Page<MovieDto> moviePage = new PageImpl<>(
                List.of(movieDto2),
                pageable,
                20
        );

        when(movieService.getAllMovies(any(Pageable.class))).thenReturn(moviePage);

        mockMvc.perform(get("/api/v1/movies")
                        .param("page", "1")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(2L))
                .andExpect(jsonPath("$.totalElements").value(20));

        verify(movieService, times(1)).getAllMovies(any(Pageable.class));
    }

    @Test
    @DisplayName("Scenario 3: Get all movies - empty list")
    @WithMockUser(roles = "USER")
    void testGetAllMovies_EmptyList() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<MovieDto> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(movieService.getAllMovies(any(Pageable.class))).thenReturn(emptyPage);

        mockMvc.perform(get("/api/v1/movies")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(0));

        verify(movieService, times(1)).getAllMovies(any(Pageable.class));
    }

    // ============= GET MOVIE BY ID =============

    @Test
    @DisplayName("Scenario 4: Get movie by ID - public access")
    @WithMockUser(roles = "USER")
    void testGetMovieById_PublicAccess_Success() throws Exception {
        when(movieService.getMovieById(1L)).thenReturn(movieDto);

        mockMvc.perform(get("/api/v1/movies/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Movie"));

        verify(movieService, times(1)).getMovieById(1L);
    }

    @Test
    @DisplayName("Scenario 5: Get movie - movie not found")
    @WithMockUser(roles = "USER")
    void testGetMovieById_NotFound() throws Exception {
        when(movieService.getMovieById(999L))
                .thenThrow(new ResourceNotFoundException("Movie not found"));

        mockMvc.perform(get("/api/v1/movies/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(movieService, times(1)).getMovieById(999L);
    }

    // ============= ADD MOVIE =============

    @Test
    @DisplayName("Scenario 6: Add movie - admin only")
    @WithMockUser(roles = "ADMIN")
    void testAddMovie_AdminOnly_Success() throws Exception {
        doNothing().when(movieService).addMovie(any(MovieRequestDto.class));

        mockMvc.perform(post("/api/v1/movies")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", "New Movie")
                        .param("description", "New description")
                        .param("genre", "Comedy")
                        .param("durationMin", "100")
                        .param("director", "New Director")
                        .param("mainCast", "New Actor")
                        .param("ageRating", "PG")
                        .param("imageUrl", "http://example.com/image2.jpg")
                        .param("trailerUrl", "http://example.com/trailer2.mp4"))
                .andExpect(status().isCreated());

        verify(movieService, times(1)).addMovie(any(MovieRequestDto.class));
    }

    @Test
    @DisplayName("Scenario 7: Add movie - access denied for users")
    @WithMockUser(roles = "USER")
    void testAddMovie_UserDenied() throws Exception {
        mockMvc.perform(post("/api/v1/movies")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", "New Movie")
                        .param("description", "New description")
                        .param("genre", "Comedy")
                        .param("durationMin", "100")
                        .param("director", "New Director")
                        .param("mainCast", "New Actor")
                        .param("ageRating", "PG")
                        .param("imageUrl", "http://example.com/image2.jpg")
                        .param("trailerUrl", "http://example.com/trailer2.mp4"))
                .andExpect(status().isForbidden());

        verify(movieService, never()).addMovie(any(MovieRequestDto.class));
    }

    @Test
    @DisplayName("Scenario 8: Add movie - no authentication")
    void testAddMovie_Unauthenticated() throws Exception {
        mockMvc.perform(post("/api/v1/movies")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", "New Movie")
                        .param("description", "New description")
                        .param("genre", "Comedy")
                        .param("durationMin", "100")
                        .param("director", "New Director")
                        .param("mainCast", "New Actor")
                        .param("ageRating", "PG")
                        .param("imageUrl", "http://example.com/image2.jpg")
                        .param("trailerUrl", "http://example.com/trailer2.mp4"))
                .andExpect(status().is3xxRedirection());

        verify(movieService, never()).addMovie(any(MovieRequestDto.class));
    }

    @Test
    @DisplayName("Scenario 9: Add movie - validation errors")
    @WithMockUser(roles = "ADMIN")
    void testAddMovie_ValidationErrors() throws Exception {
        mockMvc.perform(post("/api/v1/movies")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", "")
                        .param("description", "")
                        .param("genre", ""))
                .andExpect(status().isBadRequest());

        verify(movieService, never()).addMovie(any(MovieRequestDto.class));
    }

    @Test
    @DisplayName("Scenario 10: Add movie - title validation (too short)")
    @WithMockUser(roles = "ADMIN")
    void testAddMovie_InvalidTitle() throws Exception {
        mockMvc.perform(post("/api/v1/movies")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", "A")
                        .param("description", "Valid description")
                        .param("genre", "Action")
                        .param("durationMin", "100")
                        .param("director", "Director")
                        .param("mainCast", "Actor")
                        .param("ageRating", "PG-13")
                        .param("imageUrl", "http://example.com/image.jpg")
                        .param("trailerUrl", "http://example.com/trailer.mp4"))
                .andExpect(status().isBadRequest());

        verify(movieService, never()).addMovie(any(MovieRequestDto.class));
    }

    @Test
    @DisplayName("Scenario 11: Add movie - URL validation")
    @WithMockUser(roles = "ADMIN")
    void testAddMovie_InvalidImageUrl() throws Exception {
        mockMvc.perform(post("/api/v1/movies")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("title", "Valid Title")
                        .param("description", "Valid description")
                        .param("genre", "Action")
                        .param("durationMin", "100")
                        .param("director", "Director")
                        .param("mainCast", "Actor")
                        .param("ageRating", "PG-13")
                        .param("imageUrl", "not-a-valid-url")
                        .param("trailerUrl", "http://example.com/trailer.mp4"))
                .andExpect(status().isBadRequest());

        verify(movieService, never()).addMovie(any(MovieRequestDto.class));
    }

    // ============= DELETE MOVIE =============

    @Test
    @DisplayName("Scenario 12: Delete movie - admin only")
    @WithMockUser(roles = "ADMIN")
    void testDeleteMovie_AdminOnly_Success() throws Exception {
        doNothing().when(movieService).deleteMovie(1L);

        mockMvc.perform(delete("/api/v1/movies/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(movieService, times(1)).deleteMovie(eq(1L));
    }

    @Test
    @DisplayName("Scenario 13: Delete movie - access denied for users")
    @WithMockUser(roles = "USER")
    void testDeleteMovie_UserDenied() throws Exception {
        mockMvc.perform(delete("/api/v1/movies/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(movieService, never()).deleteMovie(anyLong());
    }

    @Test
    @DisplayName("Scenario 14: Delete movie - no authentication")
    void testDeleteMovie_Unauthenticated() throws Exception {
        mockMvc.perform(delete("/api/v1/movies/1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        verify(movieService, never()).deleteMovie(anyLong());
    }

    @Test
    @DisplayName("Scenario 15: Delete movie - movie not found")
    @WithMockUser(roles = "ADMIN")
    void testDeleteMovie_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Movie not found"))
                .when(movieService).deleteMovie(999L);

        mockMvc.perform(delete("/api/v1/movies/999")
                        .with(csrf()))
                .andExpect(status().isNotFound());

        verify(movieService, times(1)).deleteMovie(eq(999L));
    }
}
