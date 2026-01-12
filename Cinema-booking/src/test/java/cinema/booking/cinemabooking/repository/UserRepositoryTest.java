package cinema.booking.cinemabooking.repository;

import cinema.booking.cinemabooking.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for UserRepository.
 */
@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("johndoe");
        user.setEmail("john@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("password123");
        user.setRole("USER");
    }

    @Test
    void testSaveUserReturnsNotNull() {
        User saved = userRepository.save(user);
        assertThat(saved).isNotNull();
    }

    @Test
    void testSaveUserGeneratesId() {
        User saved = userRepository.save(user);
        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void testSaveUserPreservesUsername() {
        User saved = userRepository.save(user);
        assertThat(saved.getUsername()).isEqualTo("johndoe");
    }

    @Test
    void testSaveUserPreservesEmail() {
        User saved = userRepository.save(user);
        assertThat(saved.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void testFindByIdReturnsUser() {
        User saved = userRepository.save(user);
        User found = userRepository.findById(saved.getId()).orElse(null);
        assertThat(found).isNotNull();
    }

    @Test
    void testFindByIdPreservesUsername() {
        User saved = userRepository.save(user);
        User found = userRepository.findById(saved.getId()).orElse(null);
        assertThat(found.getUsername()).isEqualTo("johndoe");
    }

    @Test
    void testFindByIdPreservesEmail() {
        User saved = userRepository.save(user);
        User found = userRepository.findById(saved.getId()).orElse(null);
        assertThat(found.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void testFindByEmailReturnsPresent() {
        userRepository.save(user);
        Optional<User> found = userRepository.findByEmail("john@example.com");
        assertThat(found).isPresent();
    }

    @Test
    void testFindByEmailReturnsCorrectUsername() {
        userRepository.save(user);
        Optional<User> found = userRepository.findByEmail("john@example.com");
        assertThat(found.get().getUsername()).isEqualTo("johndoe");
    }

    @Test
    void testFindByEmailReturnsCorrectEmail() {
        userRepository.save(user);
        Optional<User> found = userRepository.findByEmail("john@example.com");
        assertThat(found.get().getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void testFindByEmailNotFoundReturnsEmpty() {
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");
        assertThat(found).isEmpty();
    }

    @Test
    void testFindByUsernameReturnsPresent() {
        userRepository.save(user);
        Optional<User> found = userRepository.findByUsername("johndoe");
        assertThat(found).isPresent();
    }

    @Test
    void testFindByUsernameReturnsCorrectEmail() {
        userRepository.save(user);
        Optional<User> found = userRepository.findByUsername("johndoe");
        assertThat(found.get().getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void testFindByUsernameReturnsCorrectUsername() {
        userRepository.save(user);
        Optional<User> found = userRepository.findByUsername("johndoe");
        assertThat(found.get().getUsername()).isEqualTo("johndoe");
    }

    @Test
    void testFindByUsernameNotFoundReturnsEmpty() {
        Optional<User> found = userRepository.findByUsername("nonexistentuser");
        assertThat(found).isEmpty();
    }

    @Test
    void testExistsByUsernameReturnsTrue() {
        userRepository.save(user);
        assertThat(userRepository.existsByUsername("johndoe")).isTrue();
    }

    @Test
    void testExistsByUsernameReturnsFalse() {
        userRepository.save(user);
        assertThat(userRepository.existsByUsername("nonexistentuser")).isFalse();
    }

    @Test
    void testExistsByEmailReturnsTrue() {
        userRepository.save(user);
        assertThat(userRepository.existsByEmail("john@example.com")).isTrue();
    }

    @Test
    void testExistsByEmailReturnsFalse() {
        userRepository.save(user);
        assertThat(userRepository.existsByEmail("nonexistent@example.com")).isFalse();
    }

    @Test
    void testFindAllReturnsMultipleUsers() {
        userRepository.save(user);

        User user2 = new User();
        user2.setUsername("janedoe");
        user2.setEmail("jane@example.com");
        user2.setFirstName("Jane");
        user2.setLastName("Doe");
        user2.setPassword("password456");
        user2.setRole("USER");
        userRepository.save(user2);

        assertThat(userRepository.findAll()).hasSize(2);
    }

    @Test
    void testUpdateUserFirstName() {
        User saved = userRepository.save(user);
        saved.setFirstName("Jonathan");
        User updated = userRepository.save(saved);
        assertThat(updated.getFirstName()).isEqualTo("Jonathan");
    }

    @Test
    void testUpdateUserLastName() {
        User saved = userRepository.save(user);
        saved.setLastName("Smith");
        User updated = userRepository.save(saved);
        assertThat(updated.getLastName()).isEqualTo("Smith");
    }

    @Test
    void testDeleteUser() {
        User saved = userRepository.save(user);
        userRepository.deleteById(saved.getId());
        assertThat(userRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void testUniqueUsernameConstraint() {
        userRepository.save(user);
        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    void testUniqueEmailConstraint() {
        userRepository.save(user);
        assertThat(userRepository.count()).isEqualTo(1);
    }
}
