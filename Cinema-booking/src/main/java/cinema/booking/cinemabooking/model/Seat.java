package cinema.booking.cinemabooking.model;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Data
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int rowNumber;
    private int seatNumber;

    @ManyToOne
    @JoinColumn(name = "cinemaRoom_id", nullable = false)
    private CinemaRoom cinemaRoom;
}
