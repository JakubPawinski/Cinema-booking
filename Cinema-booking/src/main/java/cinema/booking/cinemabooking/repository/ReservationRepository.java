package cinema.booking.cinemabooking.repository;
import cinema.booking.cinemabooking.enums.ReservationStatus;
import cinema.booking.cinemabooking.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import cinema.booking.cinemabooking.model.Reservation;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Reservation entity
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /**
     * Find all reservations made by a specific user with pagination support
     * @param user the user whose reservations are to be fetched
     * @param pageable pagination information
     * @return a paginated list of reservations made by the specified user
     */
    Page<Reservation> findAllByUser(User user, Pageable pageable);

    /**
     * Find all reservations made by a specific user with a specific status and pagination support
     * @param user the user whose reservations are to be fetched
     * @param status the status of the reservations to filter by
     * @param pageable pagination information
     * @return a paginated list of reservations made by the specified user with the specified status
     */
    Page<Reservation> findAllByUserAndStatus(User user, ReservationStatus status, Pageable pageable);

    /**
     * Find all reservations with a specific status that have expired before the given time
     * Used in scheduler that cleans up expired reservations
     * @param status the status of the reservations to filter by
     * @param now the cutoff time for expiration
     * @return a list of reservations with the specified status that have expired before the given time
     */
    List<Reservation> findAllByStatusAndExpiresAtBefore(ReservationStatus status, LocalDateTime now);

}
