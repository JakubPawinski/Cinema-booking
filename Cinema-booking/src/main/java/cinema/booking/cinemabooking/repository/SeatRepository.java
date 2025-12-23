package cinema.booking.cinemabooking.repository;
import cinema.booking.cinemabooking.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findAllByCinemaRoom_Id(Long cinemaRoomId);
}
