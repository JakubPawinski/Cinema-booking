package cinema.booking.cinemabooking.controller.view.client;

import cinema.booking.cinemabooking.config.SecurityConfig;
import cinema.booking.cinemabooking.controller.view.GlobalControllerAdvice;
import cinema.booking.cinemabooking.dto.response.SeanceDto;
import cinema.booking.cinemabooking.model.Reservation;
import cinema.booking.cinemabooking.repository.ReservationRepository;
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
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
@Import({SecurityConfig.class, GlobalControllerAdvice.class})
@DisplayName("View Tests for BookingController")
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SeanceService seanceService;

    @MockitoBean
    private ReservationRepository reservationRepository;

    @Test
    @DisplayName("Scenario 1: Select seats - not authenticated - redirect to login")
    void testSelectSeats_NotAuthenticated() throws Exception {
        mockMvc.perform(get("/booking/seance/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @DisplayName("Scenario 2: Select seats - authenticated - success")
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testSelectSeats_Success() throws Exception {
        SeanceDto seanceDto = SeanceDto.builder()
                .id(1L)
                .movieTitle("Test Movie")
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .regularTicketPrice(12.50)
                .reducedTicketPrice(8.00)
                .roomName("Room A")
                .movieId(1L)
                .build();

        when(seanceService.getSeanceDetails(1L))
                .thenReturn(seanceDto);

        mockMvc.perform(get("/booking/seance/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("booking/seat-selection"))
                .andExpect(model().attributeExists("seance"));

        verify(seanceService, times(1)).getSeanceDetails(1L);
    }

    @Test
    @DisplayName("Scenario 3: Select seats - seance not found")
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testSelectSeats_NotFound() throws Exception {
        when(seanceService.getSeanceDetails(999L))
                .thenThrow(new RuntimeException("Seance not found"));

        mockMvc.perform(get("/booking/seance/999"))
                .andExpect(status().is5xxServerError());

        verify(seanceService, times(1)).getSeanceDetails(999L);
    }

    @Test
    @DisplayName("Scenario 4: Payment page - not authenticated - redirect to login")
    void testPayment_NotAuthenticated() throws Exception {
        mockMvc.perform(get("/booking/payment/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @DisplayName("Scenario 5: Payment page - authenticated - success")
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testPayment_Success() throws Exception {
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setTotalPrice(100.0);

        when(reservationRepository.findById(1L))
                .thenReturn(Optional.of(reservation));

        mockMvc.perform(get("/booking/payment/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("booking/payment"))
                .andExpect(model().attributeExists("reservation"));

        verify(reservationRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Scenario 6: Payment page - reservation not found")
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testPayment_NotFound() throws Exception {
        when(reservationRepository.findById(999L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/booking/payment/999"))
                .andExpect(status().is5xxServerError());

        verify(reservationRepository, times(1)).findById(999L);
    }
}
