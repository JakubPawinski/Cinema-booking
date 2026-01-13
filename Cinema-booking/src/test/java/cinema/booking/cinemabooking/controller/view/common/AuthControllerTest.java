package cinema.booking.cinemabooking.controller.view.common;

import cinema.booking.cinemabooking.config.SecurityConfig;
import cinema.booking.cinemabooking.controller.view.GlobalControllerAdvice;
import cinema.booking.cinemabooking.dto.request.UserDto;
import cinema.booking.cinemabooking.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, GlobalControllerAdvice.class})
@DisplayName("View Tests for AuthController")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("Scenario 1: Login view - public access")
    void testLoginView() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"));
    }

    @Test
    @DisplayName("Scenario 2: Register view - public access")
    void testRegisterView() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @DisplayName("Scenario 3: Register user - success")
    void testRegisterUser_Success() throws Exception {
        doNothing().when(userService).register(any(UserDto.class));

        mockMvc.perform(post("/register")
                        .param("username", "testuser")
                        .param("password", "password123")
                        .param("email", "test@example.com")
                        .param("firstName", "Test")
                        .param("lastName", "User"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered=true"));

        verify(userService, times(1)).register(any(UserDto.class));
    }

    @Test
    @DisplayName("Scenario 4: Register user - validation error")
    void testRegisterUser_ValidationError() throws Exception {
        mockMvc.perform(post("/register")
                        .param("username", "")
                        .param("password", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"));

        verify(userService, never()).register(any(UserDto.class));
    }

    @Test
    @DisplayName("Scenario 5: Register user - user already exists")
    void testRegisterUser_UserAlreadyExists() throws Exception {
        doThrow(new RuntimeException("User already exists"))
                .when(userService).register(any(UserDto.class));

        mockMvc.perform(post("/register")
                        .param("username", "existinguser")
                        .param("password", "password123")
                        .param("email", "existing@example.com")
                        .param("firstName", "Existing")
                        .param("lastName", "User"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"));

        verify(userService, times(1)).register(any(UserDto.class));
    }
}
