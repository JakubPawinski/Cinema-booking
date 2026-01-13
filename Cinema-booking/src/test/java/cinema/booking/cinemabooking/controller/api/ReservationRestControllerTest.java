package cinema.booking.cinemabooking.controller.api;

import cinema.booking.cinemabooking.config.SecurityConfig;
import cinema.booking.cinemabooking.dto.request.CreateReservationDto;
import cinema.booking.cinemabooking.dto.response.ReservationSummaryDto;
import cinema.booking.cinemabooking.dto.response.TicketDto;
import cinema.booking.cinemabooking.enums.ReservationStatus;
import cinema.booking.cinemabooking.enums.TicketType;
import cinema.booking.cinemabooking.exception.ResourceNotFoundException;
import cinema.booking.cinemabooking.exception.InvalidReservationActionException;
import cinema.booking.cinemabooking.exception.SeatAlreadyOccupiedException;
import cinema.booking.cinemabooking.service.PdfTicketService;
import cinema.booking.cinemabooking.service.ReservationService;
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

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservationRestController.class)
@Import(SecurityConfig.class)
@DisplayName("REST API Tests for ReservationRestController")
class ReservationRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservationService reservationService;

    @MockitoBean
    private PdfTicketService pdfTicketService;

    private ObjectMapper objectMapper;
    private CreateReservationDto createReservationDto;
    private ReservationSummaryDto reservationSummaryDto;
    private LocalDateTime now;
    private LocalDateTime seanceTime;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        now = LocalDateTime.now();
        seanceTime = now.plusHours(2);

        createReservationDto = new CreateReservationDto();
        createReservationDto.setSeanceId(1L);
        createReservationDto.setTickets(List.of(
                new CreateReservationDto.TicketRequest(3L, TicketType.REGULAR),
                new CreateReservationDto.TicketRequest(4L, TicketType.REDUCED)
        ));
        reservationSummaryDto = ReservationSummaryDto.builder()
                .id(1L)
                .totalPrice(25.00)
                .expiresAt(now.plusMinutes(15))
                .status(ReservationStatus.PENDING)
                .ticketCount(2)
                .movieTitle("Test Movie")
                .seanceStartTime(seanceTime)
                .tickets(List.of(
                        new TicketDto(1L, 1L, 1, 1),
                        new TicketDto(2L, 2L, 2, 1)
                ))
                .build();
    }

    // ============= CREATE RESERVATION =============

    @Test
    @DisplayName("Scenario 1: Create reservation - authenticated user")
    @WithMockUser(username = "testUser", roles = "USER")
    void testCreateReservation_AuthenticatedUser_Success() throws Exception {
        when(reservationService.createReservation(any(CreateReservationDto.class), eq("testUser")))
                .thenReturn(reservationSummaryDto);

        mockMvc.perform(post("/api/v1/reservations")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReservationDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.movieTitle").value("Test Movie"))
                .andExpect(jsonPath("$.ticketCount").value(2));

        verify(reservationService, times(1)).createReservation(any(CreateReservationDto.class), eq("testUser"));
    }

    @Test
    @DisplayName("Scenario 2: Create reservation - unauthenticated user")
    void testCreateReservation_Unauthenticated() throws Exception {
        mockMvc.perform(post("/api/v1/reservations")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReservationDto)))
                .andExpect(status().is3xxRedirection());

        verify(reservationService, never()).createReservation(any(CreateReservationDto.class), anyString());
    }

    @Test
    @DisplayName("Scenario 3: Create reservation - seance not found")
    @WithMockUser(username = "testUser", roles = "USER")
    void testCreateReservation_SeanceNotFound() throws Exception {
        when(reservationService.createReservation(any(CreateReservationDto.class), eq("testUser")))
                .thenThrow(new ResourceNotFoundException("Seance not found"));

        mockMvc.perform(post("/api/v1/reservations")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReservationDto)))
                .andExpect(status().isNotFound());

        verify(reservationService, times(1)).createReservation(any(CreateReservationDto.class), eq("testUser"));
    }

    @Test
    @DisplayName("Scenario 4: Create reservation - seat already occupied")
    @WithMockUser(username = "testUser", roles = "USER")
    void testCreateReservation_SeatAlreadyOccupied() throws Exception {
        when(reservationService.createReservation(any(CreateReservationDto.class), eq("testUser")))
                .thenThrow(new SeatAlreadyOccupiedException("Seat already occupied"));

        mockMvc.perform(post("/api/v1/reservations")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReservationDto)))
                .andExpect(status().isConflict());

        verify(reservationService, times(1)).createReservation(any(CreateReservationDto.class), eq("testUser"));
    }

    @Test
    @DisplayName("Scenario 5: Create reservation - empty tickets list")
    @WithMockUser(username = "testUser", roles = "USER")
    void testCreateReservation_EmptyTicketsList() throws Exception {
        createReservationDto.setTickets(new ArrayList<>());

        mockMvc.perform(post("/api/v1/reservations")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReservationDto)))
                .andExpect(status().isBadRequest());

        verify(reservationService, never()).createReservation(any(CreateReservationDto.class), anyString());
    }

    @Test
    @DisplayName("Scenario 6: Create reservation - missing seance ID")
    @WithMockUser(username = "testUser", roles = "USER")
    void testCreateReservation_MissingSeanceId() throws Exception {
        createReservationDto.setSeanceId(null);

        mockMvc.perform(post("/api/v1/reservations")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReservationDto)))
                .andExpect(status().isBadRequest());

        verify(reservationService, never()).createReservation(any(CreateReservationDto.class), anyString());
    }

    // ============= CONFIRM AND PAY FOR RESERVATION =============

    @Test
    @DisplayName("Scenario 7: Confirm reservation - authenticated user")
    @WithMockUser(username = "testUser", roles = "USER")
    void testConfirmReservation_AuthenticatedUser_Success() throws Exception {
        doNothing().when(reservationService).payForReservation(1L);

        mockMvc.perform(put("/api/v1/reservations/pay")
                        .with(csrf())
                        .param("reservationId", "1"))
                .andExpect(status().isOk());

        verify(reservationService, times(1)).payForReservation(1L);
    }

    @Test
    @DisplayName("Scenario 8: Confirm reservation - unauthenticated user")
    void testConfirmReservation_Unauthenticated() throws Exception {
        mockMvc.perform(put("/api/v1/reservations/pay")
                        .with(csrf())
                        .param("reservationId", "1"))
                .andExpect(status().is3xxRedirection());

        verify(reservationService, never()).payForReservation(anyLong());
    }

    @Test
    @DisplayName("Scenario 9: Confirm reservation - reservation not found")
    @WithMockUser(username = "testUser", roles = "USER")
    void testConfirmReservation_ReservationNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Reservation not found"))
                .when(reservationService).payForReservation(999L);

        mockMvc.perform(put("/api/v1/reservations/pay")
                        .with(csrf())
                        .param("reservationId", "999"))
                .andExpect(status().isNotFound());

        verify(reservationService, times(1)).payForReservation(999L);
    }

    @Test
    @DisplayName("Scenario 10: Confirm reservation - invalid reservation status")
    @WithMockUser(username = "testUser", roles = "USER")
    void testConfirmReservation_InvalidStatus() throws Exception {
        doThrow(new InvalidReservationActionException("Cannot pay for already paid reservation"))
                .when(reservationService).payForReservation(1L);

        mockMvc.perform(put("/api/v1/reservations/pay")
                        .with(csrf())
                        .param("reservationId", "1"))
                .andExpect(status().isBadRequest());

        verify(reservationService, times(1)).payForReservation(1L);
    }

    // ============= CANCEL RESERVATION =============

    @Test
    @DisplayName("Scenario 11: Cancel reservation - authenticated user")
    @WithMockUser(username = "testUser", roles = "USER")
    void testCancelReservation_AuthenticatedUser_Success() throws Exception {
        doNothing().when(reservationService).cancelReservation(1L);

        mockMvc.perform(delete("/api/v1/reservations/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(reservationService, times(1)).cancelReservation(1L);
    }

    @Test
    @DisplayName("Scenario 12: Cancel reservation - unauthenticated user")
    void testCancelReservation_Unauthenticated() throws Exception {
        mockMvc.perform(delete("/api/v1/reservations/1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        verify(reservationService, never()).cancelReservation(anyLong());
    }

    @Test
    @DisplayName("Scenario 13: Cancel reservation - reservation not found")
    @WithMockUser(username = "testUser", roles = "USER")
    void testCancelReservation_ReservationNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Reservation not found"))
                .when(reservationService).cancelReservation(999L);

        mockMvc.perform(delete("/api/v1/reservations/999")
                        .with(csrf()))
                .andExpect(status().isNotFound());

        verify(reservationService, times(1)).cancelReservation(999L);
    }

    @Test
    @DisplayName("Scenario 14: Cancel reservation - invalid status for cancellation")
    @WithMockUser(username = "testUser", roles = "USER")
    void testCancelReservation_InvalidStatus() throws Exception {
        doThrow(new InvalidReservationActionException("Cannot cancel already paid reservation"))
                .when(reservationService).cancelReservation(1L);

        mockMvc.perform(delete("/api/v1/reservations/1")
                        .with(csrf()))
                .andExpect(status().isBadRequest());

        verify(reservationService, times(1)).cancelReservation(1L);
    }

    // ============= REMOVE TICKET FROM RESERVATION =============

    @Test
    @DisplayName("Scenario 15: Remove ticket - authenticated user")
    @WithMockUser(username = "testUser", roles = "USER")
    void testRemoveTicket_AuthenticatedUser_Success() throws Exception {
        ReservationSummaryDto updatedReservation = ReservationSummaryDto.builder()
                .id(1L)
                .totalPrice(12.50)
                .expiresAt(now.plusMinutes(15))
                .status(ReservationStatus.PENDING)
                .ticketCount(1)
                .movieTitle("Test Movie")
                .seanceStartTime(seanceTime)
                .tickets(List.of(new TicketDto(1L, 1L, 1, 1)))
                .build();

        when(reservationService.removeTicket(1L, 1L)).thenReturn(updatedReservation);

        mockMvc.perform(delete("/api/v1/reservations/1/tickets/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticketCount").value(1));

        verify(reservationService, times(1)).removeTicket(1L, 1L);
    }

    @Test
    @DisplayName("Scenario 16: Remove ticket - ticket not found")
    @WithMockUser(username = "testUser", roles = "USER")
    void testRemoveTicket_TicketNotFound() throws Exception {
        when(reservationService.removeTicket(1L, 999L))
                .thenThrow(new ResourceNotFoundException("Ticket not found"));

        mockMvc.perform(delete("/api/v1/reservations/1/tickets/999")
                        .with(csrf()))
                .andExpect(status().isNotFound());

        verify(reservationService, times(1)).removeTicket(1L, 999L);
    }

    @Test
    @DisplayName("Scenario 17: Remove ticket - cannot remove from paid reservation")
    @WithMockUser(username = "testUser", roles = "USER")
    void testRemoveTicket_InvalidAction() throws Exception {
        when(reservationService.removeTicket(1L, 1L))
                .thenThrow(new InvalidReservationActionException("Cannot modify paid reservation"));

        mockMvc.perform(delete("/api/v1/reservations/1/tickets/1")
                        .with(csrf()))
                .andExpect(status().isBadRequest());

        verify(reservationService, times(1)).removeTicket(1L, 1L);
    }

    // ============= UPDATE TICKET TYPE =============

    @Test
    @DisplayName("Scenario 18: Update ticket type - authenticated user")
    @WithMockUser(username = "testUser", roles = "USER")
    void testUpdateTicketType_AuthenticatedUser_Success() throws Exception {
        ReservationSummaryDto updatedReservation = ReservationSummaryDto.builder()
                .id(1L)
                .totalPrice(20.00)
                .expiresAt(now.plusMinutes(15))
                .status(ReservationStatus.PENDING)
                .ticketCount(2)
                .movieTitle("Test Movie")
                .seanceStartTime(seanceTime)
                .tickets(List.of(
                        new TicketDto(1L, 1L, 1, 1),
                        new TicketDto(2L, 2L, 2, 1)
                ))
                .build();

        when(reservationService.updateTicketType(1L, 1L, TicketType.REDUCED))
                .thenReturn(updatedReservation);

        mockMvc.perform(patch("/api/v1/reservations/1/tickets/1")
                        .with(csrf())
                        .param("type", "REDUCED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(reservationService, times(1)).updateTicketType(1L, 1L, TicketType.REDUCED);
    }

    @Test
    @DisplayName("Scenario 19: Update ticket type - ticket not found")
    @WithMockUser(username = "testUser", roles = "USER")
    void testUpdateTicketType_TicketNotFound() throws Exception {
        when(reservationService.updateTicketType(1L, 999L, TicketType.REDUCED))
                .thenThrow(new ResourceNotFoundException("Ticket not found"));

        mockMvc.perform(patch("/api/v1/reservations/1/tickets/999")
                        .with(csrf())
                        .param("type", "REDUCED"))
                .andExpect(status().isNotFound());

        verify(reservationService, times(1)).updateTicketType(1L, 999L, TicketType.REDUCED);
    }

    @Test
    @DisplayName("Scenario 20: Update ticket type - invalid type")
    @WithMockUser(username = "testUser", roles = "USER")
    void testUpdateTicketType_InvalidType() throws Exception {
        mockMvc.perform(patch("/api/v1/reservations/1/tickets/1")
                        .with(csrf())
                        .param("type", "INVALID_TYPE"))
                .andExpect(status().isBadRequest());

        verify(reservationService, never()).updateTicketType(anyLong(), anyLong(), any());
    }

    // ============= ADD TICKET TO RESERVATION =============

    @Test
    @DisplayName("Scenario 21: Add ticket to reservation - authenticated user")
    @WithMockUser(username = "testUser", roles = "USER")
    void testAddTicket_AuthenticatedUser_Success() throws Exception {
        ReservationSummaryDto updatedReservation = ReservationSummaryDto.builder()
                .id(1L)
                .totalPrice(37.50)
                .expiresAt(now.plusMinutes(15))
                .status(ReservationStatus.PENDING)
                .ticketCount(3)
                .movieTitle("Test Movie")
                .seanceStartTime(seanceTime)
                .tickets(List.of(
                        new TicketDto(1L, 1L, 1, 1),
                        new TicketDto(2L, 2L, 2, 1),
                        new TicketDto(3L, 3L, 3, 1)
                ))
                .build();

        when(reservationService.addTicketToReservation(1L, 3L))
                .thenReturn(updatedReservation);

        mockMvc.perform(post("/api/v1/reservations/1/tickets")
                        .with(csrf())
                        .param("seatId", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticketCount").value(3));

        verify(reservationService, times(1)).addTicketToReservation(1L, 3L);
    }

    @Test
    @DisplayName("Scenario 22: Add ticket - seat already occupied")
    @WithMockUser(username = "testUser", roles = "USER")
    void testAddTicket_SeatAlreadyOccupied() throws Exception {
        when(reservationService.addTicketToReservation(1L, 1L))
                .thenThrow(new SeatAlreadyOccupiedException("Seat already occupied"));

        mockMvc.perform(post("/api/v1/reservations/1/tickets")
                        .with(csrf())
                        .param("seatId", "1"))
                .andExpect(status().isConflict());

        verify(reservationService, times(1)).addTicketToReservation(1L, 1L);
    }

    @Test
    @DisplayName("Scenario 23: Add ticket - reservation not found")
    @WithMockUser(username = "testUser", roles = "USER")
    void testAddTicket_ReservationNotFound() throws Exception {
        when(reservationService.addTicketToReservation(999L, 3L))
                .thenThrow(new ResourceNotFoundException("Reservation not found"));

        mockMvc.perform(post("/api/v1/reservations/999/tickets")
                        .with(csrf())
                        .param("seatId", "3"))
                .andExpect(status().isNotFound());

        verify(reservationService, times(1)).addTicketToReservation(999L, 3L);
    }

    @Test
    @DisplayName("Scenario 24: Add ticket - cannot add to paid reservation")
    @WithMockUser(username = "testUser", roles = "USER")
    void testAddTicket_InvalidAction() throws Exception {
        when(reservationService.addTicketToReservation(1L, 3L))
                .thenThrow(new InvalidReservationActionException("Cannot modify paid reservation"));

        mockMvc.perform(post("/api/v1/reservations/1/tickets")
                        .with(csrf())
                        .param("seatId", "3"))
                .andExpect(status().isBadRequest());

        verify(reservationService, times(1)).addTicketToReservation(1L, 3L);
    }

    // ============= DOWNLOAD PDF TICKETS =============

    @Test
    @DisplayName("Scenario 25: Download PDF tickets - authenticated user")
    @WithMockUser(username = "testUser", roles = "USER")
    void testDownloadPdf_AuthenticatedUser_Success() throws Exception {
        String pdfContent = "%PDF-1.4\n%Sample PDF content";
        ByteArrayInputStream pdfStream = new ByteArrayInputStream(pdfContent.getBytes());

        when(reservationService.generatePdfForReservation(1L, "testUser"))
                .thenReturn(pdfStream);

        mockMvc.perform(get("/api/v1/reservations/1/pdf"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_PDF_VALUE));

        verify(reservationService, times(1)).generatePdfForReservation(1L, "testUser");
    }

    @Test
    @DisplayName("Scenario 26: Download PDF tickets - unauthenticated user")
    void testDownloadPdf_Unauthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/reservations/1/pdf"))
                .andExpect(status().is3xxRedirection());

        verify(reservationService, never()).generatePdfForReservation(anyLong(), anyString());
    }

    @Test
    @DisplayName("Scenario 27: Download PDF tickets - reservation not found")
    @WithMockUser(username = "testUser", roles = "USER")
    void testDownloadPdf_ReservationNotFound() throws Exception {
        when(reservationService.generatePdfForReservation(999L, "testUser"))
                .thenThrow(new ResourceNotFoundException("Reservation not found"));

        mockMvc.perform(get("/api/v1/reservations/999/pdf"))
                .andExpect(status().isNotFound());

        verify(reservationService, times(1)).generatePdfForReservation(999L, "testUser");
    }

    @Test
    @DisplayName("Scenario 28: Download PDF tickets - verify PDF content type")
    @WithMockUser(username = "testUser", roles = "USER")
    void testDownloadPdf_VerifyContentType() throws Exception {
        String pdfContent = "%PDF-1.4\n%Sample PDF content";
        ByteArrayInputStream pdfStream = new ByteArrayInputStream(pdfContent.getBytes());

        when(reservationService.generatePdfForReservation(1L, "testUser"))
                .thenReturn(pdfStream);

        mockMvc.perform(get("/api/v1/reservations/1/pdf"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_PDF_VALUE));

        verify(reservationService, times(1)).generatePdfForReservation(1L, "testUser");
    }
}
