package cinema.booking.cinemabooking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a movie screening (seance).
 */
@Entity
@Getter
@Setter
@ToString
public class Seance {

    /**
     * Unique identifier for the seance.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Start time of the seance.
     */
    @NotNull(message = "Start time is required")
    @Column(nullable = false)
    private LocalDateTime startTime;

    /**
     * End time of the seance.
     * Calculated based on movie duration.
     */
    @NotNull(message = "End time is required")
    @Column(nullable = false)
    private LocalDateTime endTime;

    /**
     * Regular ticket price for the seance.
     */
    @PositiveOrZero(message = "Regular ticket price must be zero or positive")
    @NotNull(message = "Regular ticket price is required")
    @Column(nullable = false)
    private double regularTicketPrice;

    /**
     * Reduced ticket price for the seance.
     */
    @PositiveOrZero(message = "Reduced ticket price must be zero or positive")
    @NotNull(message = "Reduced ticket price is required")
    @Column(nullable = false)
    private double reducedTicketPrice;

    /**
     * Movie being screened in the seance.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    @ToString.Exclude // Exclude movie from toString to prevent circular dependencies
    private Movie movie;

    /**
     * Cinema room where the seance takes place.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cinemaRoom_id", nullable = false)
    @ToString.Exclude // Exclude cinemaRoom from toString to prevent circular dependencies
    private CinemaRoom cinemaRoom;

    /**
     * List of tickets associated with the seance.
     */
    @OneToMany(mappedBy = "seance")
    @ToString.Exclude // Exclude tickets from toString to prevent circular dependencies
    private List<Ticket> tickets;
}
