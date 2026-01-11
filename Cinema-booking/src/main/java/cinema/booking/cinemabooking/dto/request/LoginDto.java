package cinema.booking.cinemabooking.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "The username of the user attempting to log in", example = "john_doe")
    private String username;

    /**
     * The password of the user attempting to log in.
     */
    @NotBlank(message = "Password is required")
    @Schema(description = "The password of the user attempting to log in", example = "P@ssw0rd!")
    private String password;
}
