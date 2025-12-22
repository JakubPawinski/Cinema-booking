package cinema.booking.cinemabooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import cinema.booking.cinemabooking.model.Ticket;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    // Fetch all tickets for a specific seance that are either paid or pending but not expired
    @Query("SELECT t FROM Ticket t " +
            "JOIN t.reservation r " +
            "WHERE t.seance.id = :seanceId " +
            "AND (r.status = 'PAID' OR (r.status = 'PENDING' AND r.expiresAt > :now))")
    List<Ticket> findAllTakenTickets(@Param("seanceId") Long seanceId, @Param("now") LocalDateTime now);


    // Fetch all tickets associated with a specific user's reservations
    List<Ticket> findByReservation_User_Id(Long userId);
}
