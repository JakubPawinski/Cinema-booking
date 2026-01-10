package cinema.booking.cinemabooking.service;

import cinema.booking.cinemabooking.dto.request.SeanceRequestDto;
import cinema.booking.cinemabooking.dto.response.MovieWithSeancesDto;
import cinema.booking.cinemabooking.dto.response.SeanceDto;
import cinema.booking.cinemabooking.dto.response.SeatDto;
import cinema.booking.cinemabooking.mapper.MovieMapper;
import cinema.booking.cinemabooking.mapper.SeanceMapper;
import cinema.booking.cinemabooking.mapper.SeatMapper;
import cinema.booking.cinemabooking.model.*;
import cinema.booking.cinemabooking.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.ArrayList;

/**
 * Service for managing seances (movie showings).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SeanceService {
    private final SeanceRepository seanceRepository;
    private final SeatRepository seatRepository;
    private final TicketRepository ticketRepository;
    private final CinemaRoomRepository cinemaRoomRepository;
    private final MovieRepository movieRepository;
    private final SeanceMapper seanceMapper;
    private final MovieMapper movieMapper;
    private final SeatMapper seatMapper;

    /**
     * Get the repertoire of movies with their seances for a specific date.
     * @param date the date for which to retrieve the repertoire
     * @return list of MovieWithSeancesDto
     */
    @Transactional(readOnly = true)
    public List<MovieWithSeancesDto> getRepertoireForDate(LocalDate date) {
        log.info("Fetching repertoire for date: {}", date);

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<Seance> seances = seanceRepository.findByStartTimeBetween(startOfDay, endOfDay);
        log.debug("Found {} seances for date {}", seances.size(), date);

        // Group seances by movie
        Map<Movie, List<Seance>> seancesByMovie = seances.stream()
                .collect(Collectors.groupingBy(Seance::getMovie));

        List<MovieWithSeancesDto> result = new ArrayList<>();

        for (Map.Entry<Movie, List<Seance>> entry : seancesByMovie.entrySet()) {
            Movie movie = entry.getKey();
            List<Seance> movieSeances = entry.getValue();

            List<SeanceDto> seanceDtos = movieSeances.stream()
                    .map(seanceMapper::toDto).collect(Collectors.toList());

            result.add(movieMapper.toMovieWithSeancesDto(movie, seanceDtos));
        }

        // Sort movies by title
        return result.stream().sorted((a, b) -> a.getTitle().compareTo(b.getTitle())).collect(Collectors.toList());
    }

    /**
     * Get detailed information about a specific seance.
     * @param seanceId the ID of the seance
     * @return SeanceDto with detailed information
     */
    @Transactional(readOnly = true)
    public SeanceDto getSeanceDetails(Long seanceId) {
        log.debug("Fetching details for seance ID: {}", seanceId);

        Seance seance = seanceRepository.findById(seanceId)
                .orElseThrow(() -> {
                    log.warn("Seance with ID {} not found", seanceId);
                    return new RuntimeException("Seance not found");
                });

        return seanceMapper.toDto(seance);
    }

    /**
     * Get the status of seats (occupied/free) for a specific seance.
     * @param seanceId the ID of the seance
     * @return list of SeatDto with occupancy status
     */
    @Transactional(readOnly = true)
    public List<SeatDto> getSeatsStatusForMovie(Long seanceId) {
        log.info("Fetching seat status for seance ID: {}", seanceId);

        Seance seance = seanceRepository.findById(seanceId).orElseThrow(() -> {
            log.warn("Seance with ID {} not found", seanceId);
            return new RuntimeException("Seance not found");
        });

        // Get all seats in the cinema room
        List<Seat> allSeats = seatRepository.findAllByCinemaRoom_Id(seance.getCinemaRoom().getId());

        // Get all taken tickets for the seance
        List<Ticket> takenTickets = ticketRepository.findAllTakenTickets(seanceId, LocalDateTime.now());

        List<Long> takenSeatIds = takenTickets.stream()
                .map(ticket -> ticket.getSeat().getId())
                .toList();

        log.debug("Total seats: {}, Taken seats: {}", allSeats.size(), takenSeatIds.size());

        return allSeats.stream()
                .map(seat -> seatMapper.toDto(seat, takenSeatIds.contains(seat.getId())))
                .collect(Collectors.toList());
    }

    /**
     * Create a new seance with validation to prevent overlapping seances.
     * @param dto the SeanceRequestDto containing seance details
     */
    @Transactional
    public void createSeance(SeanceRequestDto dto) {
        log.info("Attempting to create seance for Movie ID: {} in Room ID: {} at {}",
                dto.getMovieId(), dto.getRoomId(), dto.getStartTime());

        Movie movie = movieRepository.findById(dto.getMovieId())
                .orElseThrow(() -> new RuntimeException("Movie does not exist"));

        CinemaRoom room = cinemaRoomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new RuntimeException("Cinema room does not exist"));


        // Calculate busy time of the room
        LocalDateTime startTime = dto.getStartTime();
        int cleaningTime = 20;

        // Busy until includes movie duration + cleaning time
        LocalDateTime busyUntil = startTime.plusMinutes(movie.getDurationMin() + cleaningTime);

        // Validate overlapping seances
        List<Seance> overlaps = seanceRepository.findOverlappingSeances(room.getId(), startTime, busyUntil);

        if (!overlaps.isEmpty()) {
            log.warn("Overlapping seance detected in Room ID: {} at {}", room.getId(), startTime);
            throw new IllegalStateException("Seance overlaps with existing seance in the same room");
        }


        Seance seance = seanceMapper.toEntity(dto, movie, room);

        seanceRepository.save(seance);
        log.info("Seance created successfully with ID: {}", seance.getId());
    }

    /**
     * Delete a seance by its ID.
     * @param id the ID of the seance to delete
     */
    @Transactional
    public void deleteSeance(Long id) {
        log.info("Deleting seance with ID: {}", id);
        seanceRepository.deleteById(id);
    }

    /**
     * Get all seances sorted by start time in descending order.
     * @return list of Seance
     */
    @Transactional(readOnly = true)
    public List<SeanceDto> getAllSeances() {
        log.debug("Fetching all seances sorted by start time descending");
        return seanceRepository.findAll(Sort.by("startTime").descending())
                .stream()
                .map(seanceMapper::toDto)
                .collect(Collectors.toList());
    }
}