package cinema.booking.cinemabooking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;


/**
 * Entity representing a physical seat in a cinema room.
 * Stores information about the seat's row and number,
 * and its association with a specific cinema room.
 */
@Entity
@Getter
@Setter
@ToString
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"cinemaRoom_id", "rowNumber", "seatNumber"})
})
public class Seat {

    /**
     * Unique identifier for the seat.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Row number of the seat.
     */
    @Min(value = 1, message = "Row number must be at least 1")
    @Column(nullable = false)
    private int rowNumber;

    /**
     * Seat number within the row.
     */
    @Min(value = 1, message = "Seat number must be at least 1")
    @Column(nullable = false)
    private int seatNumber;

    /**
     * The cinema room to which this seat belongs.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cinemaRoom_id", nullable = false)
    @ToString.Exclude // Exclude cinemaRoom from toString to prevent circular dependencies
    private CinemaRoom cinemaRoom;
}
