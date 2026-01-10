package cinema.booking.cinemabooking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a cinema room.
 */
@Entity
@Getter
@Setter
@ToString
public class CinemaRoom {

    /**
     * Unique identifier for the cinema room.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the cinema room.
     */
    @NotBlank(message = "Cinema room name is required")
    @Column(nullable = false)
    private String name;

    /**
     * List of seats in the cinema room.
     */
    @OneToMany(mappedBy = "cinemaRoom", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude // Exclude seats from toString to prevent circular dependencies
    private List<Seat> seats = new ArrayList<>();

    /**
     * List of seances (movie screenings) scheduled in the cinema room.
     */
    @OneToMany(mappedBy = "cinemaRoom", fetch = FetchType.LAZY)
    @ToString.Exclude // Exclude seances from toString to prevent circular dependencies
    private List<Seance> seances = new ArrayList<>();
}
