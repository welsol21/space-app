package com.example.spaceapp.config;

import com.example.spaceapp.repository.UserRepository;
import com.example.spaceapp.security.UserPrincipal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
/**
 * Configures HTTP Basic security, public endpoints for docs/H2/actuator,
 * role-based access for REST, and admin-only GraphQL. Enables method security.
 */
public class SecurityConfig {

    /**
     * HTTP security rules: permit docs/h2/actuator; REST read for ADMIN/STAFF/STUDENT,
     * REST write for ADMIN/STAFF; GraphQL restricted to ADMIN.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin())) // for H2 console
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**", "/h2-console/**", "/actuator/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/planets/**", "/api/moons/**").hasAnyRole("ADMIN", "STAFF", "STUDENT")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/planets/**", "/api/moons/**").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/planets/**", "/api/moons/**").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/planets/**", "/api/moons/**").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers(request -> {
                            String path = request.getRequestURI();
                            return path.startsWith("/graphql");
                        }).hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .httpBasic(basic -> {});
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> userRepository.findByUsername(username)
                .map(UserPrincipal::new)
                .orElseThrow(() -> new org.springframework.security.core.userdetails.UsernameNotFoundException("User not found: " + username));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }
}
