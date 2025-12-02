package com.example.universe.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable) // Disabled for simplicity in testing
            .authorizeHttpRequests(auth -> auth
                // Swagger UI
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                
                // H2 Console
                .requestMatchers("/h2-console/**").hasRole("ADMIN")
                
                // Public Read Access (STUDENT, STAFF, ADMIN)
                .requestMatchers(HttpMethod.GET, "/api/planets/**", "/api/moons/**").authenticated()
                
                // Modification Access (STAFF, ADMIN)
                .requestMatchers(HttpMethod.POST, "/api/planets/**", "/api/moons/**").hasAnyRole("STAFF", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/planets/**", "/api/moons/**").hasAnyRole("STAFF", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/planets/**", "/api/moons/**").hasAnyRole("STAFF", "ADMIN")
                
                // User Management & GraphQL (ADMIN only for user creation, logic handled in controller for query)
                .requestMatchers("/graphql").authenticated() 
                
                .anyRequest().authenticated()
            )
            .headers(headers -> headers.frameOptions(f -> f.disable())) // For H2 console
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
