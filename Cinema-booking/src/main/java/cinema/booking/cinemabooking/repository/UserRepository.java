package cinema.booking.cinemabooking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import cinema.booking.cinemabooking.model.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User entity
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by email
     * @param email the email of the user
     * @return an Optional containing the user if found, or empty if not found
     */
    Optional<User> findByEmail(String email);

    /** Find user by username
     * @param username the username of the user
     * @return an Optional containing the user if found, or empty if not found
     */
    Optional<User> findByUsername(String username);

    /**
     * Check if a user exists by username
     * @param username the username to check
     * @return true if a user with the specified username exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Check if a user exists by email
     * @param email the email to check
     * @return true if a user with the specified email exists, false otherwise
     */
    boolean existsByEmail(String email);
}
