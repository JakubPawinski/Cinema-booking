package cinema.booking.cinemabooking.repository;

import cinema.booking.cinemabooking.enums.ReservationStatus;
import cinema.booking.cinemabooking.model.Reservation;
import cinema.booking.cinemabooking.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for ReservationRepository.
 */
@DataJpaTest
public class ReservationRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private Reservation reservation;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setUsername("testuser");
        user = userRepository.save(user);

        reservation = new Reservation();
        reservation.setReservationCode("RES001");
        reservation.setCreatedAt(LocalDateTime.now());
        reservation.setExpiresAt(LocalDateTime.now().plusHours(2));
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setTotalPrice(50.0);
        reservation.setUser(user);
    }

    @Test
    void testSaveReservationReturnsNotNull() {
        Reservation saved = reservationRepository.save(reservation);
        assertThat(saved).isNotNull();
    }

    @Test
    void testSaveReservationGeneratesId() {
        Reservation saved = reservationRepository.save(reservation);
        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void testSaveReservationPreservesCode() {
        Reservation saved = reservationRepository.save(reservation);
        assertThat(saved.getReservationCode()).isEqualTo("RES001");
    }

    @Test
    void testSaveReservationPreservesStatus() {
        Reservation saved = reservationRepository.save(reservation);
        assertThat(saved.getStatus()).isEqualTo(ReservationStatus.PENDING);
    }

    @Test
    void testFindByIdReturnsNotNull() {
        Reservation saved = reservationRepository.save(reservation);
        Reservation found = reservationRepository.findById(saved.getId()).orElse(null);
        assertThat(found).isNotNull();
    }

    @Test
    void testFindByIdPreservesCode() {
        Reservation saved = reservationRepository.save(reservation);
        Reservation found = reservationRepository.findById(saved.getId()).orElse(null);
        assertThat(found.getReservationCode()).isEqualTo("RES001");
    }

    @Test
    void testFindAllByUserReturnsTwoReservations() {
        reservationRepository.save(reservation);

        Reservation reservation2 = new Reservation();
        reservation2.setReservationCode("RES002");
        reservation2.setCreatedAt(LocalDateTime.now());
        reservation2.setExpiresAt(LocalDateTime.now().plusHours(2));
        reservation2.setStatus(ReservationStatus.PAID);
        reservation2.setTotalPrice(75.0);
        reservation2.setUser(user);
        reservationRepository.save(reservation2);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Reservation> reservations = reservationRepository.findAllByUser(user, pageable);

        assertThat(reservations.getTotalElements()).isEqualTo(2);
    }

    @Test
    void testFindAllByUserReturnsCorrectPageSize() {
        reservationRepository.save(reservation);

        Reservation reservation2 = new Reservation();
        reservation2.setReservationCode("RES002");
        reservation2.setCreatedAt(LocalDateTime.now());
        reservation2.setExpiresAt(LocalDateTime.now().plusHours(2));
        reservation2.setStatus(ReservationStatus.PAID);
        reservation2.setTotalPrice(75.0);
        reservation2.setUser(user);
        reservationRepository.save(reservation2);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Reservation> reservations = reservationRepository.findAllByUser(user, pageable);

        assertThat(reservations.getContent()).hasSize(2);
    }

    @Test
    void testFindAllByUserAndStatusReturnsPendingReservations() {
        reservationRepository.save(reservation);

        Reservation reservation2 = new Reservation();
        reservation2.setReservationCode("RES002");
        reservation2.setCreatedAt(LocalDateTime.now());
        reservation2.setExpiresAt(LocalDateTime.now().plusHours(2));
        reservation2.setStatus(ReservationStatus.PAID);
        reservation2.setTotalPrice(75.0);
        reservation2.setUser(user);
        reservationRepository.save(reservation2);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Reservation> pendingReservations = reservationRepository.findAllByUserAndStatus(user, ReservationStatus.PENDING, pageable);

        assertThat(pendingReservations.getTotalElements()).isEqualTo(1);
    }

    @Test
    void testFindAllByUserAndStatusReturnsCorrectStatus() {
        reservationRepository.save(reservation);

        Reservation reservation2 = new Reservation();
        reservation2.setReservationCode("RES002");
        reservation2.setCreatedAt(LocalDateTime.now());
        reservation2.setExpiresAt(LocalDateTime.now().plusHours(2));
        reservation2.setStatus(ReservationStatus.PAID);
        reservation2.setTotalPrice(75.0);
        reservation2.setUser(user);
        reservationRepository.save(reservation2);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Reservation> pendingReservations = reservationRepository.findAllByUserAndStatus(user, ReservationStatus.PENDING, pageable);

        assertThat(pendingReservations.getContent().get(0).getStatus()).isEqualTo(ReservationStatus.PENDING);
    }

    @Test
    void testFindAllByStatusAndExpiresAtBeforeReturnsExpiredReservations() {
        reservationRepository.save(reservation);

        Reservation expiredReservation = new Reservation();
        expiredReservation.setReservationCode("RES002");
        expiredReservation.setCreatedAt(LocalDateTime.now().minusHours(3));
        expiredReservation.setExpiresAt(LocalDateTime.now().minusHours(1));
        expiredReservation.setStatus(ReservationStatus.PENDING);
        expiredReservation.setTotalPrice(50.0);
        expiredReservation.setUser(user);
        reservationRepository.save(expiredReservation);

        LocalDateTime cutoffTime = LocalDateTime.now();
        List<Reservation> expiredReservations = reservationRepository.findAllByStatusAndExpiresAtBefore(ReservationStatus.PENDING, cutoffTime);

        assertThat(expiredReservations).hasSize(1);
    }

    @Test
    void testFindAllByStatusAndExpiresAtBeforeReturnsCorrectCode() {
        reservationRepository.save(reservation);

        Reservation expiredReservation = new Reservation();
        expiredReservation.setReservationCode("RES002");
        expiredReservation.setCreatedAt(LocalDateTime.now().minusHours(3));
        expiredReservation.setExpiresAt(LocalDateTime.now().minusHours(1));
        expiredReservation.setStatus(ReservationStatus.PENDING);
        expiredReservation.setTotalPrice(50.0);
        expiredReservation.setUser(user);
        reservationRepository.save(expiredReservation);

        LocalDateTime cutoffTime = LocalDateTime.now();
        List<Reservation> expiredReservations = reservationRepository.findAllByStatusAndExpiresAtBefore(ReservationStatus.PENDING, cutoffTime);

        assertThat(expiredReservations.get(0).getReservationCode()).isEqualTo("RES002");
    }

    @Test
    void testUpdateReservationStatus() {
        Reservation saved = reservationRepository.save(reservation);
        saved.setStatus(ReservationStatus.PAID);
        Reservation updated = reservationRepository.save(saved);
        assertThat(updated.getStatus()).isEqualTo(ReservationStatus.PAID);
    }

    @Test
    void testDeleteReservation() {
        Reservation saved = reservationRepository.save(reservation);
        reservationRepository.deleteById(saved.getId());
        assertThat(reservationRepository.findById(saved.getId())).isEmpty();
    }
}
