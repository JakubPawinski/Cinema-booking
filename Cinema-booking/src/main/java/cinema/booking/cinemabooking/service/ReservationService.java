package cinema.booking.cinemabooking.service;

import cinema.booking.cinemabooking.dto.CreateReservationDto;
import cinema.booking.cinemabooking.dto.ReservationSummaryDto;
import cinema.booking.cinemabooking.enums.ReservationStatus;
import cinema.booking.cinemabooking.enums.TicketType;
import cinema.booking.cinemabooking.model.*;
import cinema.booking.cinemabooking.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final TicketRepository ticketRepository;
    private final SeanceRepository seanceRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;

    @Transactional
    public ReservationSummaryDto createReservation(CreateReservationDto request, String username) {
        // 1. Pobierz podstawowe dane
        Seance seance = seanceRepository.findById(request.getSeanceId())
                .orElseThrow(() -> new RuntimeException("Seance not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. ZBIERZ ID MIEJSC
        List<Long> requestedSeatIds = request.getTickets().stream()
                .map(CreateReservationDto.TicketRequest::getSeatId)
                .toList();

        // 3. BLOKADA (CRITICAL SECTION)
        // Ta linia "zamraża" te miejsca w bazie danych.
        // Jeśli ktoś inny próbuje je teraz zarezerwować, jego transakcja CZEKA tutaj.
        List<Seat> lockedSeats = seatRepository.findAllByIdInWithLock(requestedSeatIds);

        if (lockedSeats.size() != requestedSeatIds.size()) {
            throw new RuntimeException("Nieprawidłowe identyfikatory miejsc.");
        }

        // 4. SPRAWDZENIE DOSTĘPNOŚCI
        // Teraz, gdy mamy pewność, że nikt inny nie modyfikuje tych miejsc, sprawdzamy bilety.
        List<Ticket> takenTickets = ticketRepository.findAllTakenTickets(seance.getId(), LocalDateTime.now());
        List<Long> takenSeatIds = takenTickets.stream()
                .map(ticket -> ticket.getSeat().getId())
                .toList();

        for (Long seatId : requestedSeatIds) {
            if (takenSeatIds.contains(seatId)) {
                throw new IllegalStateException("Miejsce o numerze ID " + seatId + " zostało już zajęte przez kogoś innego.");
            }
        }

        // 5. TWORZENIE REZERWACJI (Standardowa logika)
        Reservation reservation = new Reservation();
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setCreatedAt(LocalDateTime.now());
        reservation.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        reservation.setUser(user);

        List<Ticket> tickets = new ArrayList<>();
        double totalPrice = 0;

        // Iterujemy po lockedSeats (bo to są już pobrane encje z bazy)
        // Ale musimy dopasować TicketType z requestu
        for (CreateReservationDto.TicketRequest ticketRequest : request.getTickets()) {
            // Znajdź odpowiadające miejsce z listy zablokowanych
            Seat seat = lockedSeats.stream()
                    .filter(s -> s.getId().equals(ticketRequest.getSeatId()))
                    .findFirst()
                    .orElseThrow();

            TicketType ticketType = ticketRequest.getTicketType();
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

        // Zapis zwalnia blokadę automatycznie po zakończeniu transakcji
        Reservation savedReservation = reservationRepository.save(reservation);

        return mapToSummary(savedReservation);
    }


    @Transactional
    public void payForReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("Reservation cannot be paid in its current status.");
        }

        if (reservation.getExpiresAt().isBefore(LocalDateTime.now())) {
            reservation.setStatus(ReservationStatus.CANCELLED);
            reservationRepository.save(reservation);
            throw new IllegalStateException("Czas na płatność minął. Rezerwacja anulowana.");
        }

        reservation.setStatus(ReservationStatus.PAID);
        for (Ticket ticket : reservation.getTickets()) {
            ticket.setTicketCode(UUID.randomUUID().toString());
        }
        reservationRepository.save(reservation);
    }

    // --- NAPRAWIONA METODA POBIERANIA REZERWACJI ---
    // Obsługuje zarówno filtrowanie jak i jego brak.
    // Zastępuje starą metodę, która powodowała błąd.
    // --- POPRAWIONA METODA POBIERANIA REZERWACJI ---
    public Page<ReservationSummaryDto> getUserReservations(String username, int page, int size, ReservationStatus status) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ZMIANA: Sortujemy po ID lub CreatedAt zamiast nested property (tickets.seance...)
        // To naprawia problem duplikatów i "znikających" rezerwacji
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Reservation> reservations;

        if (status != null) {
            reservations = reservationRepository.findAllByUserAndStatus(user, status, pageable);
        } else {
            reservations = reservationRepository.findAllByUser(user, pageable);
        }

        return reservations.map(this::mapToSummary);
    }

    // Opcjonalnie: Przeciążenie dla wstecznej kompatybilności (jeśli gdzieś używasz wersji bez statusu)
    public Page<ReservationSummaryDto> getUserReservations(String username, int page, int size) {
        return getUserReservations(username, page, size, null);
    }

    // --- RESZTA METOD BEZ ZMIAN ---

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

        reservation.getTickets().remove(ticketToRemove);
        ticketRepository.delete(ticketToRemove);
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

        ticket.setTicketType(newType);
        Seance seance = ticket.getSeance();
        ticket.setPrice(getTicketPrice(seance, newType));
        ticketRepository.save(ticket);

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

    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        if (reservation.getStatus() == ReservationStatus.PAID) {
            throw new IllegalStateException("Cannot cancel a paid reservation.");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
    }

    @Transactional(readOnly = true)
    public Reservation getReservationDetails(Long reservationId, String username) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Rezerwacja nie znaleziona"));

        if (!reservation.getUser().getUsername().equals(username)) {
            throw new SecurityException("Brak dostępu do tej rezerwacji");
        }

        reservation.getTickets().size(); // Init Lazy
        return reservation;
    }

    // Helper mapujący
    private ReservationSummaryDto mapToSummary(Reservation r) {
        String title = r.getTickets().isEmpty() ? "Brak biletów" : r.getTickets().get(0).getSeance().getMovie().getTitle();
        LocalDateTime time = r.getTickets().isEmpty() ? null : r.getTickets().get(0).getSeance().getStartTime();

        return ReservationSummaryDto.builder()
                .id(r.getId())
                .status(r.getStatus())
                .totalPrice(r.getTotalPrice())
                .expiresAt(r.getExpiresAt())
                .ticketCount(r.getTickets().size())
                .movieTitle(title)
                .seanceStartTime(time)
                .build();
    }

    @Scheduled(fixedRate = 60000) // 60000 ms = 1 minuta
    @Transactional
    public void autoCancelExpiredReservations() {
        LocalDateTime now = LocalDateTime.now();

        // 1. Pobierz wszystkie rezerwacje, które są PENDING i już wygasły
        List<Reservation> expiredReservations = reservationRepository.findAllByStatusAndExpiresAtBefore(ReservationStatus.PENDING, now);

        if (!expiredReservations.isEmpty()) {
            // 2. Zmień status na CANCELLED
            for (Reservation reservation : expiredReservations) {
                reservation.setStatus(ReservationStatus.CANCELLED);
                // Tu mógłbyś też np. wysłać e-mail do użytkownika: "Twoja rezerwacja wygasła"
            }

            // 3. Zapisz zmiany
            reservationRepository.saveAll(expiredReservations);

            System.out.println("Automatycznie anulowano " + expiredReservations.size() + " wygasłych rezerwacji.");
        }
    }



    // W ReservationService.java

    @Transactional
    public ReservationSummaryDto addTicketToReservation(Long reservationId, Long seatId) {
        // 1. Pobierz rezerwację
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Rezerwacja nie istnieje"));

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("Rezerwacja nie jest aktywna.");
        }

        // 2. Pobierz i ZABLOKUJ miejsce (wymaga metody z @Lock w SeatRepository)
        // Zakładam, że dodałeś findAllByIdInWithLock do SeatRepository w poprzednim kroku
        Seat seat = seatRepository.findAllByIdInWithLock(List.of(seatId)).stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Miejsce nie istnieje"));

        // 3. Sprawdź dostępność (czy nie ma aktywnych biletów na to miejsce)
        List<Ticket> takenTickets = ticketRepository.findAllTakenTickets(reservation.getTickets().get(0).getSeance().getId(), LocalDateTime.now());
        boolean isTaken = takenTickets.stream().anyMatch(t -> t.getSeat().getId().equals(seatId));

        if (isTaken) {
            throw new IllegalStateException("Miejsce jest już zajęte.");
        }

        // 4. Stwórz bilet
        Ticket ticket = new Ticket();
        ticket.setReservation(reservation);
        ticket.setSeance(reservation.getTickets().get(0).getSeance());
        ticket.setSeat(seat);
        ticket.setTicketType(TicketType.REGULAR); // Domyślnie normalny
        ticket.setPrice(ticket.getSeance().getRegularTicketPrice());

        ticketRepository.save(ticket);
        reservation.getTickets().add(ticket);

        // Przelicz sumę
        recalculateReservationTotal(reservation);

        return mapToSummary(reservation);
    }
}