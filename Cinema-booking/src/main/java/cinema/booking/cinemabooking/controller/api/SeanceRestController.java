package cinema.booking.cinemabooking.controller.api;

import cinema.booking.cinemabooking.dto.response.SeatDto;
import cinema.booking.cinemabooking.service.SeanceService;
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
public class SeanceRestController {
    private final SeanceService seanceService;

    /**
     * Get seat status for a specific seance
     * @param id seance ID
     * @return list of SeatDto with status
     */
    @GetMapping("/{id}/seats")
    public ResponseEntity<List<SeatDto>> getSeatsForSeance(@PathVariable Long id) {
        log.info("API: Fetching seat status for seance ID: {}", id);

        List<SeatDto> seats = seanceService.getSeatsStatusForMovie(id);
        return ResponseEntity.ok(seats);
    }
}
