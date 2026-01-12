package cinema.booking.cinemabooking.repository;

import cinema.booking.cinemabooking.model.CinemaRoom;
import cinema.booking.cinemabooking.model.Seat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for SeatRepository.
 */
@DataJpaTest
public class SeatRepositoryTest {

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private CinemaRoomRepository cinemaRoomRepository;

    private CinemaRoom cinemaRoom;
    private Seat seat;

    @BeforeEach
    void setUp() {
        cinemaRoom = new CinemaRoom();
        cinemaRoom.setName("Sala 1");
        cinemaRoom = cinemaRoomRepository.save(cinemaRoom);

        seat = new Seat();
        seat.setRowNumber(1);
        seat.setSeatNumber(1);
        seat.setCinemaRoom(cinemaRoom);
    }

    @Test
    void testSaveSeatReturnsNotNull() {
        Seat saved = seatRepository.save(seat);
        assertThat(saved).isNotNull();
    }

    @Test
    void testSaveSeatGeneratesId() {
        Seat saved = seatRepository.save(seat);
        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void testSaveSeatPreservesRowNumber() {
        Seat saved = seatRepository.save(seat);
        assertThat(saved.getRowNumber()).isEqualTo(1);
    }

    @Test
    void testSaveSeatPreservesSeatNumber() {
        Seat saved = seatRepository.save(seat);
        assertThat(saved.getSeatNumber()).isEqualTo(1);
    }

    @Test
    void testFindByIdReturnsNotNull() {
        Seat saved = seatRepository.save(seat);
        Seat found = seatRepository.findById(saved.getId()).orElse(null);
        assertThat(found).isNotNull();
    }

    @Test
    void testFindByIdPreservesRowNumber() {
        Seat saved = seatRepository.save(seat);
        Seat found = seatRepository.findById(saved.getId()).orElse(null);
        assertThat(found.getRowNumber()).isEqualTo(1);
    }

    @Test
    void testFindByIdPreservesSeatNumber() {
        Seat saved = seatRepository.save(seat);
        Seat found = seatRepository.findById(saved.getId()).orElse(null);
        assertThat(found.getSeatNumber()).isEqualTo(1);
    }

    @Test
    void testFindAllByCinemaRoomIdReturnsMultipleSeats() {
        seatRepository.save(seat);

        Seat seat2 = new Seat();
        seat2.setRowNumber(1);
        seat2.setSeatNumber(2);
        seat2.setCinemaRoom(cinemaRoom);
        seatRepository.save(seat2);

        Seat seat3 = new Seat();
        seat3.setRowNumber(2);
        seat3.setSeatNumber(1);
        seat3.setCinemaRoom(cinemaRoom);
        seatRepository.save(seat3);

        List<Seat> seats = seatRepository.findAllByCinemaRoom_Id(cinemaRoom.getId());
        assertThat(seats).hasSize(3);
    }

    @Test
    void testFindAllByCinemaRoomIdAllSeatsMatchRoom() {
        seatRepository.save(seat);

        Seat seat2 = new Seat();
        seat2.setRowNumber(1);
        seat2.setSeatNumber(2);
        seat2.setCinemaRoom(cinemaRoom);
        seatRepository.save(seat2);

        Seat seat3 = new Seat();
        seat3.setRowNumber(2);
        seat3.setSeatNumber(1);
        seat3.setCinemaRoom(cinemaRoom);
        seatRepository.save(seat3);

        List<Seat> seats = seatRepository.findAllByCinemaRoom_Id(cinemaRoom.getId());
        assertThat(seats.stream().allMatch(s -> s.getCinemaRoom().getId().equals(cinemaRoom.getId()))).isTrue();
    }

    @Test
    void testFindAllByIdInWithLockReturnsMultipleSeats() {
        Seat saved1 = seatRepository.save(seat);

        Seat seat2 = new Seat();
        seat2.setRowNumber(1);
        seat2.setSeatNumber(2);
        seat2.setCinemaRoom(cinemaRoom);
        Seat saved2 = seatRepository.save(seat2);

        List<Long> ids = Arrays.asList(saved1.getId(), saved2.getId());
        List<Seat> seats = seatRepository.findAllByIdInWithLock(ids);

        assertThat(seats).hasSize(2);
    }

    @Test
    void testFindAllByIdInWithLockReturnsCorrectIds() {
        Seat saved1 = seatRepository.save(seat);

        Seat seat2 = new Seat();
        seat2.setRowNumber(1);
        seat2.setSeatNumber(2);
        seat2.setCinemaRoom(cinemaRoom);
        Seat saved2 = seatRepository.save(seat2);

        List<Long> ids = Arrays.asList(saved1.getId(), saved2.getId());
        List<Seat> seats = seatRepository.findAllByIdInWithLock(ids);

        assertThat(seats.stream().map(Seat::getId)).containsExactlyInAnyOrder(saved1.getId(), saved2.getId());
    }

    @Test
    void testFindAllByIdInWithLockEmptyList() {
        List<Seat> seats = seatRepository.findAllByIdInWithLock(Arrays.asList());
        assertThat(seats).isEmpty();
    }

    @Test
    void testUpdateSeatNumber() {
        Seat saved = seatRepository.save(seat);
        saved.setSeatNumber(5);
        Seat updated = seatRepository.save(saved);
        assertThat(updated.getSeatNumber()).isEqualTo(5);
    }

    @Test
    void testDeleteSeat() {
        Seat saved = seatRepository.save(seat);
        seatRepository.deleteById(saved.getId());
        assertThat(seatRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void testUniqueSeatConstraint() {
        seatRepository.save(seat);
        assertThat(seatRepository.count()).isEqualTo(1);
    }
}
