package cinema.booking.cinemabooking.service;

import cinema.booking.cinemabooking.dto.request.UserDto;
import cinema.booking.cinemabooking.enums.UserRole;
import cinema.booking.cinemabooking.model.User;
import cinema.booking.cinemabooking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /*
     * Register a new user
     */
    @Transactional
    public void register(UserDto dto) {

        // Check if username already exists
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Create new User entity
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());

        // Encode and set password
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        // Set default role
        user.setRole(UserRole.USER.name());

        userRepository.save(user);
    }
}
