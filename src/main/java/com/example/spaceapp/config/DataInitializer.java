package com.example.spaceapp.config;

import com.example.spaceapp.model.User;
import com.example.spaceapp.model.UserRole;
import com.example.spaceapp.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.spaceapp.repository.PlanetRepository;
import com.example.spaceapp.repository.MoonRepository;
import com.example.spaceapp.model.Planet;
import com.example.spaceapp.model.Moon;

@Component
public class DataInitializer implements ApplicationRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PlanetRepository planetRepository;
    private final MoonRepository moonRepository;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder, PlanetRepository planetRepository, MoonRepository moonRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.planetRepository = planetRepository;
        this.moonRepository = moonRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.count() == 0) {
            createUser("admin", "admin123", UserRole.ADMIN);
            createUser("staff", "staff123", UserRole.STAFF);
            createUser("student", "student123", UserRole.STUDENT);
        }
        if (planetRepository.count() == 0) {
            Planet earth = Planet.builder()
                    .name("Earth").type("Terrestrial")
                    .radiusKm(6371.0).massKg(5.972e24).orbitalPeriodDays(365.0)
                    .build();
            Planet jupiter = Planet.builder()
                    .name("Jupiter").type("Gas Giant")
                    .radiusKm(69911.0).massKg(1.898e27).orbitalPeriodDays(4332.59)
                    .build();
            earth = planetRepository.save(earth);
            jupiter = planetRepository.save(jupiter);

            Moon luna = Moon.builder()
                    .name("Moon").diameterKm(3474.8).orbitalPeriodDays(27.3)
                    .planet(earth)
                    .build();
            Moon io = Moon.builder()
                    .name("Io").diameterKm(3642.6).orbitalPeriodDays(1.77)
                    .planet(jupiter)
                    .build();
            Moon europa = Moon.builder()
                    .name("Europa").diameterKm(3121.6).orbitalPeriodDays(3.55)
                    .planet(jupiter)
                    .build();
            moonRepository.save(luna);
            moonRepository.save(io);
            moonRepository.save(europa);
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
