package cinema.booking.cinemabooking.controller.api;

import cinema.booking.cinemabooking.config.SecurityConfig;
import cinema.booking.cinemabooking.dto.response.MovieWithSeancesDto;
import cinema.booking.cinemabooking.dto.response.SeanceDto;
import cinema.booking.cinemabooking.service.SeanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RepertoireRestController.class)
@Import(SecurityConfig.class)
@DisplayName("REST API Tests for RepertoireRestController")
class RepertoireRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SeanceService seanceService;

    private MovieWithSeancesDto movieDto1;
    private MovieWithSeancesDto movieDto2;
    private MovieWithSeancesDto movieDto3;

    @BeforeEach
    void setUp() {
        SeanceDto seance1 = SeanceDto.builder()
                .id(1L)
                .startTime(LocalDateTime.of(2025, 1, 15, 17, 0))
                .endTime(LocalDateTime.of(2025, 1, 15, 18, 40))
                .regularTicketPrice(12.50)
                .reducedTicketPrice(9.00)
                .roomName("Room A")
                .movieId(1L)
                .movieTitle("Movie 1")
                .build();

        SeanceDto seance2 = SeanceDto.builder()
                .id(2L)
                .startTime(LocalDateTime.of(2025, 1, 15, 19, 0))
                .endTime(LocalDateTime.of(2025, 1, 15, 20, 40))
                .regularTicketPrice(12.50)
                .reducedTicketPrice(9.00)
                .roomName("Room B")
                .movieId(1L)
                .movieTitle("Movie 1")
                .build();

        movieDto1 = MovieWithSeancesDto.builder()
                .movieId(1L)
                .title("Movie 1")
                .description("Description 1")
                .genre("Action")
                .durationMin(120)
                .imageUrl("http://example.com/movie1.jpg")
                .seances(List.of(seance1, seance2))
                .build();

        SeanceDto seance3 = SeanceDto.builder()
                .id(3L)
                .startTime(LocalDateTime.of(2025, 1, 15, 20, 0))
                .endTime(LocalDateTime.of(2025, 1, 15, 21, 40))
                .regularTicketPrice(12.50)
                .reducedTicketPrice(9.00)
                .roomName("Room C")
                .movieId(2L)
                .movieTitle("Movie 2")
                .build();

        movieDto2 = MovieWithSeancesDto.builder()
                .movieId(2L)
                .title("Movie 2")
                .description("Description 2")
                .genre("Drama")
                .durationMin(140)
                .imageUrl("http://example.com/movie2.jpg")
                .seances(List.of(seance3))
                .build();

        movieDto3 = MovieWithSeancesDto.builder()
                .movieId(3L)
                .title("Movie 3")
                .description("Description 3")
                .genre("Comedy")
                .durationMin(100)
                .imageUrl("http://example.com/movie3.jpg")
                .seances(List.of())
                .build();
    }

    // ============= GET REPERTOIRE - BASIC SCENARIOS =============

    @Test
    @DisplayName("Scenario 1: Get repertoire for specific date - public access")
    void testGetRepertoire_WithSpecificDate_Success() throws Exception {
        LocalDate testDate = LocalDate.of(2025, 1, 15);
        List<MovieWithSeancesDto> repertoire = List.of(movieDto1, movieDto2);

        when(seanceService.getRepertoireForDate(testDate)).thenReturn(repertoire);

        mockMvc.perform(get("/api/v1/repertoires")
                        .param("date", "2025-01-15")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].movieId").value(1L))
                .andExpect(jsonPath("$[0].title").value("Movie 1"))
                .andExpect(jsonPath("$[0].genre").value("Action"))
                .andExpect(jsonPath("$[0].durationMin").value(120))
                .andExpect(jsonPath("$[0].seances.length()").value(2))
                .andExpect(jsonPath("$[0].seances[0].id").value(1L))
                .andExpect(jsonPath("$[0].seances[0].roomName").value("Room A"))
                .andExpect(jsonPath("$[0].seances[0].regularTicketPrice").value(12.50))
                .andExpect(jsonPath("$[1].movieId").value(2L))
                .andExpect(jsonPath("$[1].title").value("Movie 2"))
                .andExpect(jsonPath("$[1].genre").value("Drama"));

        verify(seanceService, times(1)).getRepertoireForDate(testDate);
    }

    @Test
    @DisplayName("Scenario 2: Get repertoire without date - uses today")
    void testGetRepertoire_WithoutDate_UsesCurrentDate() throws Exception {
        LocalDate currentDate = LocalDate.now();
        List<MovieWithSeancesDto> repertoire = List.of(movieDto1);

        when(seanceService.getRepertoireForDate(currentDate)).thenReturn(repertoire);

        mockMvc.perform(get("/api/v1/repertoires")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Movie 1"))
                .andExpect(jsonPath("$[0].genre").value("Action"));

        verify(seanceService, times(1)).getRepertoireForDate(currentDate);
    }

    @Test
    @DisplayName("Scenario 3: Get repertoire - invalid date format")
    void testGetRepertoire_InvalidDateFormat_BadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/repertoires")
                        .param("date", "invalid-date")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(seanceService, never()).getRepertoireForDate(any(LocalDate.class));
    }

    @Test
    @DisplayName("Scenario 4: Get repertoire - empty repertoire")
    void testGetRepertoire_EmptyRepertoire() throws Exception {
        LocalDate testDate = LocalDate.of(2025, 1, 20);

        when(seanceService.getRepertoireForDate(testDate)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/repertoires")
                        .param("date", "2025-01-20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(seanceService, times(1)).getRepertoireForDate(testDate);
    }

    @Test
    @DisplayName("Scenario 5: Get repertoire - movie without seances")
    void testGetRepertoire_MovieWithoutSeances() throws Exception {
        LocalDate testDate = LocalDate.of(2025, 1, 25);
        List<MovieWithSeancesDto> repertoire = List.of(movieDto3);

        when(seanceService.getRepertoireForDate(testDate)).thenReturn(repertoire);

        mockMvc.perform(get("/api/v1/repertoires")
                        .param("date", "2025-01-25")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Movie 3"))
                .andExpect(jsonPath("$[0].seances.length()").value(0));

        verify(seanceService, times(1)).getRepertoireForDate(testDate);
    }

    // ============= GET REPERTOIRE - ADVANCED SCENARIOS =============

    @Test
    @DisplayName("Scenario 6: Get repertoire - multiple movies with multiple seances")
    void testGetRepertoire_MultipleMoviesMultipleSeances() throws Exception {
        LocalDate testDate = LocalDate.of(2025, 2, 1);
        List<MovieWithSeancesDto> repertoire = List.of(movieDto1, movieDto2, movieDto3);

        when(seanceService.getRepertoireForDate(testDate)).thenReturn(repertoire);

        mockMvc.perform(get("/api/v1/repertoires")
                        .param("date", "2025-02-01")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].seances.length()").value(2))
                .andExpect(jsonPath("$[1].seances.length()").value(1))
                .andExpect(jsonPath("$[2].seances.length()").value(0))
                .andExpect(jsonPath("$[0].title").value("Movie 1"))
                .andExpect(jsonPath("$[1].title").value("Movie 2"))
                .andExpect(jsonPath("$[2].title").value("Movie 3"));

        verify(seanceService, times(1)).getRepertoireForDate(testDate);
    }

    @Test
    @DisplayName("Scenario 7: Get repertoire - authenticated user")
    @WithMockUser(username = "testUser", roles = {"USER"})
    void testGetRepertoire_WithAuthenticatedUser() throws Exception {
        LocalDate testDate = LocalDate.of(2025, 3, 1);
        List<MovieWithSeancesDto> repertoire = List.of(movieDto1);

        when(seanceService.getRepertoireForDate(testDate)).thenReturn(repertoire);

        mockMvc.perform(get("/api/v1/repertoires")
                        .param("date", "2025-03-01")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Movie 1"));

        verify(seanceService, times(1)).getRepertoireForDate(testDate);
    }

    @Test
    @DisplayName("Scenario 8: Get repertoire - administrator")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetRepertoire_WithAdminUser() throws Exception {
        LocalDate testDate = LocalDate.of(2025, 3, 5);
        List<MovieWithSeancesDto> repertoire = List.of(movieDto2);

        when(seanceService.getRepertoireForDate(testDate)).thenReturn(repertoire);

        mockMvc.perform(get("/api/v1/repertoires")
                        .param("date", "2025-03-05")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Movie 2"));

        verify(seanceService, times(1)).getRepertoireForDate(testDate);
    }

    @Test
    @DisplayName("Scenario 9: Get repertoire - ticket prices validation")
    void testGetRepertoire_TicketPrices() throws Exception {
        LocalDate testDate = LocalDate.of(2025, 4, 1);
        List<MovieWithSeancesDto> repertoire = List.of(movieDto1);

        when(seanceService.getRepertoireForDate(testDate)).thenReturn(repertoire);

        mockMvc.perform(get("/api/v1/repertoires")
                        .param("date", "2025-04-01")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].seances[0].regularTicketPrice").value(12.50))
                .andExpect(jsonPath("$[0].seances[0].reducedTicketPrice").value(9.00));

        verify(seanceService, times(1)).getRepertoireForDate(testDate);
    }

    @Test
    @DisplayName("Scenario 10: Get repertoire - different rooms")
    void testGetRepertoire_DifferentRooms() throws Exception {
        LocalDate testDate = LocalDate.of(2025, 5, 1);

        SeanceDto seance1 = SeanceDto.builder()
                .id(1L)
                .startTime(LocalDateTime.of(2025, 5, 1, 17, 0))
                .endTime(LocalDateTime.of(2025, 5, 1, 18, 40))
                .regularTicketPrice(12.00)
                .reducedTicketPrice(8.00)
                .roomName("Room 1")
                .movieId(1L)
                .movieTitle("Multi-room Movie")
                .build();

        SeanceDto seance2 = SeanceDto.builder()
                .id(2L)
                .startTime(LocalDateTime.of(2025, 5, 1, 19, 0))
                .endTime(LocalDateTime.of(2025, 5, 1, 20, 40))
                .regularTicketPrice(12.00)
                .reducedTicketPrice(8.00)
                .roomName("Room 2 IMAX")
                .movieId(1L)
                .movieTitle("Multi-room Movie")
                .build();

        SeanceDto seance3 = SeanceDto.builder()
                .id(3L)
                .startTime(LocalDateTime.of(2025, 5, 1, 21, 0))
                .endTime(LocalDateTime.of(2025, 5, 1, 22, 40))
                .regularTicketPrice(12.00)
                .reducedTicketPrice(8.00)
                .roomName("Room 3")
                .movieId(1L)
                .movieTitle("Multi-room Movie")
                .build();

        MovieWithSeancesDto multiRoomMovie = MovieWithSeancesDto.builder()
                .movieId(1L)
                .title("Multi-room Movie")
                .description("Movie in multiple rooms")
                .genre("Action")
                .durationMin(120)
                .imageUrl("http://example.com/multiroom.jpg")
                .seances(List.of(seance1, seance2, seance3))
                .build();

        List<MovieWithSeancesDto> repertoire = List.of(multiRoomMovie);

        when(seanceService.getRepertoireForDate(testDate)).thenReturn(repertoire);

        mockMvc.perform(get("/api/v1/repertoires")
                        .param("date", "2025-05-01")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].seances[0].roomName").value("Room 1"))
                .andExpect(jsonPath("$[0].seances[1].roomName").value("Room 2 IMAX"))
                .andExpect(jsonPath("$[0].seances[2].roomName").value("Room 3"));

        verify(seanceService, times(1)).getRepertoireForDate(testDate);
    }
}
