package cinema.booking.cinemabooking.repository;
import cinema.booking.cinemabooking.model.Seat;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Seat entity
 */
@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    /**
     * Find all seats by cinema room ID
     * @param cinemaRoomId the ID of the cinema room
     * @return a list of seats in the specified cinema room
     */
    List<Seat> findAllByCinemaRoom_Id(Long cinemaRoomId);

    /**
     * Find all seats by a list of IDs with a pessimistic write lock
     * This is used to prevent concurrent modifications when reserving seats
     * @param ids the list of seat IDs
     * @return a list of seats with the specified IDs
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s from Seat s WHERE s.id IN :ids")
    List<Seat> findAllByIdInWithLock(@Param("ids") List<Long> ids);
}
