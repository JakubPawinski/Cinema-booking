package cinema.booking.cinemabooking.controller.api;

import cinema.booking.cinemabooking.dto.MovieWithSeancesDto;
import cinema.booking.cinemabooking.service.SeanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/repertoires")
@RequiredArgsConstructor
public class RepertoireRestController {
    private final SeanceService seanceService;

    @GetMapping
    public ResponseEntity<List<MovieWithSeancesDto>> getRepertoire(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        if (date == null) {
            date = LocalDate.now();
        }

        List<MovieWithSeancesDto> repertoire = seanceService.getRepertoireForDate(date);
        return ResponseEntity.ok(repertoire);
    }
}
