package cinema.booking.cinemabooking.mapper;

import cinema.booking.cinemabooking.dto.request.UserDto;
import cinema.booking.cinemabooking.model.User;
import org.springframework.stereotype.Component;

/**
 * Mapper class for User entity and DTOs.
 */
@Component
public class UserMapper {

    /**
     * Converts UserDto to User entity.
     *
     * @param dto the UserDto
     * @return the User entity
     */
    public User toEntity(UserDto dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        // Password will be set separately after encoding
        return user;
    }
}
