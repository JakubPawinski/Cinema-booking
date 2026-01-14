package cinema.booking.cinemabooking.service;

import cinema.booking.cinemabooking.dto.request.CreateReservationDto;
import cinema.booking.cinemabooking.dto.response.ReservationSummaryDto;
import cinema.booking.cinemabooking.enums.ReservationStatus;
import cinema.booking.cinemabooking.enums.TicketType;
import cinema.booking.cinemabooking.exception.InvalidReservationActionException;
import cinema.booking.cinemabooking.exception.ResourceNotFoundException;
import cinema.booking.cinemabooking.exception.SeatAlreadyOccupiedException;
import cinema.booking.cinemabooking.mapper.ReservationMapper;
import cinema.booking.cinemabooking.mapper.TicketMapper;
import cinema.booking.cinemabooking.model.*;
import cinema.booking.cinemabooking.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service for managing reservations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {

    private final TicketRepository ticketRepository;
    private final SeanceRepository seanceRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final PdfTicketService pdfTicketService;
    private final ReservationMapper reservationMapper;
    private final TicketMapper ticketMapper;

    /**
     * Create a new reservation with proper concurrency handling.
     *
     * @param request  the reservation request data
     * @param username the username of the user making the reservation
     * @return summary of the created reservation
     * @throws ResourceNotFoundException          if the seance or user is not found
     * @throws SeatAlreadyOccupiedException       if any of the requested seats are already taken
     * @throws InvalidReservationActionException  if the reservation request is invalid
     */
    @Transactional
    public ReservationSummaryDto createReservation(CreateReservationDto request, String username) {
        log.info("Attempting to create reservation for user: {} on seance ID: {}", username, request.getSeanceId());

        // Fetch seance and user
        Seance seance = seanceRepository.findById(request.getSeanceId())
                .orElseThrow(() -> {
                    log.warn("Seance with ID {} not found", request.getSeanceId());
                    return new ResourceNotFoundException("Seance not found");
                });

        // Check if seance is in the past
        if (seance.getStartTime().isBefore(LocalDateTime.now())) {
            log.warn("Cannot create reservation for past seance ID: {}", seance.getId());
            throw new InvalidReservationActionException("Cannot create reservation for past seance.");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User with username {} not found", username);
                    return new ResourceNotFoundException("User not found");
                });

        // Collect requested seat IDs
        List<Long> requestedSeatIds = request.getTickets().stream()
                .map(CreateReservationDto.TicketRequest::getSeatId)
                .toList();

        // Lock the seats to prevent concurrent modifications
        log.debug("Locking seats for reservation: {}", requestedSeatIds);
        List<Seat> lockedSeats = seatRepository.findAllByIdInWithLock(requestedSeatIds);

        if (lockedSeats.size() != requestedSeatIds.size()) {
            log.warn("Invalid seat IDs in the reservation request: {}", requestedSeatIds);
            throw new InvalidReservationActionException("Invalid seat IDs in the request");
        }

        // Check if any of the requested seats are already taken
        List<Ticket> takenTickets = ticketRepository.findAllTakenTickets(seance.getId(), LocalDateTime.now());
        List<Long> takenSeatIds = takenTickets.stream()
                .map(ticket -> ticket.getSeat().getId())
                .toList();

        for (Long seatId : requestedSeatIds) {
            if (takenSeatIds.contains(seatId)) {
                log.warn("Seat ID {} is already taken", seatId);
                throw new SeatAlreadyOccupiedException("Seat " + seatId + " is already taken");
            }
        }

        // Create reservation and tickets
        Reservation reservation = new Reservation();
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setCreatedAt(LocalDateTime.now());
        reservation.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        reservation.setUser(user);

        List<Ticket> tickets = new ArrayList<>();
        double totalPrice = 0;

        // Iterate over ticket requests to create Ticket entities
        for (CreateReservationDto.TicketRequest ticketRequest : request.getTickets()) {
            Seat seat = lockedSeats.stream()
                    .filter(s -> s.getId().equals(ticketRequest.getSeatId()))
                    .findFirst()
                    .orElseThrow();

            TicketType ticketType = ticketRequest.getTicketType();
            double price = getTicketPrice(seance, ticketType);

            Ticket ticket = ticketMapper.toEntity(reservation, seance, seat, ticketType, price);

            tickets.add(ticket);
            totalPrice += price;
        }

        reservation.setTickets(tickets);
        reservation.setTotalPrice(totalPrice);

        Reservation savedReservation = reservationRepository.save(reservation);
        log.info("Reservation created successfully with ID: {} for user: {}", savedReservation.getId(), username);

        return reservationMapper.toSummaryDto(savedReservation);
    }


    /**
     * Pay for a reservation, updating its status and generating unique codes.
     * @param reservationId the ID of the reservation to pay for
     * @throws ResourceNotFoundException if the reservation is not found
     * @throws InvalidReservationActionException if the reservation cannot be paid
     */
    @Transactional
    public void payForReservation(Long reservationId) {
        log.info("Processing payment for reservation ID: {}", reservationId);

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> {
                    log.warn("Reservation with ID {} not found", reservationId);
                    return new ResourceNotFoundException("Reservation not found");
                });

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            log.warn("Reservation ID {} cannot be paid in its current status: {}", reservationId, reservation.getStatus());
            throw new InvalidReservationActionException("Reservation cannot be paid in its current status.");
        }

        if (reservation.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.warn("Reservation ID {} has expired and cannot be paid", reservationId);
            reservation.setStatus(ReservationStatus.CANCELLED);
            reservationRepository.save(reservation);
            throw new InvalidReservationActionException("Reservation has expired and cannot be paid.");
        }

        // Update reservation status and generate codes
        reservation.setStatus(ReservationStatus.PAID);
        reservation.setReservationCode(UUID.randomUUID().toString());

        for (Ticket ticket : reservation.getTickets()) {
            ticket.setTicketCode(UUID.randomUUID().toString());
        }
        reservationRepository.save(reservation);
        log.info("Payment processed successfully for reservation ID: {}", reservationId);
    }

    /**
     * Get paginated reservations for a user, optionally filtered by status.
     * @param username the username of the user
     * @param page the page number
     * @param size the page size
     * @param status optional reservation status to filter by
     * @return paginated list of reservation summaries
     * @throws ResourceNotFoundException if the user is not found
     */
    public Page<ReservationSummaryDto> getUserReservations(String username, int page, int size, ReservationStatus status) {
        log.debug("Fetching reservations for user: {}, page: {}, size: {}, status {}", username, page, size, status);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User with username {} not found", username);
                    return new ResourceNotFoundException("User not found");
                });

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Reservation> reservations;
        if (status != null) {
            reservations = reservationRepository.findAllByUserAndStatus(user, status, pageable);
        } else {
            reservations = reservationRepository.findAllByUser(user, pageable);
        }

        return reservations.map(reservationMapper::toSummaryDto);
    }

    /**
     * Remove a ticket from a reservation and recalculate the total price.
     * @param reservationId the ID of the reservation
     * @param ticketId the ID of the ticket to remove
     * @return updated reservation summary
     * @throws ResourceNotFoundException if the reservation or ticket is not found
     * @throws InvalidReservationActionException if the reservation cannot be modified
     */
    @Transactional
    public ReservationSummaryDto removeTicket(Long reservationId, Long ticketId) {
        log.info("Removing ticket ID: {} from reservation ID: {}", ticketId, reservationId);

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> {
                    log.warn("Reservation with ID {} not found", reservationId);
                    return new ResourceNotFoundException("Reservation not found");
                });

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            log.warn("Cannot modify ticket in reservation ID {} with status: {}", reservationId, reservation.getStatus());
            throw new InvalidReservationActionException("Cannot modify a paid reservation.");
        }

        Ticket ticketToRemove = reservation.getTickets().stream()
                .filter(t -> t.getId().equals(ticketId))
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("Ticket ID {} not found in reservation ID {}", ticketId, reservationId);
                    return new ResourceNotFoundException("Ticket does not exist in the reservation");
                });

        reservation.getTickets().remove(ticketToRemove);
        ticketRepository.delete(ticketToRemove);
        recalculateReservationTotal(reservation);

        log.info("Ticket ID: {} removed successfully from reservation ID: {}", ticketId, reservationId);

        // If no tickets remain, cancel the reservation
        if (reservation.getTickets().isEmpty()) {
            log.info("Reservation ID: {} has no more tickets, cancelling reservation", reservationId);
            reservation.setStatus(ReservationStatus.CANCELLED);
            reservationRepository.save(reservation);
        }
        return reservationMapper.toSummaryDto(reservation);
    }

    /**
     * Update the ticket type of specific ticket in a reservation.
     * @param reservationId the ID of the reservation
     * @param ticketId the ID of the ticket to update
     * @param newType the new ticket type
     * @return updated reservation summary
     * @throws ResourceNotFoundException if the reservation or ticket is not found
     * @throws InvalidReservationActionException if the reservation cannot be modified
     */
    @Transactional
    public ReservationSummaryDto updateTicketType(Long reservationId, Long ticketId, TicketType newType) {
        log.info("Updating ticket ID: {} in reservation ID: {} to type: {}", ticketId, reservationId, newType);

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> {
                    log.warn("Reservation with ID {} not found", reservationId);
                    return new ResourceNotFoundException("Reservation not found");
                });

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            log.warn("Cannot edit ticket in reservation ID {} with status: {}", reservationId, reservation.getStatus());
            throw new InvalidReservationActionException("Cannot modify a paid reservation.");
        }

        // Find the ticket to update
        Ticket ticket = reservation.getTickets().stream()
                .filter(t -> t.getId().equals(ticketId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Ticket does not exist in the reservation"));

        // Update ticket type and price
        ticket.setTicketType(newType);
        Seance seance = ticket.getSeance();
        ticket.setPrice(getTicketPrice(seance, newType));
        ticketRepository.save(ticket);

        recalculateReservationTotal(reservation);

        log.info("Ticket ID: {} updated successfully in reservation ID: {}", ticketId, reservationId);
        return reservationMapper.toSummaryDto(reservation);
    }

    /**
     * Cancel a reservation if it is not paid.
     * @param reservationId the ID of the reservation to cancel
     * @throws ResourceNotFoundException if the reservation is not found
     * @throws InvalidReservationActionException if the reservation cannot be cancelled
     */
    public void cancelReservation(Long reservationId) {
        log.info("Cancelling reservation ID: {}", reservationId);

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> {
                    log.warn("Reservation with ID {} not found", reservationId);
                    return new ResourceNotFoundException("Reservation not found");
                });

        if (reservation.getStatus() == ReservationStatus.PAID) {
            log.warn("Cannot cancel paid reservation ID: {}", reservationId);
            throw new InvalidReservationActionException("Cannot cancel a paid reservation.");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
        log.info("Reservation ID: {} cancelled successfully", reservationId);
    }

    /**
     * Get detailed information about a reservation for a user.
     * @param reservationId the ID of the reservation
     * @param username the username of the user
     * @return the reservation details
     * @throws ResourceNotFoundException if the reservation is not found
     * @throws SecurityException if the user does not own the reservation
     */
    @Transactional(readOnly = true)
    public Reservation getReservationDetails(Long reservationId, String username) {
        log.debug("Fetching details for reservation ID: {} for user: {}", reservationId, username);

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> {
                    log.warn("Reservation with ID {} not found", reservationId);
                    return new ResourceNotFoundException("Reservation not found");
                });

        if (!reservation.getUser().getUsername().equals(username)) {
            log.warn("User: {} attempted to access reservation ID: {} without permission", username, reservationId);
            throw new SecurityException("Access denied to this reservation.");
        }

        reservation.getTickets().size(); // Initialize lazy loading
        return reservation;
    }

    /**
     * Add a ticket to an existing reservation.
     * @param reservationId the ID of the reservation
     * @param seatId the ID of the seat to add
     * @return updated reservation summary
     * @throws ResourceNotFoundException if the reservation or seat is not found
     * @throws InvalidReservationActionException if the reservation cannot be modified
     * @throws SeatAlreadyOccupiedException if the seat is already taken
     */
    @Transactional
    public ReservationSummaryDto addTicketToReservation(Long reservationId, Long seatId) {
        log.info("Adding ticket for seat ID: {} to reservation ID: {}", seatId, reservationId);

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> {
                    log.warn("Reservation with ID {} not found", reservationId);
                    return new ResourceNotFoundException("Reservation not found");
                });

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            log.warn("Cannot add ticket to reservation ID {} with status: {}", reservationId, reservation.getStatus());
            throw new InvalidReservationActionException("Cannot modify a paid reservation.");
        }

        // Lock the seat
        Seat seat = seatRepository.findAllByIdInWithLock(List.of(seatId)).stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Seat not found"));

        // Check if the seat is already taken for the seance
        List<Ticket> takenTickets = ticketRepository.findAllTakenTickets(reservation.getTickets().get(0).getSeance().getId(), LocalDateTime.now());
        boolean isTaken = takenTickets.stream().anyMatch(t -> t.getSeat().getId().equals(seatId));

        if (isTaken) {
            log.warn("Seat ID {} is already taken for reservation ID: {}", seatId, reservationId);
            throw new SeatAlreadyOccupiedException("Seat is already taken.");
        }

        Ticket ticket = ticketMapper.toEntity(reservation,
                reservation.getTickets().getFirst().getSeance(),
                seat,
                TicketType.REGULAR,
                reservation.getTickets().getFirst().getSeance().getRegularTicketPrice());

        ticketRepository.save(ticket);
        reservation.getTickets().add(ticket);

        recalculateReservationTotal(reservation);

        log.info("Ticket for seat ID: {} added successfully to reservation ID: {}", seatId, reservationId);

        return reservationMapper.toSummaryDto(reservation);
    }

    /**
     * Generate a PDF ticket for a paid reservation.
     * @param reservationId the ID of the reservation
     * @param username the username of the user
     * @return ByteArrayInputStream containing the PDF data
     * @throws ResourceNotFoundException if the reservation is not found
     * @throws InvalidReservationActionException if the reservation is not paid
     */
    @Transactional(readOnly = true)
    public ByteArrayInputStream generatePdfForReservation(Long reservationId, String username) {
        log.info("Generating PDF ticket for reservation ID: {} for user: {}", reservationId, username);
        Reservation reservation = getReservationDetails(reservationId, username);

        if (reservation.getStatus() != ReservationStatus.PAID) {
            log.warn("Cannot generate PDF for unpaid reservation ID: {}", reservationId);
            throw new InvalidReservationActionException("Cannot generate PDF for unpaid reservation.");
        }

        return pdfTicketService.generateReservationPdf(reservation);
    }

    /**
     * Scheduled task to automatically cancel expired reservations.
     * Runs every minute.
     */
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void autoCancelExpiredReservations() {
        LocalDateTime now = LocalDateTime.now();
        List<Reservation> expiredReservations = reservationRepository.findAllByStatusAndExpiresAtBefore(ReservationStatus.PENDING, now);

        if (!expiredReservations.isEmpty()) {
            log.info("Found {} expired reservations to cancel", expiredReservations.size());
            for (Reservation reservation : expiredReservations) {
                reservation.setStatus(ReservationStatus.CANCELLED);

                //TODO: send notification to user about cancellation
            }
            reservationRepository.saveAll(expiredReservations);
            log.info("Successfully cancelled {} expired reservations.", expiredReservations.size());
        }
    }

    /**
     * Get the ticket price based on the ticket type.
     * @param seance the seance for which the ticket is being purchased
     * @param ticketType the type of ticket (REGULAR or REDUCED)
     * @return the price of the ticket
     */
    private double getTicketPrice(Seance seance, TicketType ticketType) {
        return ticketType == TicketType.REDUCED
                ? seance.getReducedTicketPrice()
                : seance.getRegularTicketPrice();
    }

    /**
     * Recalculate the total price of a reservation based on its tickets.
     * @param reservation the reservation to recalculate
     */
    private void recalculateReservationTotal(Reservation reservation) {
        double total = reservation.getTickets().stream()
                .mapToDouble(Ticket::getPrice)
                .sum();
        reservation.setTotalPrice(total);
        reservationRepository.save(reservation);
    }
}