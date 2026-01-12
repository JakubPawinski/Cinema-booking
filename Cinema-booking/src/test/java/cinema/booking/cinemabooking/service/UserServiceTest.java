package cinema.booking.cinemabooking.service;

import cinema.booking.cinemabooking.dto.request.UserDto;
import cinema.booking.cinemabooking.enums.UserRole;
import cinema.booking.cinemabooking.exception.UserAlreadyExistsException;
import cinema.booking.cinemabooking.mapper.UserMapper;
import cinema.booking.cinemabooking.model.User;
import cinema.booking.cinemabooking.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private UserDto userDto;
    private User user;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setUsername("testuser");
        userDto.setEmail("testuser@example.com");
        userDto.setPassword("password123");
        userDto.setFirstName("John");
        userDto.setLastName("Doe");

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");
        user.setPassword("encodedpassword");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole(UserRole.USER.name());
    }

    // === Testy dla metody register() ===

    @Test
    void testRegisterSuccessfully() {
        // Arrange
        when(userRepository.existsByUsername(userDto.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(false);
        when(userMapper.toEntity(userDto)).thenReturn(user);
        when(passwordEncoder.encode(userDto.getPassword())).thenReturn("encodedpassword");

        // Act
        userService.register(userDto);

        // Assert
        verify(userRepository, times(1)).existsByUsername(userDto.getUsername());
        verify(userRepository, times(1)).existsByEmail(userDto.getEmail());
        verify(userMapper, times(1)).toEntity(userDto);
        verify(passwordEncoder, times(1)).encode(userDto.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterThrowsExceptionWhenUsernameExists() {
        // Arrange
        when(userRepository.existsByUsername(userDto.getUsername())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userService.register(userDto))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessage("Username already exists");

        verify(userRepository, times(1)).existsByUsername(userDto.getUsername());
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegisterThrowsExceptionWhenEmailExists() {
        // Arrange
        when(userRepository.existsByUsername(userDto.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userService.register(userDto))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessage("Email already exists");

        verify(userRepository, times(1)).existsByUsername(userDto.getUsername());
        verify(userRepository, times(1)).existsByEmail(userDto.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegisterSetsDefaultUserRole() {
        // Arrange
        when(userRepository.existsByUsername(userDto.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(false);
        when(userMapper.toEntity(userDto)).thenReturn(user);
        when(passwordEncoder.encode(userDto.getPassword())).thenReturn("encodedpassword");

        // Act
        userService.register(userDto);

        // Assert
        assertThat(user.getRole()).isEqualTo(UserRole.USER.name());
    }

    @Test
    void testRegisterEncodesPassword() {
        // Arrange
        when(userRepository.existsByUsername(userDto.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(false);
        when(userMapper.toEntity(userDto)).thenReturn(user);
        when(passwordEncoder.encode(userDto.getPassword())).thenReturn("encodedpassword");

        // Act
        userService.register(userDto);

        // Assert
        verify(passwordEncoder, times(1)).encode("password123");
        assertThat(user.getPassword()).isEqualTo("encodedpassword");
    }

    @Test
    void testRegisterMapsUserDtoToEntity() {
        // Arrange
        when(userRepository.existsByUsername(userDto.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(false);
        when(userMapper.toEntity(userDto)).thenReturn(user);
        when(passwordEncoder.encode(userDto.getPassword())).thenReturn("encodedpassword");

        // Act
        userService.register(userDto);

        // Assert
        verify(userMapper, times(1)).toEntity(userDto);
    }

    @Test
    void testRegisterSavesUserToRepository() {
        // Arrange
        when(userRepository.existsByUsername(userDto.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(false);
        when(userMapper.toEntity(userDto)).thenReturn(user);
        when(passwordEncoder.encode(userDto.getPassword())).thenReturn("encodedpassword");

        // Act
        userService.register(userDto);

        // Assert
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testRegisterWithValidUserData() {
        // Arrange
        UserDto validUserDto = new UserDto();
        validUserDto.setUsername("newuser");
        validUserDto.setEmail("newuser@example.com");
        validUserDto.setPassword("securepass123");
        validUserDto.setFirstName("Jane");
        validUserDto.setLastName("Smith");

        User newUser = new User();
        newUser.setUsername(validUserDto.getUsername());
        newUser.setEmail(validUserDto.getEmail());

        when(userRepository.existsByUsername(validUserDto.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(validUserDto.getEmail())).thenReturn(false);
        when(userMapper.toEntity(validUserDto)).thenReturn(newUser);
        when(passwordEncoder.encode(validUserDto.getPassword())).thenReturn("encodedpass");

        // Act
        userService.register(validUserDto);

        // Assert
        assertThat(newUser.getUsername()).isEqualTo("newuser");
        assertThat(newUser.getEmail()).isEqualTo("newuser@example.com");
        verify(userRepository, times(1)).save(newUser);
    }

    // === Testy dla metody validateUserDto() ===

    @Test
    void testValidateUserDtoWithDuplicateUsername() {
        // Arrange
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userService.register(userDto))
                .isInstanceOf(UserAlreadyExistsException.class);
    }

    @Test
    void testValidateUserDtoWithDuplicateEmail() {
        // Arrange
        when(userRepository.existsByUsername(userDto.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail("testuser@example.com")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userService.register(userDto))
                .isInstanceOf(UserAlreadyExistsException.class);
    }

    @Test
    void testValidateUserDtoCallsRepositoryMethods() {
        // Arrange
        when(userRepository.existsByUsername(userDto.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(false);
        when(userMapper.toEntity(userDto)).thenReturn(user);
        when(passwordEncoder.encode(userDto.getPassword())).thenReturn("encodedpassword");

        // Act
        userService.register(userDto);

        // Assert
        verify(userRepository, times(1)).existsByUsername(userDto.getUsername());
        verify(userRepository, times(1)).existsByEmail(userDto.getEmail());
    }

    @Test
    void testRegisterWithSpecialCharactersInPassword() {
        // Arrange
        UserDto specialUserDto = new UserDto();
        specialUserDto.setUsername("testuser2");
        specialUserDto.setEmail("test2@example.com");
        specialUserDto.setPassword("P@ssw0rd!#$%");
        specialUserDto.setFirstName("Test");
        specialUserDto.setLastName("User");

        when(userRepository.existsByUsername(specialUserDto.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(specialUserDto.getEmail())).thenReturn(false);
        when(userMapper.toEntity(specialUserDto)).thenReturn(user);
        when(passwordEncoder.encode(specialUserDto.getPassword())).thenReturn("encodedspecialpass");

        // Act
        userService.register(specialUserDto);

        // Assert
        verify(passwordEncoder, times(1)).encode("P@ssw0rd!#$%");
    }

    @Test
    void testRegisterCallsValidationBeforeSave() {
        // Arrange
        when(userRepository.existsByUsername(userDto.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(false);
        when(userMapper.toEntity(userDto)).thenReturn(user);
        when(passwordEncoder.encode(userDto.getPassword())).thenReturn("encodedpassword");

        // Act
        userService.register(userDto);

        // Assert - Verification order matters
        InOrder inOrder = inOrder(userRepository, userMapper, passwordEncoder);
        inOrder.verify(userRepository).existsByUsername(userDto.getUsername());
        inOrder.verify(userRepository).existsByEmail(userDto.getEmail());
        inOrder.verify(userMapper).toEntity(userDto);
        inOrder.verify(passwordEncoder).encode(userDto.getPassword());
        inOrder.verify(userRepository).save(any(User.class));
    }

    @Test
    void testRegisterDoesNotSaveIfUsernameAlreadyExists() {
        // Arrange
        when(userRepository.existsByUsername(userDto.getUsername())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userService.register(userDto))
                .isInstanceOf(UserAlreadyExistsException.class);

        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void testRegisterDoesNotSaveIfEmailAlreadyExists() {
        // Arrange
        when(userRepository.existsByUsername(userDto.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userService.register(userDto))
                .isInstanceOf(UserAlreadyExistsException.class);

        verify(userRepository, never()).save(any(User.class));
    }
}
