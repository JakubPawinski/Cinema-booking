package cinema.booking.cinemabooking.mapper;

import cinema.booking.cinemabooking.dto.response.SeatDto;
import cinema.booking.cinemabooking.model.Seat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for SeatMapper.
 */
public class SeatMapperTest {

    private SeatMapper seatMapper;
    private Seat seat;

    @BeforeEach
    void setUp() {
        seatMapper = new SeatMapper();

        seat = new Seat();
        seat.setId(1L);
        seat.setRowNumber(5);
        seat.setSeatNumber(12);
    }

    @Test
    void testToDtoReturnsNotNull() {
        SeatDto result = seatMapper.toDto(seat, false);
        assertThat(result).isNotNull();
    }

    @Test
    void testToDtoPreservesId() {
        SeatDto result = seatMapper.toDto(seat, false);
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void testToDtoPreservesRowNumber() {
        SeatDto result = seatMapper.toDto(seat, false);
        assertThat(result.getRowNumber()).isEqualTo(5);
    }

    @Test
    void testToDtoPreservesSeatNumber() {
        SeatDto result = seatMapper.toDto(seat, false);
        assertThat(result.getSeatNumber()).isEqualTo(12);
    }

    @Test
    void testToDtoSetIsOccupiedToTrue() {
        SeatDto result = seatMapper.toDto(seat, true);
        assertThat(result.isOccupied()).isTrue();
    }

    @Test
    void testToDtoSetIsOccupiedToFalse() {
        SeatDto result = seatMapper.toDto(seat, false);
        assertThat(result.isOccupied()).isFalse();
    }

    @Test
    void testToDtoWithDifferentRowNumber() {
        seat.setRowNumber(10);
        SeatDto result = seatMapper.toDto(seat, false);
        assertThat(result.getRowNumber()).isEqualTo(10);
    }

    @Test
    void testToDtoWithDifferentSeatNumber() {
        seat.setSeatNumber(25);
        SeatDto result = seatMapper.toDto(seat, false);
        assertThat(result.getSeatNumber()).isEqualTo(25);
    }

    @Test
    void testToDtoWithDifferentId() {
        seat.setId(99L);
        SeatDto result = seatMapper.toDto(seat, false);
        assertThat(result.getId()).isEqualTo(99L);
    }

    @Test
    void testToDtoWithOccupiedSeatRowNumber() {
        seat.setRowNumber(15);
        SeatDto result = seatMapper.toDto(seat, true);
        assertThat(result.getRowNumber()).isEqualTo(15);
    }

    @Test
    void testToDtoWithOccupiedSeatSeatNumber() {
        seat.setSeatNumber(8);
        SeatDto result = seatMapper.toDto(seat, true);
        assertThat(result.getSeatNumber()).isEqualTo(8);
    }

    @Test
    void testToDtoWithOccupiedSeatIsOccupied() {
        SeatDto result = seatMapper.toDto(seat, true);
        assertThat(result.isOccupied()).isTrue();
    }

    @Test
    void testToDtoWithUnoccupiedSeatRowNumber() {
        seat.setRowNumber(1);
        SeatDto result = seatMapper.toDto(seat, false);
        assertThat(result.getRowNumber()).isEqualTo(1);
    }

    @Test
    void testToDtoWithUnoccupiedSeatSeatNumber() {
        seat.setSeatNumber(1);
        SeatDto result = seatMapper.toDto(seat, false);
        assertThat(result.getSeatNumber()).isEqualTo(1);
    }

    @Test
    void testToDtoWithUnoccupiedSeatIsOccupied() {
        SeatDto result = seatMapper.toDto(seat, false);
        assertThat(result.isOccupied()).isFalse();
    }

    @Test
    void testToDtoPreservesIdWithOccupiedTrue() {
        SeatDto result = seatMapper.toDto(seat, true);
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void testToDtoPreservesRowNumberWithOccupiedTrue() {
        SeatDto result = seatMapper.toDto(seat, true);
        assertThat(result.getRowNumber()).isEqualTo(5);
    }

    @Test
    void testToDtoPreservesSeatNumberWithOccupiedTrue() {
        SeatDto result = seatMapper.toDto(seat, true);
        assertThat(result.getSeatNumber()).isEqualTo(12);
    }

    @Test
    void testToDtoPreservesIdWithOccupiedFalse() {
        SeatDto result = seatMapper.toDto(seat, false);
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void testToDtoPreservesRowNumberWithOccupiedFalse() {
        SeatDto result = seatMapper.toDto(seat, false);
        assertThat(result.getRowNumber()).isEqualTo(5);
    }

    @Test
    void testToDtoPreservesSeatNumberWithOccupiedFalse() {
        SeatDto result = seatMapper.toDto(seat, false);
        assertThat(result.getSeatNumber()).isEqualTo(12);
    }
}
