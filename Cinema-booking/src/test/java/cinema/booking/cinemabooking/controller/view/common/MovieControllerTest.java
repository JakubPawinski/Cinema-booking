package cinema.booking.cinemabooking.controller.view.common;

import cinema.booking.cinemabooking.config.SecurityConfig;
import cinema.booking.cinemabooking.dto.response.MovieDto;
import cinema.booking.cinemabooking.service.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MovieController.class)
@Import(SecurityConfig.class)
@DisplayName("View Tests for MovieController")
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MovieService movieService;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("Scenario 1: Movies view - public access")
    void testMoviesView() throws Exception {
        mockMvc.perform(get("/movies"))
                .andExpect(status().isOk())
                .andExpect(view().name("movies/list"));
    }

    @Test
    @DisplayName("Scenario 2: Movie detail view - valid movie ID")
    void testMovieDetailView_WithValidMovieId() throws Exception {
        Long movieId = 1L;
        MovieDto movieDto = MovieDto.builder()
                .id(movieId)
                .title("Test Movie")
                .build();

        when(movieService.getMovieById(movieId)).thenReturn(movieDto);

        mockMvc.perform(get("/movies/{movieId}", movieId))
                .andExpect(status().isOk())
                .andExpect(view().name("movies/details"))
                .andExpect(model().attributeExists("movie"))
                .andExpect(model().attribute("movie", movieDto));

        verify(movieService, times(1)).getMovieById(movieId);
    }

    @Test
    @DisplayName("Scenario 3: Movie detail view - verify service call")
    void testMovieDetailView_VerifyServiceCall() throws Exception {
        Long movieId = 2L;
        MovieDto movieDto = MovieDto.builder()
                .id(movieId)
                .title("Another Movie")
                .build();

        when(movieService.getMovieById(movieId)).thenReturn(movieDto);

        mockMvc.perform(get("/movies/{movieId}", movieId));

        verify(movieService).getMovieById(movieId);
    }
}
