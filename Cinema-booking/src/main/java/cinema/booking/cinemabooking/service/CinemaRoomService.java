package cinema.booking.cinemabooking.service;

import cinema.booking.cinemabooking.model.CinemaRoom;
import cinema.booking.cinemabooking.repository.CinemaRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for managing cinema rooms.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CinemaRoomService {
    private final CinemaRoomRepository cinemaRoomRepository;

    /**
     * Retrieve all cinema rooms.
     *
     * @return List of CinemaRoom
     */
    public List<CinemaRoom> getAllCinemaRooms() {
        log.info("Fetching all cinema rooms");
        return cinemaRoomRepository.findAll();
    }
}
