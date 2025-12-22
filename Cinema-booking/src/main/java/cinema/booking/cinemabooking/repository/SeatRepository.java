package cinema.booking.cinemabooking.repository;
import cinema.booking.cinemabooking.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<Seat, Long> {
}
