package cinema.booking.cinemabooking.controller.api;

import cinema.booking.cinemabooking.dto.SeatDto;
import cinema.booking.cinemabooking.service.SeanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/seances")
@RequiredArgsConstructor
public class SeanceRestController {
    private final SeanceService seanceService;

    @GetMapping("/{id}/seats")
    public ResponseEntity<List<SeatDto>> getSeatsForSeance(@PathVariable Long id) {
        List<SeatDto> seats = seanceService.getSeatsStatusForMovie(id);
        return ResponseEntity.ok(seats);
    }
}
