package cinema.booking.cinemabooking.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import cinema.booking.cinemabooking.model.Reservation;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // Fetch all reservations made by a specific user, ordered by creation date descending
    List<Reservation> findByUser_UsernameOrderByCreatedAtDesc(String username);
}
