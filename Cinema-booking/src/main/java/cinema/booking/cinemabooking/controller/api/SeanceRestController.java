package cinema.booking.cinemabooking.controller.api;

import cinema.booking.cinemabooking.dto.response.SeatDto;
import cinema.booking.cinemabooking.service.SeanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST API controller for seances
 */
@RestController
@RequestMapping("/api/v1/seances")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Seances", description = "Endpoints for managing seances")
public class SeanceRestController {
    private final SeanceService seanceService;

    /**
     * Get seat status for a specific seance
     * @param id seance ID
     * @return list of SeatDto with status
     */
    @GetMapping("/{id}/seats")
    @Operation(summary = "Get seat status for a specific seance", description = "Retrieve the list of seats with their status for a given seance ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved seat status for the seance"),
            @ApiResponse(responseCode = "404", description = "Seance not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<SeatDto>> getSeatsForSeance(@PathVariable Long id) {
        log.info("API: Fetching seat status for seance ID: {}", id);

        List<SeatDto> seats = seanceService.getSeatsStatusForMovie(id);
        return ResponseEntity.ok(seats);
    }
}
