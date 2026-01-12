package cinema.booking.cinemabooking.mapper;

import cinema.booking.cinemabooking.dto.request.UserDto;
import cinema.booking.cinemabooking.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for UserMapper.
 */
public class UserMapperTest {

    private UserMapper userMapper;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();

        userDto = new UserDto();
        userDto.setUsername("johndoe");
        userDto.setEmail("john@example.com");
        userDto.setFirstName("John");
        userDto.setLastName("Doe");
    }

    @Test
    void testToEntityReturnsNotNull() {
        User result = userMapper.toEntity(userDto);
        assertThat(result).isNotNull();
    }

    @Test
    void testToEntityPreservesUsername() {
        User result = userMapper.toEntity(userDto);
        assertThat(result.getUsername()).isEqualTo("johndoe");
    }

    @Test
    void testToEntityPreservesEmail() {
        User result = userMapper.toEntity(userDto);
        assertThat(result.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void testToEntityPreservesFirstName() {
        User result = userMapper.toEntity(userDto);
        assertThat(result.getFirstName()).isEqualTo("John");
    }

    @Test
    void testToEntityPreservesLastName() {
        User result = userMapper.toEntity(userDto);
        assertThat(result.getLastName()).isEqualTo("Doe");
    }

    @Test
    void testToEntityPasswordIsNull() {
        User result = userMapper.toEntity(userDto);
        assertThat(result.getPassword()).isNull();
    }

    @Test
    void testToEntityIdIsNull() {
        User result = userMapper.toEntity(userDto);
        assertThat(result.getId()).isNull();
    }

    @Test
    void testToEntityRoleIsNull() {
        User result = userMapper.toEntity(userDto);
        assertThat(result.getRole()).isNull();
    }

    @Test
    void testToEntityWithDifferentUsername() {
        userDto.setUsername("janedoe");
        User result = userMapper.toEntity(userDto);
        assertThat(result.getUsername()).isEqualTo("janedoe");
    }

    @Test
    void testToEntityWithDifferentEmail() {
        userDto.setEmail("jane@example.com");
        User result = userMapper.toEntity(userDto);
        assertThat(result.getEmail()).isEqualTo("jane@example.com");
    }

    @Test
    void testToEntityWithDifferentFirstName() {
        userDto.setFirstName("Jane");
        User result = userMapper.toEntity(userDto);
        assertThat(result.getFirstName()).isEqualTo("Jane");
    }

    @Test
    void testToEntityWithDifferentLastName() {
        userDto.setLastName("Smith");
        User result = userMapper.toEntity(userDto);
        assertThat(result.getLastName()).isEqualTo("Smith");
    }

    @Test
    void testToEntityWithEmptyUsername() {
        userDto.setUsername("");
        User result = userMapper.toEntity(userDto);
        assertThat(result.getUsername()).isEmpty();
    }

    @Test
    void testToEntityWithEmptyEmail() {
        userDto.setEmail("");
        User result = userMapper.toEntity(userDto);
        assertThat(result.getEmail()).isEmpty();
    }

    @Test
    void testToEntityWithEmptyFirstName() {
        userDto.setFirstName("");
        User result = userMapper.toEntity(userDto);
        assertThat(result.getFirstName()).isEmpty();
    }

    @Test
    void testToEntityWithEmptyLastName() {
        userDto.setLastName("");
        User result = userMapper.toEntity(userDto);
        assertThat(result.getLastName()).isEmpty();
    }

    @Test
    void testToEntityReservationsListIsEmpty() {
        User result = userMapper.toEntity(userDto);
        assertThat(result.getReservations()).isEmpty();
    }

    @Test
    void testToEntityReservationsListIsNotNull() {
        User result = userMapper.toEntity(userDto);
        assertThat(result.getReservations()).isNotNull();
    }

    @Test
    void testToEntityMultipleCallsCreateDifferentInstances() {
        User user1 = userMapper.toEntity(userDto);
        User user2 = userMapper.toEntity(userDto);
        assertThat(user1).isNotSameAs(user2);
    }

    @Test
    void testToEntityWithNullFirstName() {
        userDto.setFirstName(null);
        User result = userMapper.toEntity(userDto);
        assertThat(result.getFirstName()).isNull();
    }

    @Test
    void testToEntityWithNullLastName() {
        userDto.setLastName(null);
        User result = userMapper.toEntity(userDto);
        assertThat(result.getLastName()).isNull();
    }

    @Test
    void testToEntityWithSpecialCharactersInUsername() {
        userDto.setUsername("john_doe-123");
        User result = userMapper.toEntity(userDto);
        assertThat(result.getUsername()).isEqualTo("john_doe-123");
    }

    @Test
    void testToEntityWithLongUsername() {
        userDto.setUsername("verylongusernamethatisallowedinthesystem");
        User result = userMapper.toEntity(userDto);
        assertThat(result.getUsername()).isEqualTo("verylongusernamethatisallowedinthesystem");
    }

    @Test
    void testToEntityWithComplexEmail() {
        userDto.setEmail("john.doe+test@example.co.uk");
        User result = userMapper.toEntity(userDto);
        assertThat(result.getEmail()).isEqualTo("john.doe+test@example.co.uk");
    }

    @Test
    void testToEntityWithWhitespaceInFirstName() {
        userDto.setFirstName("Jean-Pierre");
        User result = userMapper.toEntity(userDto);
        assertThat(result.getFirstName()).isEqualTo("Jean-Pierre");
    }

    @Test
    void testToEntityWithWhitespaceInLastName() {
        userDto.setLastName("van der Berg");
        User result = userMapper.toEntity(userDto);
        assertThat(result.getLastName()).isEqualTo("van der Berg");
    }
}
