package cinema.booking.cinemabooking.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a user in the cinema booking system.
 */
@Entity
@Getter
@Setter
@ToString
@Table(name = "users")
public class User {

    /**
     * Unique identifier for the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Username of the user.
     */
    @NotBlank(message = "Username is required")
    @Column(nullable = false, unique = true)
    private String username;

    /**
     * Email of the user.
     */
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * First name of the user.
     */
    private String firstName;

    /**
     * Last name of the user.
     */
    private String lastName;

    /**
     * Password of the user.
     */
    @NotBlank(message = "Password is required")
    @Column(nullable = false)
    private String password;

    /**
     * Role of the user (e.g., USER, ADMIN).
     */
    private String role;

    /**
     * List of reservations made by the user.
     */
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @ToString.Exclude // Exclude reservations from toString to prevent circular dependencies
    private List<Reservation> reservations = new ArrayList<>();
}
