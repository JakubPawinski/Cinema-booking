package cinema.booking.cinemabooking.service;

import cinema.booking.cinemabooking.dto.MovieDto;
import cinema.booking.cinemabooking.dto.MovieWithSeancesDto;
import cinema.booking.cinemabooking.dto.SeanceDto;
import cinema.booking.cinemabooking.dto.SeatDto;
import cinema.booking.cinemabooking.model.Seance;
import cinema.booking.cinemabooking.model.Seat;
import cinema.booking.cinemabooking.model.Ticket;
import cinema.booking.cinemabooking.repository.SeanceRepository;
import cinema.booking.cinemabooking.repository.SeatRepository;
import cinema.booking.cinemabooking.repository.TicketRepository;
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
import cinema.booking.cinemabooking.model.Movie;


@Service
@RequiredArgsConstructor
public class SeanceService {
    private final SeanceRepository seanceRepository;
    private final SeatRepository seatRepository;
    private final TicketRepository ticketRepository;

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

        // Sort the result by movie title
        result.sort((a, b) -> a.getTitle().compareTo(b.getTitle()));

        return result;
    }

    /*
     * Method to get detailed information about a specific seance by its ID.
     */
    @Transactional(readOnly = true)
    public SeanceDto getSeanceDetails(Long seanceId) {
        // Fetch the seance by ID
        Seance seance = seanceRepository.findById(seanceId)
                .orElseThrow(() -> new RuntimeException("Seans nie istnieje"));

        // Convert Seance entity to SeanceDto and return
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

        // Fetch the seance by ID
        Seance seance = seanceRepository.findById(seanceId).orElseThrow(() -> new RuntimeException("Seance not found"));

        // Fetch all seats in the cinema room for the seance
        List<Seat> allSeats = seatRepository.findAllByCinemaRoom_Id(seance.getCinemaRoom().getId());

        // Fetch all taken tickets for the seance
        List<Ticket> takenTickets = ticketRepository.findAllTakenTickets(seanceId, LocalDateTime.now());

        // Extract the IDs of taken seats
        List<Long> takenSeatIds = takenTickets.stream()
                .map(ticket -> ticket.getSeat().getId())
                .toList();

        // Prepare and return the list of SeatDto with occupancy status
        return allSeats.stream()
                .map(seat -> cinema.booking.cinemabooking.dto.SeatDto.builder()
                        .id(seat.getId())
                        .rowNumber(seat.getRowNumber())
                        .seatNumber(seat.getSeatNumber())
                        .isOccupied(takenSeatIds.contains(seat.getId()))
                        .build())
                .collect(Collectors.toList());
    }
}
