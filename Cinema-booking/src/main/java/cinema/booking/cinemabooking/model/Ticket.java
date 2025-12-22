package cinema.booking.cinemabooking.model;

import jakarta.persistence.*;
import cinema.booking.cinemabooking.enums.TicketType;

@Entity
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TicketType ticketType;

    private double price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="reservation_id", nullable = false)
    private Reservation reservation;

    @ManyToOne
    @JoinColumn(name="seance_id", nullable = false)
    private Seance seance;

    @ManyToOne
    @JoinColumn(name="seat_id", nullable = false)
    private Seat seat;
}
