package cinema.booking.cinemabooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import cinema.booking.cinemabooking.model.Seance;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Seance entity
 */
@Repository
public interface SeanceRepository extends JpaRepository<Seance, Long> {

    /**
     * Find seances by movie ID
     * @param movieId the ID of the movie
     * @return a list of seances associated with the specified movie
     */
    List<Seance> findByMovieId(Long movieId);

    /**
     * Find seances within a specific time range
     * @param start the start time of the range
     * @param end the end time of the range
     * @return a list of seances occurring within the specified time range
     */
    List<Seance> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Find seances by cinema room ID
     * @param cinemaRoomId the ID of the cinema room
     * @return a list of seances scheduled in the specified cinema room
     */
    List<Seance> findByCinemaRoomId(Long cinemaRoomId);

    /**
     * Find seances that overlap with a given time range in a specific cinema room
     * @param roomId the ID of the cinema room
     * @param startTime the start time of the range
     * @param endTime the end time of the range
     * @return a list of seances that overlap with the specified time range in the given cinema room
     */
    @Query("SELECT s FROM Seance s WHERE s.cinemaRoom.id = :roomId " +
            "AND ((s.startTime <= :endTime AND s.endTime >= :startTime))")
    List<Seance> findOverlappingSeances(@Param("roomId") Long roomId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}
