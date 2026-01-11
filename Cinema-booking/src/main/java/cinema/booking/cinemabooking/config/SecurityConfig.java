package cinema.booking.cinemabooking.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpMethod;

/**
 * Configuration class for Spring Security
 * Defines security roles, authentication and authorization
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    /**
     * Bean for password encoding
     * @return PasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Bean for configuration the security filter chain
     * @param http HttpSecurity object
     * @return SecurityFilterChain security filter chain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        // Static resources
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/uploads/**").permitAll()

                        // Swagger documentation
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        // Public views
                        .requestMatchers("/", "/movies/**", "/register", "/login").permitAll()

                        // API endpoints
                        .requestMatchers("/api/v1/movies/**").permitAll()
                        .requestMatchers("/api/v1/seances/**").permitAll()
                        .requestMatchers("/api/v1/repertoires/**").permitAll()

                        // Admin
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/reports/**").hasRole("ADMIN")

                        // Movie and Seance management (admin only)
                        .requestMatchers(HttpMethod.POST, "/api/v1/movies/**", "/api/v1/seances/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/movies/**", "/api/v1/seances/**").hasRole("ADMIN")

                        // Authenticated users
                        .requestMatchers("/api/v1/reservations/**").authenticated()
                        .requestMatchers("/profile/**", "/booking/**").authenticated()

                        .anyRequest().authenticated()
                )

                // Form login configuration
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )

                // Logout configuration
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                );

        return http.build();
    }
}
