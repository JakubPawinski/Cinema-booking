package cinema.booking.cinemabooking.controller.view.admin;

import cinema.booking.cinemabooking.config.SecurityConfig;
import cinema.booking.cinemabooking.controller.view.GlobalControllerAdvice;
import cinema.booking.cinemabooking.dto.request.SeanceRequestDto;
import cinema.booking.cinemabooking.dto.response.SeanceDto;
import cinema.booking.cinemabooking.model.CinemaRoom;
import cinema.booking.cinemabooking.model.Movie;
import cinema.booking.cinemabooking.repository.CinemaRoomRepository;
import cinema.booking.cinemabooking.repository.MovieRepository;
import cinema.booking.cinemabooking.service.SeanceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminSeanceController.class)
@Import({SecurityConfig.class, GlobalControllerAdvice.class})
@DisplayName("View Tests for AdminSeanceController")
class AdminSeanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SeanceService seanceService;

    @MockitoBean
    private MovieRepository movieRepository;

    @MockitoBean
    private CinemaRoomRepository roomRepository;

    @Test
    @DisplayName("Scenario 1: List seances - not authenticated - redirect to login")
    void testListSeances_NotAuthenticated() throws Exception {
        mockMvc.perform(get("/admin/seances"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @DisplayName("Scenario 2: List seances - user without admin role - access denied")
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testListSeances_NoAdminRole() throws Exception {
        mockMvc.perform(get("/admin/seances"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Scenario 3: List seances - admin user - success")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testListSeances_Success() throws Exception {
        SeanceDto seance1 = SeanceDto.builder()
                .id(1L)
                .movieTitle("Inception")
                .startTime(LocalDateTime.of(2024, 12, 20, 18, 30))
                .endTime(LocalDateTime.of(2024, 12, 20, 20, 30))
                .regularTicketPrice(12.50)
                .reducedTicketPrice(8.00)
                .roomName("Room A")
                .movieId(1L)
                .build();

        SeanceDto seance2 = SeanceDto.builder()
                .id(2L)
                .movieTitle("The Matrix")
                .startTime(LocalDateTime.of(2024, 12, 20, 21, 0))
                .endTime(LocalDateTime.of(2024, 12, 20, 23, 0))
                .regularTicketPrice(12.50)
                .reducedTicketPrice(8.00)
                .roomName("Room B")
                .movieId(2L)
                .build();

        when(seanceService.getAllSeances()).thenReturn(List.of(seance1, seance2));

        mockMvc.perform(get("/admin/seances"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/seances-list"))
                .andExpect(model().attributeExists("seances"));

        verify(seanceService, times(1)).getAllSeances();
    }

    @Test
    @DisplayName("Scenario 4: List seances - empty list")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testListSeances_EmptyList() throws Exception {
        when(seanceService.getAllSeances()).thenReturn(List.of());

        mockMvc.perform(get("/admin/seances"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/seances-list"))
                .andExpect(model().attribute("seances", List.of()));

        verify(seanceService, times(1)).getAllSeances();
    }

    @Test
    @DisplayName("Scenario 5: Add seance form - not authenticated - redirect to login")
    void testAddSeanceForm_NotAuthenticated() throws Exception {
        mockMvc.perform(get("/admin/seances/add"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @DisplayName("Scenario 6: Add seance form - admin user - success")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testAddSeanceForm_Success() throws Exception {
        Movie movie = new Movie();
        movie.setId(1L);
        movie.setTitle("Inception");

        CinemaRoom room = new CinemaRoom();
        room.setId(1L);
        room.setName("Room A");

        when(movieRepository.findAll()).thenReturn(List.of(movie));
        when(roomRepository.findAll()).thenReturn(List.of(room));

        mockMvc.perform(get("/admin/seances/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/seance-form"))
                .andExpect(model().attributeExists("seance", "movies", "rooms"));

        verify(movieRepository, times(1)).findAll();
        verify(roomRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Scenario 7: Submit add seance - admin user - success")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testAddSeance_Success() throws Exception {
        doNothing().when(seanceService).createSeance(any(SeanceRequestDto.class));

        mockMvc.perform(post("/admin/seances/add")
                        .param("movieId", "1")
                        .param("roomId", "1")
                        .param("startTime", "2024-12-20T18:30:00")
                        .param("regularTicketPrice", "12.50")
                        .param("reducedTicketPrice", "8.00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/seances"))
                .andExpect(flash().attributeExists("success"));

        verify(seanceService, times(1)).createSeance(any(SeanceRequestDto.class));
    }

    @Test
    @DisplayName("Scenario 8: Submit add seance - conflict - seance time conflict")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testAddSeance_Conflict() throws Exception {
        Movie movie = new Movie();
        movie.setId(1L);
        movie.setTitle("Inception");

        CinemaRoom room = new CinemaRoom();
        room.setId(1L);
        room.setName("Room A");

        doThrow(new IllegalStateException("Seance time conflicts with existing seance"))
                .when(seanceService).createSeance(any(SeanceRequestDto.class));

        when(movieRepository.findAll()).thenReturn(List.of(movie));
        when(roomRepository.findAll()).thenReturn(List.of(room));

        mockMvc.perform(post("/admin/seances/add")
                        .param("movieId", "1")
                        .param("roomId", "1")
                        .param("startTime", "2024-12-20T18:30:00")
                        .param("regularTicketPrice", "12.50")
                        .param("reducedTicketPrice", "8.00"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/seance-form"))
                .andExpect(model().attributeExists("error", "movies", "rooms"));

        verify(seanceService, times(1)).createSeance(any(SeanceRequestDto.class));
        verify(movieRepository, times(1)).findAll();
        verify(roomRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Scenario 9: Delete seance - not authenticated - redirect to login")
    void testDeleteSeance_NotAuthenticated() throws Exception {
        mockMvc.perform(post("/admin/seances/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @DisplayName("Scenario 10: Delete seance - user without admin role - access denied")
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testDeleteSeance_NoAdminRole() throws Exception {
        mockMvc.perform(post("/admin/seances/delete/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Scenario 11: Delete seance - admin user - success")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testDeleteSeance_Success() throws Exception {
        doNothing().when(seanceService).deleteSeance(1L);

        mockMvc.perform(post("/admin/seances/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/seances"));

        verify(seanceService, times(1)).deleteSeance(1L);
    }
}
