package com.example.universe.loader;

import com.example.universe.dto.CreateUserDTO;
import com.example.universe.entity.Moon;
import com.example.universe.entity.Planet;
import com.example.universe.entity.User;
import com.example.universe.repository.MoonRepository;
import com.example.universe.repository.PlanetRepository;
import com.example.universe.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final PlanetRepository planetRepo;
    private final MoonRepository moonRepo;
    private final UserService userService;

    @Override
    public void run(String... args) {
        // Users
        CreateUserDTO admin = new CreateUserDTO();
        admin.setUsername("admin");
        admin.setPassword("admin123");
        admin.setRole(User.Role.ADMIN);
        userService.createUser(admin);

        CreateUserDTO staff = new CreateUserDTO();
        staff.setUsername("staff");
        staff.setPassword("staff123");
        staff.setRole(User.Role.STAFF);
        userService.createUser(staff);
        
        CreateUserDTO student = new CreateUserDTO();
        student.setUsername("student");
        student.setPassword("student123");
        student.setRole(User.Role.STUDENT);
        userService.createUser(student);

        // Planets
        Planet earth = Planet.builder()
                .name("Earth").type("Terrestrial").massKg(5.972e24).radiusKm(6371.0).orbitalPeriodDays(365.25)
                .build();
        planetRepo.save(earth);

        Planet jupiter = Planet.builder()
                .name("Jupiter").type("Gas Giant").massKg(1.898e27).radiusKm(69911.0).orbitalPeriodDays(4333.0)
                .build();
        planetRepo.save(jupiter);

        // Moons
        moonRepo.save(Moon.builder().name("Moon").diameterKm(3474.8).orbitalPeriodDays(27.3).planet(earth).build());
        moonRepo.save(Moon.builder().name("Io").diameterKm(3642.0).orbitalPeriodDays(1.77).planet(jupiter).build());
        moonRepo.save(Moon.builder().name("Europa").diameterKm(3121.6).orbitalPeriodDays(3.55).planet(jupiter).build());
    }
}
