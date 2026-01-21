package cinema.booking.cinemabooking.config;

import cinema.booking.cinemabooking.model.User;
import cinema.booking.cinemabooking.repository.UserRepository;
import cinema.booking.cinemabooking.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


/**
 * Service responsible for loading user data from the database for Spring Security.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    /**
     *
     * Method finds a user in the database by username and returns their data in Spring Security format
     *
     * @param username the username to search for
     * @return UserDetails - object with user data
     * @throws UsernameNotFoundException - if user does not exists
     */
    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        User user = userService.findByUsername(username);

        // Convert User model to spring security object
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole())
                .build();
    }
}