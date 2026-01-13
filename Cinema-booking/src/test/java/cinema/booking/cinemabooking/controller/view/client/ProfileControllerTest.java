package cinema.booking.cinemabooking.controller.view.client;

import cinema.booking.cinemabooking.config.SecurityConfig;
import cinema.booking.cinemabooking.controller.view.GlobalControllerAdvice;
import cinema.booking.cinemabooking.dto.response.ReservationSummaryDto;
import cinema.booking.cinemabooking.enums.ReservationStatus;
import cinema.booking.cinemabooking.exception.ResourceNotFoundException;
import cinema.booking.cinemabooking.model.Reservation;
import cinema.booking.cinemabooking.service.ReservationService;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProfileController.class)
@Import({SecurityConfig.class, GlobalControllerAdvice.class})
@DisplayName("View Tests for ProfileController")
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservationService reservationService;

    @Test
    @DisplayName("Scenario 1: User profile - not authenticated - redirect to login")
    void testUserProfile_NotAuthenticated() throws Exception {
        mockMvc.perform(get("/profile"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @DisplayName("Scenario 2: User profile - authenticated - success")
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testUserProfile_Authenticated() throws Exception {
        Page<ReservationSummaryDto> emptyPage = new PageImpl<>(List.of());
        when(reservationService.getUserReservations("testuser", 0, 10, null))
                .thenReturn(emptyPage);

        mockMvc.perform(get("/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile/index"))
                .andExpect(model().attributeExists("reservations"));

        verify(reservationService, times(1)).getUserReservations("testuser", 0, 10, null);
    }

    @Test
    @DisplayName("Scenario 3: User profile - with pagination")
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testUserProfile_WithPagination() throws Exception {
        Page<ReservationSummaryDto> page = new PageImpl<>(List.of());
        when(reservationService.getUserReservations("testuser", 2, 20, null))
                .thenReturn(page);

        mockMvc.perform(get("/profile")
                        .param("page", "2")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile/index"));

        verify(reservationService, times(1)).getUserReservations("testuser", 2, 20, null);
    }

    @Test
    @DisplayName("Scenario 4: User profile - filter by reservation status")
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testUserProfile_FilterByStatus() throws Exception {
        Page<ReservationSummaryDto> page = new PageImpl<>(List.of());
        when(reservationService.getUserReservations("testuser", 0, 10, ReservationStatus.PAID))
                .thenReturn(page);

        mockMvc.perform(get("/profile")
                        .param("status", "PAID"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile/index"));

        verify(reservationService, times(1)).getUserReservations("testuser", 0, 10, ReservationStatus.PAID);
    }

    @Test
    @DisplayName("Scenario 5: Reservation details - not authenticated - redirect to login")
    void testReservationDetails_NotAuthenticated() throws Exception {
        mockMvc.perform(get("/profile/reservation/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @DisplayName("Scenario 6: Reservation details - authenticated - success")
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testReservationDetails_Success() throws Exception {
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setStatus(ReservationStatus.PAID);

        when(reservationService.getReservationDetails(1L, "testuser"))
                .thenReturn(reservation);

        mockMvc.perform(get("/profile/reservation/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile/reservation-details"))
                .andExpect(model().attributeExists("reservation"));

        verify(reservationService, times(1)).getReservationDetails(1L, "testuser");
    }

    @Test
    @DisplayName("Scenario 7: Reservation details - access denied")
    @WithMockUser(username = "otheruser", roles = {"USER"})
    void testReservationDetails_AccessDenied() throws Exception {
        when(reservationService.getReservationDetails(1L, "otheruser"))
                .thenThrow(new SecurityException("Access denied"));

        mockMvc.perform(get("/profile/reservation/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile?error=access_denied"));

        verify(reservationService, times(1)).getReservationDetails(1L, "otheruser");
    }

    @Test
    @DisplayName("Scenario 8: Reservation details - not found")
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testReservationDetails_NotFound() throws Exception {
        when(reservationService.getReservationDetails(999L, "testuser"))
                .thenThrow(new ResourceNotFoundException("Reservation not found"));

        mockMvc.perform(get("/profile/reservation/999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile?error=not_found"));

        verify(reservationService, times(1)).getReservationDetails(999L, "testuser");
    }
}
