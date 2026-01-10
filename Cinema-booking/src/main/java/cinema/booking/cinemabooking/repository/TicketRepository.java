package cinema.booking.cinemabooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import cinema.booking.cinemabooking.model.Ticket;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;


/**
 * Repository interface for Ticket entity
 */
@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    /**
     * Fetch all tickets for a specific seance that are either paid or pending (not yet expired)
     * @param seanceId the ID of the seance
     * @param now the current date and time
     * @return a list of taken tickets for the specified seance
     */
    @Query("SELECT t FROM Ticket t " +
            "JOIN t.reservation r " +
            "WHERE t.seance.id = :seanceId " +
            "AND (r.status = 'PAID' OR (r.status = 'PENDING' AND r.expiresAt > :now))")
    List<Ticket> findAllTakenTickets(@Param("seanceId") Long seanceId, @Param("now") LocalDateTime now);


    /**
     * Find all tickets associated with reservations made by a specific user
     * @param userId the ID of the user
     * @return a list of tickets associated with the user's reservations
     */
    List<Ticket> findByReservationUserId(Long userId);
}
