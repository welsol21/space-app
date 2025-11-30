package com.example.spaceapp.config;

import com.example.spaceapp.model.User;
import com.example.spaceapp.model.UserRole;
import com.example.spaceapp.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

@Component
public class DataInitializer implements ApplicationRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.count() == 0) {
            createUser("admin", "admin123", UserRole.ADMIN);
            createUser("staff", "staff123", UserRole.STAFF);
            createUser("student", "student123", UserRole.STUDENT);
        }
    }

    private void createUser(String username, String rawPassword, UserRole role) {
        if (userRepository.findByUsername(username).isPresent()) return;
        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(rawPassword))
                .role(role)
                .build();
        userRepository.save(user);
    }
}
