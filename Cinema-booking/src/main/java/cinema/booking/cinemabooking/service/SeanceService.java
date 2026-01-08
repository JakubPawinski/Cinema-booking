package cinema.booking.cinemabooking.service;

import cinema.booking.cinemabooking.dto.*;
import cinema.booking.cinemabooking.model.*;
import cinema.booking.cinemabooking.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class SeanceService {
    private final SeanceRepository seanceRepository;
    private final SeatRepository seatRepository;
    private final TicketRepository ticketRepository;
    private final CinemaRoomRepository cinemaRoomRepository;
    private final MovieRepository movieRepository;

    /*
     * Method to get the repertoire for a specific date
     */
    @Transactional(readOnly = true)
    public List<MovieWithSeancesDto> getRepertoireForDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<Seance> seances = seanceRepository.findByStartTimeBetween(startOfDay, endOfDay);

        Map<Movie, List<Seance>> seancesByMovie = seances.stream()
                .collect(Collectors.groupingBy(Seance::getMovie));

        List<MovieWithSeancesDto> result = new ArrayList<>();

        for (Map.Entry<Movie, List<Seance>> entry : seancesByMovie.entrySet()) {
            Movie movie = entry.getKey();
            List<Seance> movieSeances = entry.getValue();

            List<SeanceDto> seanceDtos = movieSeances.stream()
                    .map(seance -> SeanceDto.builder()
                            .id(seance.getId())
                            .startTime(seance.getStartTime())
                            .endTime(seance.getEndTime())
                            .ticketPrice(seance.getTicketPrice())
                            .roomName(seance.getCinemaRoom().getName())
                            .movieId(seance.getMovie().getId())
                            .build())
                    .collect(Collectors.toList());

            MovieWithSeancesDto dto = MovieWithSeancesDto.builder()
                    .movieId(movie.getId())
                    .title(movie.getTitle())
                    .genre(movie.getGenre())
                    .durationMin(movie.getDurationMin())
                    .description(movie.getDescription())
                    .imageUrl(movie.getImageUrl())
                    .seances(seanceDtos)
                    .build();

            result.add(dto);
        }

        return result.stream().sorted((a, b) -> a.getTitle().compareTo(b.getTitle())).collect(Collectors.toList());
    }

    /*
     * Method to get detailed information about a specific seance by its ID.
     */
    @Transactional(readOnly = true)
    public SeanceDto getSeanceDetails(Long seanceId) {
        Seance seance = seanceRepository.findById(seanceId)
                .orElseThrow(() -> new RuntimeException("Seans nie istnieje"));

        return SeanceDto.builder()
                .id(seance.getId())
                .startTime(seance.getStartTime())
                .endTime(seance.getEndTime())
                .ticketPrice(seance.getTicketPrice())
                .roomName(seance.getCinemaRoom().getName())
                .movieId(seance.getMovie().getId())
                .build();
    }

    /*
     * Method to get the status of seats (occupied or available) for a specific seance.
     */
    @Transactional(readOnly = true)
    public List<SeatDto> getSeatsStatusForMovie(Long seanceId) {
        Seance seance = seanceRepository.findById(seanceId).orElseThrow(() -> new RuntimeException("Seance not found"));
        List<Seat> allSeats = seatRepository.findAllByCinemaRoom_Id(seance.getCinemaRoom().getId());
        List<Ticket> takenTickets = ticketRepository.findAllTakenTickets(seanceId, LocalDateTime.now());

        List<Long> takenSeatIds = takenTickets.stream()
                .map(ticket -> ticket.getSeat().getId())
                .toList();

        return allSeats.stream()
                .map(seat -> cinema.booking.cinemabooking.dto.SeatDto.builder()
                        .id(seat.getId())
                        .rowNumber(seat.getRowNumber())
                        .seatNumber(seat.getSeatNumber())
                        .isOccupied(takenSeatIds.contains(seat.getId()))
                        .build())
                .collect(Collectors.toList());
    }

    /*
     * Create a new seance with overlap validation
     */
    @Transactional
    public void createSeance(SeanceRequestDto dto) {
        // 1. Pobierz Film (używając wstrzykniętego repozytorium)
        Movie movie = movieRepository.findById(dto.getMovieId())
                .orElseThrow(() -> new RuntimeException("Film nie istnieje"));

        // 2. Pobierz Salę
        CinemaRoom room = cinemaRoomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new RuntimeException("Sala nie istnieje"));

        // 3. Oblicz czas zakończenia (Start + Czas trwania filmu + 20 min na sprzątanie)
        LocalDateTime startTime = dto.getStartTime();
        int cleaningTime = 20;

        // Koniec zajętości sali = start + film + sprzątanie
        LocalDateTime busyUntil = startTime.plusMinutes(movie.getDurationMin() + cleaningTime);

        // 4. WALIDACJA NAKŁADANIA SIĘ (Overlap)
        List<Seance> overlaps = seanceRepository.findOverlappingSeances(room.getId(), startTime, busyUntil);

        if (!overlaps.isEmpty()) {
            throw new IllegalStateException("W tej sali odbywa się w tym czasie inny seans! Wybierz inną godzinę lub salę.");
        }

        // 5. Zapisz Seans
        Seance seance = new Seance();
        seance.setMovie(movie);
        seance.setCinemaRoom(room);
        seance.setStartTime(startTime);

        // W bazie zapisujemy faktyczny koniec filmu (bez sprzątania), żeby wiedzieć kiedy się kończy seans dla widza
        seance.setEndTime(startTime.plusMinutes(movie.getDurationMin()));

        seance.setTicketPrice(dto.getTicketPrice());

        seanceRepository.save(seance);
    }

    /*
     * Delete a seance by its ID
     */
    @Transactional
    public void deleteSeance(Long id) {
        seanceRepository.deleteById(id);
    }

    /*
     * Get all seances sorted by start time descending
     */
    public List<Seance> getAllSeances() {
        return seanceRepository.findAll(org.springframework.data.domain.Sort.by("startTime").descending());
    }
}