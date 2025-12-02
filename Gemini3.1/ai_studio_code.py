import os

project_name = "universe-api"
base_package = "com/example/universe"
base_path = f"{project_name}/src/main/java/{base_package}"
resources_path = f"{project_name}/src/main/resources"

file_structure = {
    # ==========================================
    # Build Configuration (Maven)
    # ==========================================
    f"{project_name}/pom.xml": """<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" 
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.3.0</version> <!-- Using stable 3.3.x as 3.5 is futuristic -->
        <relativePath/>
    </parent>
    <groupId>com.example</groupId>
    <artifactId>universe-api</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>universe-api</name>
    <description>Universe REST and GraphQL API</description>
    <properties>
        <java.version>17</java.version>
    </properties>
    <dependencies>
        <!-- Web & REST -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        
        <!-- Data -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>

        <!-- Security -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>

        <!-- GraphQL -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-graphql</artifactId>
        </dependency>

        <!-- AOP -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-aop</artifactId>
        </dependency>

        <!-- Documentation -->
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
            <version>2.5.0</version>
        </dependency>

        <!-- Utilities -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- Testing -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.graphql</groupId>
            <artifactId>spring-graphql-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
""",

    # ==========================================
    # Main Application
    # ==========================================
    f"{base_path}/UniverseApplication.java": """package com.example.universe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UniverseApplication {
    public static void main(String[] args) {
        SpringApplication.run(UniverseApplication.class, args);
    }
}
""",

    # ==========================================
    # Configuration
    # ==========================================
    f"{base_path}/config/OpenApiConfig.java": """package com.example.universe.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI universeOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Universe API")
                .description("Spring Boot REST & GraphQL API for Planets and Moons")
                .version("1.0"));
    }
}
""",

    f"{base_path}/config/SecurityConfig.java": """package com.example.universe.config;

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
""",

    # ==========================================
    # AOP Logging
    # ==========================================
    f"{base_path}/aop/LoggingAspect.java": """package com.example.universe.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    // Pointcut for Controllers
    @Pointcut("execution(* com.example.universe.controller..*(..))")
    public void controllerPointcut() {}

    // Pointcut for Services
    @Pointcut("execution(* com.example.universe.service..*(..))")
    public void servicePointcut() {}

    @Before("controllerPointcut() || servicePointcut()")
    public void logBefore(JoinPoint joinPoint) {
        log.info("Enter: {}.{}() with argument[s] = {}", 
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), 
                Arrays.toString(joinPoint.getArgs()));
    }

    @AfterThrowing(pointcut = "controllerPointcut() || servicePointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        log.error("Exception in {}.{}() with cause = {}", 
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), 
                e.getCause() != null ? e.getCause() : "NULL");
    }
}
""",

    # ==========================================
    # Entities
    # ==========================================
    f"{base_path}/entity/Planet.java": """package com.example.universe.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "planets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Planet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type; // e.g., Terrestrial, Gas Giant
    private Double radiusKm;
    private Double massKg;
    private Double orbitalPeriodDays;

    @OneToMany(mappedBy = "planet", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @Builder.Default
    private List<Moon> moons = new ArrayList<>();
}
""",

    f"{base_path}/entity/Moon.java": """package com.example.universe.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "moons")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Moon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Double diameterKm;
    private Double orbitalPeriodDays;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "planet_id")
    @ToString.Exclude
    private Planet planet;
}
""",

    f"{base_path}/entity/User.java": """package com.example.universe.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    public enum Role {
        ADMIN, STAFF, STUDENT
    }
}
""",

    # ==========================================
    # DTOs
    # ==========================================
    f"{base_path}/dto/PlanetDTO.java": """package com.example.universe.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlanetDTO {
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Type is required")
    private String type;

    @NotNull
    @Positive
    private Double radiusKm;

    @NotNull
    @Positive
    private Double massKg;

    @NotNull
    @Positive
    private Double orbitalPeriodDays;
}
""",

    f"{base_path}/dto/MoonDTO.java": """package com.example.universe.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MoonDTO {
    private Long id;

    @NotBlank
    private String name;

    @NotNull
    @Positive
    private Double diameterKm;

    @NotNull
    @Positive
    private Double orbitalPeriodDays;

    @NotNull(message = "Planet ID is required")
    private Long planetId;
}
""",

    f"{base_path}/dto/UserDTO.java": """package com.example.universe.dto;

import com.example.universe.entity.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO {
    private Long id;
    private String username;
    private User.Role role;
}
""",

    f"{base_path}/dto/CreateUserDTO.java": """package com.example.universe.dto;

import com.example.universe.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateUserDTO {
    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank
    @Size(min = 4)
    private String password;

    @NotNull
    private User.Role role;
}
""",

    # ==========================================
    # Repositories
    # ==========================================
    f"{base_path}/repository/PlanetRepository.java": """package com.example.universe.repository;

import com.example.universe.entity.Planet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlanetRepository extends JpaRepository<Planet, Long> {
    List<Planet> findByTypeIgnoreCase(String type);
}
""",

    f"{base_path}/repository/MoonRepository.java": """package com.example.universe.repository;

import com.example.universe.entity.Moon;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MoonRepository extends JpaRepository<Moon, Long> {
    List<Moon> findByPlanetNameIgnoreCase(String planetName);
    long countByPlanetId(Long planetId);
}
""",

    f"{base_path}/repository/UserRepository.java": """package com.example.universe.repository;

import com.example.universe.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
""",

    # ==========================================
    # Exceptions
    # ==========================================
    f"{base_path}/exception/ResourceNotFoundException.java": """package com.example.universe.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
""",

    f"{base_path}/exception/GlobalExceptionHandler.java": """package com.example.universe.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(ResourceNotFoundException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Not Found");
        error.put("message", ex.getMessage());
        return error;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage()));
        return errors;
    }
    
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleGeneral(Exception ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Internal Server Error");
        error.put("message", ex.getMessage());
        return error;
    }
}
""",

    # ==========================================
    # Services
    # ==========================================
    f"{base_path}/service/CustomUserDetailsService.java": """package com.example.universe.service;

import com.example.universe.entity.User;
import com.example.universe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}
""",

    f"{base_path}/service/PlanetService.java": """package com.example.universe.service;

import com.example.universe.dto.PlanetDTO;
import com.example.universe.entity.Planet;
import com.example.universe.exception.ResourceNotFoundException;
import com.example.universe.repository.PlanetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlanetService {
    private final PlanetRepository repository;

    public List<PlanetDTO> findAll() {
        return repository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }
    
    public PlanetDTO findById(Long id) {
        return repository.findById(id).map(this::toDTO)
            .orElseThrow(() -> new ResourceNotFoundException("Planet not found: " + id));
    }
    
    public List<PlanetDTO> findByType(String type) {
        return repository.findByTypeIgnoreCase(type).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public PlanetDTO create(PlanetDTO dto) {
        Planet planet = toEntity(dto);
        return toDTO(repository.save(planet));
    }

    public PlanetDTO update(Long id, PlanetDTO dto) {
        Planet planet = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Planet not found: " + id));
        planet.setName(dto.getName());
        planet.setType(dto.getType());
        planet.setMassKg(dto.getMassKg());
        planet.setRadiusKm(dto.getRadiusKm());
        planet.setOrbitalPeriodDays(dto.getOrbitalPeriodDays());
        return toDTO(repository.save(planet));
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Planet not found: " + id);
        }
        repository.deleteById(id);
    }

    private PlanetDTO toDTO(Planet p) {
        return PlanetDTO.builder()
                .id(p.getId())
                .name(p.getName())
                .type(p.getType())
                .massKg(p.getMassKg())
                .radiusKm(p.getRadiusKm())
                .orbitalPeriodDays(p.getOrbitalPeriodDays())
                .build();
    }

    private Planet toEntity(PlanetDTO dto) {
        return Planet.builder()
                .name(dto.getName())
                .type(dto.getType())
                .massKg(dto.getMassKg())
                .radiusKm(dto.getRadiusKm())
                .orbitalPeriodDays(dto.getOrbitalPeriodDays())
                .build();
    }
}
""",

    f"{base_path}/service/MoonService.java": """package com.example.universe.service;

import com.example.universe.dto.MoonDTO;
import com.example.universe.entity.Moon;
import com.example.universe.entity.Planet;
import com.example.universe.exception.ResourceNotFoundException;
import com.example.universe.repository.MoonRepository;
import com.example.universe.repository.PlanetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MoonService {
    private final MoonRepository moonRepository;
    private final PlanetRepository planetRepository;

    public List<MoonDTO> findAll() {
        return moonRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public MoonDTO findById(Long id) {
        return moonRepository.findById(id).map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Moon not found: " + id));
    }
    
    public List<MoonDTO> findByPlanetName(String planetName) {
        return moonRepository.findByPlanetNameIgnoreCase(planetName).stream()
                .map(this::toDTO).collect(Collectors.toList());
    }
    
    public long countByPlanetId(Long planetId) {
        if (!planetRepository.existsById(planetId)) {
            throw new ResourceNotFoundException("Planet not found: " + planetId);
        }
        return moonRepository.countByPlanetId(planetId);
    }

    public MoonDTO create(MoonDTO dto) {
        Planet planet = planetRepository.findById(dto.getPlanetId())
                .orElseThrow(() -> new ResourceNotFoundException("Planet not found: " + dto.getPlanetId()));
        
        Moon moon = Moon.builder()
                .name(dto.getName())
                .diameterKm(dto.getDiameterKm())
                .orbitalPeriodDays(dto.getOrbitalPeriodDays())
                .planet(planet)
                .build();
        
        return toDTO(moonRepository.save(moon));
    }
    
    public void delete(Long id) {
        if (!moonRepository.existsById(id)) {
            throw new ResourceNotFoundException("Moon not found: " + id);
        }
        moonRepository.deleteById(id);
    }

    private MoonDTO toDTO(Moon m) {
        return MoonDTO.builder()
                .id(m.getId())
                .name(m.getName())
                .diameterKm(m.getDiameterKm())
                .orbitalPeriodDays(m.getOrbitalPeriodDays())
                .planetId(m.getPlanet().getId())
                .build();
    }
}
""",

    f"{base_path}/service/UserService.java": """package com.example.universe.service;

import com.example.universe.dto.CreateUserDTO;
import com.example.universe.dto.UserDTO;
import com.example.universe.entity.User;
import com.example.universe.exception.ResourceNotFoundException;
import com.example.universe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserDTO findById(Long id) {
        return repository.findById(id)
                .map(u -> UserDTO.builder()
                        .id(u.getId())
                        .username(u.getUsername())
                        .role(u.getRole())
                        .build())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public UserDTO createUser(CreateUserDTO input) {
        User user = User.builder()
                .username(input.getUsername())
                .password(passwordEncoder.encode(input.getPassword()))
                .role(input.getRole())
                .build();
        
        User saved = repository.save(user);
        return UserDTO.builder()
                .id(saved.getId())
                .username(saved.getUsername())
                .role(saved.getRole())
                .build();
    }
}
""",

    # ==========================================
    # Controllers (REST)
    # ==========================================
    f"{base_path}/controller/PlanetController.java": """package com.example.universe.controller;

import com.example.universe.dto.PlanetDTO;
import com.example.universe.service.PlanetService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/planets")
@RequiredArgsConstructor
public class PlanetController {

    private final PlanetService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all planets or filter by type")
    public List<PlanetDTO> getPlanets(@RequestParam(required = false) String type) {
        if (type != null && !type.isEmpty()) {
            return service.findByType(type);
        }
        return service.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PlanetDTO getPlanet(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PlanetDTO createPlanet(@Valid @RequestBody PlanetDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PlanetDTO updatePlanet(@PathVariable Long id, @Valid @RequestBody PlanetDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePlanet(@PathVariable Long id) {
        service.delete(id);
    }
}
""",

    f"{base_path}/controller/MoonController.java": """package com.example.universe.controller;

import com.example.universe.dto.MoonDTO;
import com.example.universe.service.MoonService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/moons")
@RequiredArgsConstructor
public class MoonController {

    private final MoonService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all moons or filter by planet name")
    public List<MoonDTO> getMoons(@RequestParam(required = false) String planetName) {
        if (planetName != null && !planetName.isEmpty()) {
            return service.findByPlanetName(planetName);
        }
        return service.findAll();
    }
    
    @GetMapping("/count")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Count moons for a specific planet")
    public Long countMoons(@RequestParam Long planetId) {
        return service.countByPlanetId(planetId);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MoonDTO getMoon(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MoonDTO createMoon(@Valid @RequestBody MoonDTO dto) {
        return service.create(dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMoon(@PathVariable Long id) {
        service.delete(id);
    }
}
""",

    # ==========================================
    # Controllers (GraphQL)
    # ==========================================
    f"{base_path}/controller/AuthGraphQLController.java": """package com.example.universe.controller;

import com.example.universe.dto.CreateUserDTO;
import com.example.universe.dto.UserDTO;
import com.example.universe.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class AuthGraphQLController {

    private final UserService userService;

    @QueryMapping
    public UserDTO userById(@Argument Long id) {
        return userService.findById(id);
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public UserDTO createUser(@Argument @Valid CreateUserDTO input) {
        return userService.createUser(input);
    }
}
""",

    # ==========================================
    # Data Loader
    # ==========================================
    f"{base_path}/loader/DataLoader.java": """package com.example.universe.loader;

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
""",

    # ==========================================
    # Resources
    # ==========================================
    f"{resources_path}/application.properties": """spring.application.name=universe-api
spring.datasource.url=jdbc:h2:mem:universe_db
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true
spring.jpa.hibernate.ddl-auto=create-drop

spring.graphql.graphiql.enabled=true

logging.level.com.example.universe=INFO
""",

    f"{resources_path}/graphql/schema.graphqls": """type Query {
    userById(id: ID): UserDto
}

type Mutation {
    createUser(input: CreateUserInput!): UserDto
}

type UserDto {
    id: ID
    username: String
    role: Role
}

input CreateUserInput {
    username: String!
    password: String!
    role: Role!
}

enum Role {
    ADMIN
    STAFF
    STUDENT
}
"""
}

def create_project():
    for file_path, content in file_structure.items():
        directory = os.path.dirname(file_path)
        if not os.path.exists(directory):
            os.makedirs(directory)
        
        with open(file_path, "w") as f:
            f.write(content)
        print(f"Created: {file_path}")
    
    print("\\nProject generated successfully!")
    print(f"1. cd {project_name}")
    print("2. mvn clean install")
    print("3. mvn spring-boot:run")
    print("\\nAccess points:")
    print("- Swagger UI: http://localhost:8080/swagger-ui.html")
    print("- H2 Console: http://localhost:8080/h2-console (jdbc:h2:mem:universe_db)")
    print("- GraphiQL: http://localhost:8080/graphiql")
    print("\\nCredentials (Preloaded):")
    print("- admin / admin123")
    print("- staff / staff123")
    print("- student / student123")

if __name__ == "__main__":
    create_project()