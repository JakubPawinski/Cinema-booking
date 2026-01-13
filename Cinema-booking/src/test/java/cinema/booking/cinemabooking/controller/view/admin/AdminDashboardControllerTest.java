package cinema.booking.cinemabooking.controller.view.admin;

import cinema.booking.cinemabooking.config.SecurityConfig;
import cinema.booking.cinemabooking.controller.view.GlobalControllerAdvice;
import cinema.booking.cinemabooking.dto.report.DailySalesDto;
import cinema.booking.cinemabooking.dto.report.SalesReportDto;
import cinema.booking.cinemabooking.repository.MovieRepository;
import cinema.booking.cinemabooking.repository.ReservationRepository;
import cinema.booking.cinemabooking.repository.UserRepository;
import cinema.booking.cinemabooking.service.ReportService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminDashboardController.class)
@Import({SecurityConfig.class, GlobalControllerAdvice.class})
@DisplayName("View Tests for AdminDashboardController")
class AdminDashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MovieRepository movieRepository;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private ReservationRepository reservationRepository;

    @MockitoBean
    private ReportService reportService;

    @Test
    @DisplayName("Scenario 1: Admin dashboard - not authenticated - redirect to login")
    void testAdminDashboard_NotAuthenticated() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @DisplayName("Scenario 2: Admin dashboard - user without admin role - access denied")
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testAdminDashboard_NoAdminRole() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Scenario 3: Admin dashboard - admin user - success")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testAdminDashboard_Success() throws Exception {
        when(movieRepository.count()).thenReturn(15L);
        when(userRepository.count()).thenReturn(42L);
        when(reservationRepository.count()).thenReturn(128L);

        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"))
                .andExpect(model().attribute("moviesCount", 15L))
                .andExpect(model().attribute("usersCount", 42L))
                .andExpect(model().attribute("reservationsCount", 128L));

        verify(movieRepository, times(1)).count();
        verify(userRepository, times(1)).count();
        verify(reservationRepository, times(1)).count();
    }

    @Test
    @DisplayName("Scenario 4: Admin dashboard - zero counts")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testAdminDashboard_ZeroCounts() throws Exception {
        when(movieRepository.count()).thenReturn(0L);
        when(userRepository.count()).thenReturn(0L);
        when(reservationRepository.count()).thenReturn(0L);

        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"))
                .andExpect(model().attribute("moviesCount", 0L))
                .andExpect(model().attribute("usersCount", 0L))
                .andExpect(model().attribute("reservationsCount", 0L));

        verify(movieRepository, times(1)).count();
        verify(userRepository, times(1)).count();
        verify(reservationRepository, times(1)).count();
    }

    @Test
    @DisplayName("Scenario 5: Admin reports - not authenticated - redirect to login")
    void testAdminReports_NotAuthenticated() throws Exception {
        mockMvc.perform(get("/admin/reports"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @DisplayName("Scenario 6: Admin reports - user without admin role - access denied")
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testAdminReports_NoAdminRole() throws Exception {
        mockMvc.perform(get("/admin/reports"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Scenario 7: Admin reports - admin user - success")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testAdminReports_Success() throws Exception {
        List<SalesReportDto> salesByMovie = List.of(
                new SalesReportDto("Inception", 120L, 1500.0),
                new SalesReportDto("The Matrix", 100L, 1200.0)
        );

        List<DailySalesDto> salesByDate = List.of(
                new DailySalesDto(LocalDate.of(2024, 12, 20), 65L, 800.0),
                new DailySalesDto(LocalDate.of(2024, 12, 21), 75L, 900.0)
        );

        when(reportService.getSalesReport()).thenReturn(salesByMovie);
        when(reportService.getDailySalesReport()).thenReturn(salesByDate);

        mockMvc.perform(get("/admin/reports"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/reports"))
                .andExpect(model().attributeExists("salesByMovie", "salesByDate"));

        verify(reportService, times(1)).getSalesReport();
        verify(reportService, times(1)).getDailySalesReport();
    }

    @Test
    @DisplayName("Scenario 8: Admin reports - empty reports")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testAdminReports_EmptyReports() throws Exception {
        when(reportService.getSalesReport()).thenReturn(List.of());
        when(reportService.getDailySalesReport()).thenReturn(List.of());

        mockMvc.perform(get("/admin/reports"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/reports"))
                .andExpect(model().attribute("salesByMovie", List.of()))
                .andExpect(model().attribute("salesByDate", List.of()));

        verify(reportService, times(1)).getSalesReport();
        verify(reportService, times(1)).getDailySalesReport();
    }
}
