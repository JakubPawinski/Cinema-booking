package cinema.booking.cinemabooking.service;

import cinema.booking.cinemabooking.dto.CreateReservationDto;
import cinema.booking.cinemabooking.dto.ReservationSummaryDto;
import cinema.booking.cinemabooking.enums.ReservationStatus;
import cinema.booking.cinemabooking.enums.TicketType;
import cinema.booking.cinemabooking.model.*;
import cinema.booking.cinemabooking.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ReservationService {

    private final TicketRepository ticketRepository;
    private final SeanceRepository seanceRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;

    /*
     * Method to create a new reservation.
     */
    @Transactional
    public ReservationSummaryDto createReservation(CreateReservationDto request, String username) {

        // Fetch Seance and User
        Seance seance = seanceRepository.findById(request.getSeanceId())
                .orElseThrow(() -> new RuntimeException("Seance not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check seat availability
        List<Ticket> takenTickets = ticketRepository.findAllTakenTickets(seance.getId(), LocalDateTime.now());
        List<Long> takenSeatIds = takenTickets.stream()
                .map(ticket -> ticket.getSeat().getId())
                .toList();

        // Validate requested seats
        for (CreateReservationDto.TicketRequest ticketRequest : request.getTickets()) {
            Long seatId = ticketRequest.getSeatId();
            if (takenSeatIds.contains(seatId)) {
                throw new IllegalStateException("This seat " + seatId + " is already taken.");
            }
        }

        // Create Reservation
        Reservation reservation = new Reservation();
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setCreatedAt(LocalDateTime.now());
        reservation.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        reservation.setUser(user);

        // Calculate total price and create tickets
        List<Ticket> tickets = new ArrayList<>();
        double totalPrice = 0;

        for (CreateReservationDto.TicketRequest ticketRequest : request.getTickets()) {
            Seat seat = seatRepository.findById(ticketRequest.getSeatId())
                    .orElseThrow(() -> new RuntimeException("Seat not found"));

            TicketType ticketType = ticketRequest.getTicketType();

//            double price = ticketType == TicketType.REDUCED
//                    ? seance.getReducedTicketPrice()
//                    : seance.getRegularTicketPrice();

            double price = getTicketPrice(seance, ticketType);

            Ticket ticket = new Ticket();
            ticket.setReservation(reservation);
            ticket.setSeance(seance);
            ticket.setSeat(seat);
            ticket.setTicketType(ticketType);
            ticket.setPrice(price);

            tickets.add(ticket);
            totalPrice += price;
        }

        reservation.setTickets(tickets);
        reservation.setTotalPrice(totalPrice);

        // Save Reservation and Tickets
        Reservation savedReservation = reservationRepository.save(reservation);

        // Prepare and return summary DTO
        return ReservationSummaryDto.builder()
                .id(savedReservation.getId())
                .status(savedReservation.getStatus().name())
                .totalPrice(savedReservation.getTotalPrice())
                .expiresAt(savedReservation.getExpiresAt())
                .ticketCount(tickets.size())
                .movieTitle(seance.getMovie().getTitle())
                .seanceStartTime(seance.getStartTime())
                .build();
    }


    /*
     * Method to pay for a reservation.
     */
    @Transactional
    public void payForReservation(Long reservationId) {
        // Fetch Reservation
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        // Validate reservation status and expiration
        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("Reservation cannot be paid in its current status.");
        }

        // Check if reservation has expired
        if (reservation.getExpiresAt().isBefore(LocalDateTime.now())) {
            reservation.setStatus(ReservationStatus.CANCELLED);
            reservationRepository.save(reservation);
            throw new IllegalStateException("Czas na płatność minął. Rezerwacja anulowana.");
        }

        // Update reservation status to PAID
        reservation.setStatus(ReservationStatus.PAID);

        // Generate unique ticket codes
        for (Ticket ticket : reservation.getTickets()) {
            ticket.setTicketCode(UUID.randomUUID().toString());
        }

        reservationRepository.save(reservation);
    }


    /*
     * Method to get all reservations for a specific user.
     */
    public List<ReservationSummaryDto> getUserReservations(String username) {
        return reservationRepository.findByUser_UsernameOrderByCreatedAtDesc(username)
                .stream()
                .map(res -> ReservationSummaryDto.builder()
                        .id(res.getId())
                        .movieTitle(res.getTickets().getFirst().getSeance().getMovie().getTitle())
                        .seanceStartTime(res.getTickets().getFirst().getSeance().getStartTime())
                        .ticketCount(res.getTickets().size())
                        .totalPrice(res.getTotalPrice())
                        .status(res.getStatus().name())
                        .build())
                .collect(Collectors.toList());
    }

    private double getTicketPrice(Seance seance, TicketType ticketType) {
        return ticketType == TicketType.REDUCED
                ? seance.getReducedTicketPrice()
                : seance.getRegularTicketPrice();
    }

    @Transactional
    public ReservationSummaryDto removeTicket(Long reservationId, Long ticketId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono rezerwacji"));

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("Nie można edytować opłaconej rezerwacji.");
        }

        Ticket ticketToRemove = reservation.getTickets().stream()
                .filter(t -> t.getId().equals(ticketId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Bilet nie istnieje w tej rezerwacji"));

        // Usuń bilet
        reservation.getTickets().remove(ticketToRemove);
        ticketRepository.delete(ticketToRemove);

        // Przelicz cenę całkowitą
        recalculateReservationTotal(reservation);

        return mapToSummary(reservation);
    }

    @Transactional
    public ReservationSummaryDto updateTicketType(Long reservationId, Long ticketId, TicketType newType) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono rezerwacji"));

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("Nie można edytować opłaconej rezerwacji.");
        }

        Ticket ticket = reservation.getTickets().stream()
                .filter(t -> t.getId().equals(ticketId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Bilet nie istnieje"));

        // Aktualizacja typu i ceny
        ticket.setTicketType(newType);
        Seance seance = ticket.getSeance();


        ticket.setPrice(getTicketPrice(seance, newType));

        ticketRepository.save(ticket);

        // Przelicz cenę całkowitą rezerwacji
        recalculateReservationTotal(reservation);

        return mapToSummary(reservation);
    }

    private void recalculateReservationTotal(Reservation reservation) {
        double total = reservation.getTickets().stream()
                .mapToDouble(Ticket::getPrice)
                .sum();
        reservation.setTotalPrice(total);
        reservationRepository.save(reservation);
    }

    private ReservationSummaryDto mapToSummary(Reservation r) {
        // Jeśli usunięto wszystkie bilety, można anulować rezerwację, ale tu zwracamy pustą
        String title = r.getTickets().isEmpty() ? "Brak biletów" : r.getTickets().get(0).getSeance().getMovie().getTitle();
        LocalDateTime time = r.getTickets().isEmpty() ? null : r.getTickets().get(0).getSeance().getStartTime();

        return ReservationSummaryDto.builder()
                .id(r.getId())
                .status(r.getStatus().name())
                .totalPrice(r.getTotalPrice())
                .expiresAt(r.getExpiresAt())
                .ticketCount(r.getTickets().size())
                .movieTitle(title)
                .seanceStartTime(time)
                .build();
    }

    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        if (reservation.getStatus() == ReservationStatus.PAID) {
            throw new IllegalStateException("Cannot cancel a paid reservation.");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
    }
}