package cinema.booking.cinemabooking.model;

import cinema.booking.cinemabooking.enums.ReservationStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

/**
 * Entity representing a cinema reservation.
 */
@Entity
@Getter
@Setter
@ToString
public class Reservation {
    /**
     * Unique identifier for the reservation.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique reservation code.
     */
    @Column(unique = true)
    private String reservationCode;

    /**
     * Timestamp when the reservation was created.
     */
    @NotNull(message = "Creation time is required")
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the reservation expires.
     */
    @NotNull(message = "Expiration time is required")
    @Column(nullable = false)
    private LocalDateTime expiresAt;

    /**
     * Status of the reservation.
     */
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Reservation status is required")
    @Column(nullable = false)
    private ReservationStatus status;

    /**
     * Total price of the reservation.
     */
    @NotNull(message = "Total price is required")
    @Column(nullable = false)
    @PositiveOrZero(message = "Total price must be zero or positive")
    private double totalPrice;

    /**
     * User who made the reservation.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "user_id", nullable = false)
    @ToString.Exclude // Exclude user from toString to prevent circular dependencies
    private User user;

    /**
     * List of tickets associated with the reservation.
     */
    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @ToString.Exclude // Exclude tickets from toString to prevent circular dependencies
    private List<Ticket> tickets = new ArrayList<>();
}
