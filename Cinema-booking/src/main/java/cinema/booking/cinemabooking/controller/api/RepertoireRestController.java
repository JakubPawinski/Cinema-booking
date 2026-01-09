package cinema.booking.cinemabooking.controller.api;

import cinema.booking.cinemabooking.dto.response.MovieWithSeancesDto;
import cinema.booking.cinemabooking.service.SeanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;
import java.util.List;

/**
 * REST API controller for repertoire
 */
@RestController
@RequestMapping("/api/v1/repertoires")
@RequiredArgsConstructor
@Slf4j
public class RepertoireRestController {
    private final SeanceService seanceService;

    /**
     * Get repertoire for a specific date
     * If no date is provided, the current date is used
     *
     * @param date the date for which to get the repertoire (optional, defaults to today)
     * @return list of movies with their seances for the specified date
     */
    @GetMapping
    public ResponseEntity<List<MovieWithSeancesDto>> getRepertoire(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("API: Fetching repertoire for date: {}", date);

        // Check and set default date if not provided
        System.out.println("Received date parameter: " + date);
        if (date == null) {
            log.info("No date provided, using current date {}", LocalDate.now());
            date = LocalDate.now();
        }

        List<MovieWithSeancesDto> repertoire = seanceService.getRepertoireForDate(date);
        return ResponseEntity.ok(repertoire);
    }
}
