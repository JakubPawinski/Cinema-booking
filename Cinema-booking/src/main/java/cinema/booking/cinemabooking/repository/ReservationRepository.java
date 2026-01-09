package cinema.booking.cinemabooking.repository;
import cinema.booking.cinemabooking.enums.ReservationStatus;
import cinema.booking.cinemabooking.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import cinema.booking.cinemabooking.model.Reservation;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Page<Reservation> findAllByUser(User user, Pageable pageable);

    Page<Reservation> findAllByUserAndStatus(User user, ReservationStatus status, Pageable pageable);

    List<Reservation> findAllByStatusAndExpiresAtBefore(ReservationStatus status, LocalDateTime now);

}
