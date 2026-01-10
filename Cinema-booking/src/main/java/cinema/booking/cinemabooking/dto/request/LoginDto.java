package cinema.booking.cinemabooking.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO representing login request data.
 */
@Data
public class LoginDto {

    /**
     * The username of the user attempting to log in.
     */
    @NotBlank(message = "Username is required")
    private String username;

    /**
     * The password of the user attempting to log in.
     */
    @NotBlank(message = "Password is required")
    private String password;
}
