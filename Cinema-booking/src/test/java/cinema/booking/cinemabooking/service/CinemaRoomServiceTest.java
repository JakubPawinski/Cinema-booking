package cinema.booking.cinemabooking.service;

import cinema.booking.cinemabooking.model.CinemaRoom;
import cinema.booking.cinemabooking.repository.CinemaRoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for CinemaRoomService")
class CinemaRoomServiceTest {

    @Mock
    private CinemaRoomRepository cinemaRoomRepository;

    @InjectMocks
    private CinemaRoomService cinemaRoomService;

    @BeforeEach
    void setUp() {
        CinemaRoom room1 = new CinemaRoom();
        room1.setId(1L);
        room1.setName("IMAX");

        CinemaRoom room2 = new CinemaRoom();
        room2.setId(2L);
        room2.setName("Dolby Cinema");

        when(cinemaRoomRepository.findAll()).thenReturn(List.of(room1, room2));
    }

    @Test
    @DisplayName("Should return exactly two cinema rooms")
    void shouldReturnTwoRooms() {
        assertEquals(2, cinemaRoomService.getAllCinemaRooms().size());
    }

    @Test
    @DisplayName("Should return 'IMAX' as the first room name")
    void shouldReturnImaxAsFirstRoom() {
        assertEquals("IMAX", cinemaRoomService.getAllCinemaRooms().get(0).getName());
    }

    @Test
    @DisplayName("Should return 'Dolby Cinema' as the second room name")
    void shouldReturnDolbyAsSecondRoom() {
        assertEquals("Dolby Cinema", cinemaRoomService.getAllCinemaRooms().get(1).getName());
    }

    @Test
    @DisplayName("Should invoke repository findAll method exactly once")
    void shouldInvokeRepository() {
        cinemaRoomService.getAllCinemaRooms();
        verify(cinemaRoomRepository, times(1)).findAll();
    }
}
