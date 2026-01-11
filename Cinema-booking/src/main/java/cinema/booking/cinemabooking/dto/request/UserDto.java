package cinema.booking.cinemabooking.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO representing a user for registration or update purposes.
 */
@Data
public class UserDto {

    /**
     * Username of the user.
     */
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    @Schema(description = "Username of the user", example = "john_doe")
    private String username;

    /**
     * Password of the user.
     */
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    @Schema(description = "Password of the user", example = "P@ssw0rd!")
    private String password;

    /**
     * Email of the user.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Schema(description = "Email of the user", example = "test@test.pl")
    private String email;

    /**
     * First name of the user.
     */
    @NotBlank(message = "First name is required")
    @Schema(description = "First name of the user", example = "John")
    private String firstName;

    /**
     * Last name of the user.
     */
    @NotBlank(message = "Last name is required")
    @Schema(description = "Last name of the user", example = "Doe")
    private String lastName;
}
