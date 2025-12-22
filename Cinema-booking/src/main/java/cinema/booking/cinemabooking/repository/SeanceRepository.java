package cinema.booking.cinemabooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import cinema.booking.cinemabooking.model.Seance;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SeanceRepository extends JpaRepository<Seance, Long> {
    // Find seances by movie ID
    List<Seance> findByMovieId(Long movieId);

    // Find seances within a specific time range
    List<Seance> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);

    // Find overlapping seances in a specific cinema room
    @Query("SELECT s FROM Seance s WHERE s.cinemaRoom.id = :roomId " +
            "AND ((s.startTime <= :endTime AND s.endTime >= :startTime))")
    List<Seance> findOverlappingSeances(@Param("roomId") Long roomId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}
