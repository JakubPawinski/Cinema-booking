package cinema.booking.cinemabooking.repository;
import cinema.booking.cinemabooking.model.CinemaRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Repository interface for CinemaRoom entity
 */
@Repository
public interface CinemaRoomRepository extends JpaRepository<CinemaRoom, Long> {
}
