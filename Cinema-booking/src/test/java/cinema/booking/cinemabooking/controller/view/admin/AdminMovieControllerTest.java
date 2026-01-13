package cinema.booking.cinemabooking.controller.view.admin;

import cinema.booking.cinemabooking.config.SecurityConfig;
import cinema.booking.cinemabooking.controller.view.GlobalControllerAdvice;
import cinema.booking.cinemabooking.dto.request.MovieRequestDto;
import cinema.booking.cinemabooking.dto.response.MovieDto;
import cinema.booking.cinemabooking.mapper.MovieMapper;
import cinema.booking.cinemabooking.service.MovieService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminMovieController.class)
@Import({SecurityConfig.class, GlobalControllerAdvice.class})
@DisplayName("View Tests for AdminMovieController")
class AdminMovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MovieService movieService;

    @MockitoBean
    private MovieMapper movieMapper;

    @Test
    @DisplayName("Scenario 1: List movies - not authenticated - redirect to login")
    void testListMovies_NotAuthenticated() throws Exception {
        mockMvc.perform(get("/admin/movies"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @DisplayName("Scenario 2: List movies - user without admin role - access denied")
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testListMovies_NoAdminRole() throws Exception {
        mockMvc.perform(get("/admin/movies"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Scenario 3: List movies - admin user - success")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testListMovies_Success() throws Exception {
        MovieDto movie1 = MovieDto.builder()
                .id(1L)
                .title("Inception")
                .genre("Sci-Fi")
                .durationMin(148)
                .imageUrl("/images/inception.jpg")
                .build();

        MovieDto movie2 = MovieDto.builder()
                .id(2L)
                .title("The Matrix")
                .genre("Sci-Fi")
                .durationMin(136)
                .imageUrl("/images/matrix.jpg")
                .build();

        Page<MovieDto> page = new PageImpl<>(List.of(movie1, movie2));
        when(movieService.getAllMovies(any())).thenReturn(page);

        mockMvc.perform(get("/admin/movies")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/movies-list"))
                .andExpect(model().attributeExists("movies"));

        verify(movieService, times(1)).getAllMovies(any());
    }

    @Test
    @DisplayName("Scenario 4: List movies - empty list")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testListMovies_EmptyList() throws Exception {
        Page<MovieDto> emptyPage = new PageImpl<>(List.of());
        when(movieService.getAllMovies(any())).thenReturn(emptyPage);

        mockMvc.perform(get("/admin/movies"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/movies-list"))
                .andExpect(model().attribute("movies", emptyPage));

        verify(movieService, times(1)).getAllMovies(any());
    }

    @Test
    @DisplayName("Scenario 5: Add movie form - not authenticated - redirect to login")
    void testAddMovieForm_NotAuthenticated() throws Exception {
        mockMvc.perform(get("/admin/movies/add"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @DisplayName("Scenario 6: Add movie form - admin user - success")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testAddMovieForm_Success() throws Exception {
        mockMvc.perform(get("/admin/movies/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/movie-form"))
                .andExpect(model().attributeExists("movie"))
                .andExpect(model().attribute("isEdit", false));
    }

    @Test
    @DisplayName("Scenario 7: Submit add movie - admin user - success")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testAddMovie_Success() throws Exception {
        doNothing().when(movieService).addMovie(any(MovieRequestDto.class));

        mockMvc.perform(post("/admin/movies/add")
                        .param("title", "Inception")
                        .param("genre", "Sci-Fi")
                        .param("durationMin", "148")
                        .param("director", "Christopher Nolan")
                        .param("imageUrl", "/images/inception.jpg"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/movies"));

        verify(movieService, times(1)).addMovie(any(MovieRequestDto.class));
    }

    @Test
    @DisplayName("Scenario 8: Edit movie form - not authenticated - redirect to login")
    void testEditMovieForm_NotAuthenticated() throws Exception {
        mockMvc.perform(get("/admin/movies/edit/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @DisplayName("Scenario 9: Edit movie form - admin user - success")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testEditMovieForm_Success() throws Exception {
        MovieDto movieDto = MovieDto.builder()
                .id(1L)
                .title("Inception")
                .genre("Sci-Fi")
                .durationMin(148)
                .imageUrl("/images/inception.jpg")
                .galleryImages(List.of("/images/gallery1.jpg", "/images/gallery2.jpg"))
                .build();

        MovieRequestDto requestDto = new MovieRequestDto();
        requestDto.setTitle("Inception");
        requestDto.setGenre("Sci-Fi");

        when(movieService.getMovieById(1L)).thenReturn(movieDto);
        when(movieMapper.toRequestDto(movieDto)).thenReturn(requestDto);

        mockMvc.perform(get("/admin/movies/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/movie-form"))
                .andExpect(model().attributeExists("movie", "movieId", "isEdit", "currentGallery"))
                .andExpect(model().attribute("movieId", 1L))
                .andExpect(model().attribute("isEdit", true));

        verify(movieService, times(1)).getMovieById(1L);
        verify(movieMapper, times(1)).toRequestDto(movieDto);
    }

    @Test
    @DisplayName("Scenario 10: Edit movie form - movie not found")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testEditMovieForm_NotFound() throws Exception {
        when(movieService.getMovieById(999L))
                .thenThrow(new RuntimeException("Movie not found"));

        mockMvc.perform(get("/admin/movies/edit/999"))
                .andExpect(status().is5xxServerError());

        verify(movieService, times(1)).getMovieById(999L);
    }

    @Test
    @DisplayName("Scenario 11: Submit update movie - admin user - success")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUpdateMovie_Success() throws Exception {
        doNothing().when(movieService).updateMovie(eq(1L), any(MovieRequestDto.class));

        mockMvc.perform(post("/admin/movies/edit/1")
                        .param("title", "Inception Updated")
                        .param("genre", "Sci-Fi")
                        .param("durationMin", "150")
                        .param("director", "Christopher Nolan"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/movies"));

        verify(movieService, times(1)).updateMovie(eq(1L), any(MovieRequestDto.class));
    }

    @Test
    @DisplayName("Scenario 12: Delete movie - not authenticated - redirect to login")
    void testDeleteMovie_NotAuthenticated() throws Exception {
        mockMvc.perform(post("/admin/movies/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @DisplayName("Scenario 13: Delete movie - admin user - success")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testDeleteMovie_Success() throws Exception {
        doNothing().when(movieService).deleteMovie(1L);

        mockMvc.perform(post("/admin/movies/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/movies"));

        verify(movieService, times(1)).deleteMovie(1L);
    }

    @Test
    @DisplayName("Scenario 14: Delete gallery image - admin user - success")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testDeleteGalleryImage_Success() throws Exception {
        doNothing().when(movieService).removeGalleryImage(eq(1L), anyString());

        mockMvc.perform(delete("/admin/movies/edit/1/gallery")
                        .param("image", "/images/gallery1.jpg"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/movies/edit/1"));

        verify(movieService, times(1)).removeGalleryImage(eq(1L), eq("/images/gallery1.jpg"));
    }
}
