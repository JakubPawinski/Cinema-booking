package cinema.booking.cinemabooking.model;

import jakarta.persistence.*;
import cinema.booking.cinemabooking.enums.TicketType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents a ticket for a movie seance in the cinema booking system.
 */
@Entity
@Getter
@Setter
@ToString
public class Ticket {

    /**
     * Unique identifier for the ticket.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique code associated with the ticket.
     * Generated after payment.
     */
    @Column(unique = true)
    private  String ticketCode;

    /**
     * Type of the ticket (e.g., REGULAR, REDUCED).
     */
    @NotNull(message = "Ticket type is required")
    @Enumerated(EnumType.STRING)
    private TicketType ticketType;

    /**
     * Price of the ticket.
     */
    @NotNull(message = "Ticket price is required")
    @PositiveOrZero(message = "Ticket price must be zero or positive")
    private double price;

    /**
     * Reservation associated with the ticket.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="reservation_id", nullable = false)
    @ToString.Exclude // Exclude reservation from toString to prevent circular dependencies
    private Reservation reservation;

    /**
     * Seance (movie screening) associated with the ticket.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="seance_id", nullable = false)
    @ToString.Exclude // Exclude seance from toString to prevent circular dependencies
    private Seance seance;

    /**
     * Seat associated with the ticket.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="seat_id", nullable = false)
    @ToString.Exclude // Exclude seat from toString to prevent circular dependencies
    private Seat seat;
}
