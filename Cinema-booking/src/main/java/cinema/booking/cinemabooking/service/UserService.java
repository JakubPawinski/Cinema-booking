package cinema.booking.cinemabooking.service;

import cinema.booking.cinemabooking.dto.RegisterDto;
import cinema.booking.cinemabooking.enums.UserRole;
import cinema.booking.cinemabooking.model.User;
import cinema.booking.cinemabooking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    /*
     * Register a new user
     */
    @Transactional
    public void register(RegisterDto dto) {

        // Check if username already exists
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        // Create new User entity
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());

        // For simplicity, storing password as plain text
        user.setPassword("{noop}" + dto.getPassword());

        // Set default role
        user.setRole(UserRole.USER.name());

        userRepository.save(user);
    }
}
