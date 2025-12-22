package cinema.booking.cinemabooking.repository;
import cinema.booking.cinemabooking.model.CinemaRoom;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CinemaRoomRepository extends JpaRepository<CinemaRoom, Long> {
}
