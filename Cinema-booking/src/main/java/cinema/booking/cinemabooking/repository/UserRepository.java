package cinema.booking.cinemabooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import cinema.booking.cinemabooking.model.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Find user by email
    Optional<User> findByEmail(String email);

    // Find user by username
    Optional<User> findByUsername(String username);

    // Check if a user exists by username
    boolean existsByUsername(String username);
}
