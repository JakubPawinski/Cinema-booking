package cinema.booking.cinemabooking.service;

import cinema.booking.cinemabooking.dto.request.UserDto;
import cinema.booking.cinemabooking.enums.UserRole;
import cinema.booking.cinemabooking.exception.UserAlreadyExistsException;
import cinema.booking.cinemabooking.mapper.UserMapper;
import cinema.booking.cinemabooking.model.User;
import cinema.booking.cinemabooking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Service for managing users.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    /**
     * Registers a new user.
     *
     * @param dto the UserDto containing user registration data
     */
    @Transactional
    public void register(UserDto dto) {
        log.info("Registering new user: {}", dto.getUsername());

        validateUserDto(dto);

        // Create new User entity
        User user = userMapper.toEntity(dto);

        // Encode and set password
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        // Set default role
        user.setRole(UserRole.USER.name());

        userRepository.save(user);
        log.info("User {} registered successfully", dto.getUsername());
    }

    /**
     * Validates the UserDto for uniqueness of username and email.
     *
     * @param dto the UserDto to validate
     */
    private void validateUserDto(UserDto dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            log.warn("Username {} already exists", dto.getUsername());
            throw new UserAlreadyExistsException("Username already exists");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            log.warn("Email {} already exists", dto.getEmail());
            throw new UserAlreadyExistsException("Email already exists");
        }
    }
}
