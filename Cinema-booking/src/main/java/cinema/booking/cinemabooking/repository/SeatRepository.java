package cinema.booking.cinemabooking.repository;
import cinema.booking.cinemabooking.model.Seat;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findAllByCinemaRoom_Id(Long cinemaRoomId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s from Seat s WHERE s.id IN :ids")
    List<Seat> findAllByIdInWithLock(@Param("ids") List<Long> ids);
}
