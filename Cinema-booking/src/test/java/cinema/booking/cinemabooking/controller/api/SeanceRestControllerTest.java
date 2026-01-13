package cinema.booking.cinemabooking.controller.api;

import cinema.booking.cinemabooking.config.SecurityConfig;
import cinema.booking.cinemabooking.dto.response.SeatDto;
import cinema.booking.cinemabooking.exception.ResourceNotFoundException;
import cinema.booking.cinemabooking.service.SeanceService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SeanceRestController.class)
@Import(SecurityConfig.class)
@DisplayName("REST API Tests for SeanceRestController")
class SeanceRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SeanceService seanceService;

    private ObjectMapper objectMapper;
    private List<SeatDto> seatDtos;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        seatDtos = new ArrayList<>();
        seatDtos.add(SeatDto.builder().id(1L).rowNumber(1).seatNumber(1).isOccupied(true).build());
        seatDtos.add(SeatDto.builder().id(2L).rowNumber(1).seatNumber(2).isOccupied(false).build());
        seatDtos.add(SeatDto.builder().id(3L).rowNumber(1).seatNumber(3).isOccupied(true).build());
        seatDtos.add(SeatDto.builder().id(4L).rowNumber(2).seatNumber(1).isOccupied(true).build());
        seatDtos.add(SeatDto.builder().id(5L).rowNumber(2).seatNumber(2).isOccupied(false).build());
    }

    // ============= GET SEATS FOR SEANCE =============

    @Test
    @DisplayName("Scenario 1: Get seats for seance - public access")
    @WithMockUser(roles = "USER")
    void testGetSeatsForSeance_PublicAccess_Success() throws Exception {
        when(seanceService.getSeatsStatusForMovie(1L)).thenReturn(seatDtos);

        mockMvc.perform(get("/api/v1/seances/1/seats")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].rowNumber").value(1))
                .andExpect(jsonPath("$[0].seatNumber").value(1))
                .andExpect(jsonPath("$[0].isOccupied").value(true))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].isOccupied").value(false))
                .andExpect(jsonPath("$[4].id").value(5L))
                .andExpect(jsonPath("$[4].rowNumber").value(2))
                .andExpect(jsonPath("$[4].seatNumber").value(2));

        verify(seanceService, times(1)).getSeatsStatusForMovie(1L);
    }

    @Test
    @DisplayName("Scenario 2: Get seats for seance - anonymous user")
    void testGetSeatsForSeance_AnonymousUser_Success() throws Exception {
        when(seanceService.getSeatsStatusForMovie(1L)).thenReturn(seatDtos);

        mockMvc.perform(get("/api/v1/seances/1/seats")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5));

        verify(seanceService, times(1)).getSeatsStatusForMovie(1L);
    }

    @Test
    @DisplayName("Scenario 3: Get seats for seance - seance not found")
    @WithMockUser(roles = "USER")
    void testGetSeatsForSeance_SeanceNotFound() throws Exception {
        when(seanceService.getSeatsStatusForMovie(999L))
                .thenThrow(new ResourceNotFoundException("Seance not found"));

        mockMvc.perform(get("/api/v1/seances/999/seats")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(seanceService, times(1)).getSeatsStatusForMovie(999L);
    }

    @Test
    @DisplayName("Scenario 4: Get seats for seance - empty seats list")
    @WithMockUser(roles = "USER")
    void testGetSeatsForSeance_EmptyList() throws Exception {
        when(seanceService.getSeatsStatusForMovie(2L)).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/v1/seances/2/seats")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(seanceService, times(1)).getSeatsStatusForMovie(2L);
    }

    @Test
    @DisplayName("Scenario 5: Get seats for seance - verify all seats occupied")
    @WithMockUser(roles = "USER")
    void testGetSeatsForSeance_AllSeatsOccupied() throws Exception {
        List<SeatDto> occupiedSeats = List.of(
                SeatDto.builder().id(1L).rowNumber(1).seatNumber(1).isOccupied(true).build(),
                SeatDto.builder().id(2L).rowNumber(1).seatNumber(2).isOccupied(true).build(),
                SeatDto.builder().id(3L).rowNumber(1).seatNumber(3).isOccupied(true).build()
        );

        when(seanceService.getSeatsStatusForMovie(3L)).thenReturn(occupiedSeats);

        mockMvc.perform(get("/api/v1/seances/3/seats")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].isOccupied").value(true))
                .andExpect(jsonPath("$[1].isOccupied").value(true))
                .andExpect(jsonPath("$[2].isOccupied").value(true));

        verify(seanceService, times(1)).getSeatsStatusForMovie(3L);
    }

    @Test
    @DisplayName("Scenario 6: Get seats for seance - verify all seats available")
    @WithMockUser(roles = "USER")
    void testGetSeatsForSeance_AllSeatsAvailable() throws Exception {
        List<SeatDto> availableSeats = List.of(
                SeatDto.builder().id(1L).rowNumber(1).seatNumber(1).isOccupied(false).build(),
                SeatDto.builder().id(2L).rowNumber(1).seatNumber(2).isOccupied(false).build(),
                SeatDto.builder().id(3L).rowNumber(1).seatNumber(3).isOccupied(false).build()
        );

        when(seanceService.getSeatsStatusForMovie(4L)).thenReturn(availableSeats);

        mockMvc.perform(get("/api/v1/seances/4/seats")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].isOccupied").value(false))
                .andExpect(jsonPath("$[1].isOccupied").value(false))
                .andExpect(jsonPath("$[2].isOccupied").value(false));

        verify(seanceService, times(1)).getSeatsStatusForMovie(4L);
    }

    @Test
    @DisplayName("Scenario 7: Get seats for seance - large seance with many seats")
    @WithMockUser(roles = "USER")
    void testGetSeatsForSeance_LargeSeance() throws Exception {
        List<SeatDto> largeSeatList = new ArrayList<>();
        for (long i = 1; i <= 100; i++) {
            largeSeatList.add(SeatDto.builder()
                    .id(i)
                    .rowNumber((int) ((i - 1) / 10 + 1))
                    .seatNumber((int) ((i - 1) % 10 + 1))
                    .isOccupied(i % 3 == 0)
                    .build());
        }

        when(seanceService.getSeatsStatusForMovie(5L)).thenReturn(largeSeatList);

        mockMvc.perform(get("/api/v1/seances/5/seats")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(100))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].rowNumber").value(1))
                .andExpect(jsonPath("$[0].seatNumber").value(1))
                .andExpect(jsonPath("$[99].id").value(100L))
                .andExpect(jsonPath("$[99].rowNumber").value(10))
                .andExpect(jsonPath("$[99].seatNumber").value(10));

        verify(seanceService, times(1)).getSeatsStatusForMovie(5L);
    }

    @Test
    @DisplayName("Scenario 8: Get seats for seance - verify response content type")
    @WithMockUser(roles = "USER")
    void testGetSeatsForSeance_VerifyContentType() throws Exception {
        when(seanceService.getSeatsStatusForMovie(1L)).thenReturn(seatDtos);

        mockMvc.perform(get("/api/v1/seances/1/seats")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(seanceService, times(1)).getSeatsStatusForMovie(1L);
    }

    @Test
    @DisplayName("Scenario 9: Get seats for seance - different seance IDs")
    @WithMockUser(roles = "USER")
    void testGetSeatsForSeance_DifferentSeanceIds() throws Exception {
        List<SeatDto> seats1 = List.of(SeatDto.builder().id(1L).rowNumber(1).seatNumber(1).isOccupied(false).build());
        List<SeatDto> seats2 = List.of(SeatDto.builder().id(2L).rowNumber(1).seatNumber(1).isOccupied(true).build());

        when(seanceService.getSeatsStatusForMovie(1L)).thenReturn(seats1);
        when(seanceService.getSeatsStatusForMovie(2L)).thenReturn(seats2);

        mockMvc.perform(get("/api/v1/seances/1/seats")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].isOccupied").value(false));

        mockMvc.perform(get("/api/v1/seances/2/seats")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].isOccupied").value(true));

        verify(seanceService, times(1)).getSeatsStatusForMovie(1L);
        verify(seanceService, times(1)).getSeatsStatusForMovie(2L);
    }

    @Test
    @DisplayName("Scenario 10: Get seats for seance - internal server error")
    @WithMockUser(roles = "USER")
    void testGetSeatsForSeance_InternalServerError() throws Exception {
        when(seanceService.getSeatsStatusForMovie(anyLong()))
                .thenThrow(new RuntimeException("Database connection error"));

        mockMvc.perform(get("/api/v1/seances/1/seats")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(seanceService, times(1)).getSeatsStatusForMovie(1L);
    }
}
