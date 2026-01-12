package cinema.booking.cinemabooking.repository;

import cinema.booking.cinemabooking.model.CinemaRoom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for CinemaRoomRepository.
 */
@DataJpaTest
public class CinemaRoomRepositoryTest {

    @Autowired
    private CinemaRoomRepository cinemaRoomRepository;

    private CinemaRoom cinemaRoom;

    @BeforeEach
    void setUp() {
        cinemaRoom = new CinemaRoom();
        cinemaRoom.setName("Sala 1");
    }

    @Test
    void testSaveCinemaRoomReturnsNotNull() {
        CinemaRoom saved = cinemaRoomRepository.save(cinemaRoom);
        assertThat(saved).isNotNull();
    }

    @Test
    void testSaveCinemaRoomGeneratesId() {
        CinemaRoom saved = cinemaRoomRepository.save(cinemaRoom);
        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void testSaveCinemaRoomPreservesName() {
        CinemaRoom saved = cinemaRoomRepository.save(cinemaRoom);
        assertThat(saved.getName()).isEqualTo("Sala 1");
    }

    @Test
    void testFindByIdReturnsNotNull() {
        CinemaRoom saved = cinemaRoomRepository.save(cinemaRoom);
        CinemaRoom found = cinemaRoomRepository.findById(saved.getId()).orElse(null);
        assertThat(found).isNotNull();
    }

    @Test
    void testFindByIdPreservesName() {
        CinemaRoom saved = cinemaRoomRepository.save(cinemaRoom);
        CinemaRoom found = cinemaRoomRepository.findById(saved.getId()).orElse(null);
        assertThat(found.getName()).isEqualTo("Sala 1");
    }

    @Test
    void testFindAllReturnsMultipleCinemaRooms() {
        cinemaRoomRepository.save(cinemaRoom);
        CinemaRoom room2 = new CinemaRoom();
        room2.setName("Sala 2");
        cinemaRoomRepository.save(room2);

        assertThat(cinemaRoomRepository.findAll()).hasSize(2);
    }

    @Test
    void testUpdateCinemaRoomName() {
        CinemaRoom saved = cinemaRoomRepository.save(cinemaRoom);
        saved.setName("Sala Zmieniona");
        CinemaRoom updated = cinemaRoomRepository.save(saved);
        assertThat(updated.getName()).isEqualTo("Sala Zmieniona");
    }

    @Test
    void testDeleteCinemaRoom() {
        CinemaRoom saved = cinemaRoomRepository.save(cinemaRoom);
        cinemaRoomRepository.deleteById(saved.getId());
        assertThat(cinemaRoomRepository.findById(saved.getId())).isEmpty();
    }
}
