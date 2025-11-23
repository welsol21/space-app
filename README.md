\# Space App — Spring Boot 3.5 (REST + GraphQL)



This is a training project built with \*\*Spring Boot 3.5.8 / Java 17 / Maven\*\*.  

It exposes both \*\*REST APIs\*\* and a \*\*GraphQL endpoint\*\* for managing planets, moons, and users with role-based security.



The README documents the \*\*project structure down to class and method signatures\*\*.  

For entities and DTOs we assume the use of \*\*Lombok\*\* (no manual getters/setters).



---



\## Tech Stack



\- \*\*Language:\*\* Java 17  

\- \*\*Framework:\*\* Spring Boot 3.5.8

&nbsp; - `spring-boot-starter-web`

&nbsp; - `spring-boot-starter-data-jpa`

&nbsp; - `spring-boot-starter-security`

&nbsp; - `spring-boot-starter-validation`

&nbsp; - `spring-boot-starter-graphql`

&nbsp; - `spring-boot-starter-aop`

\- \*\*Persistence:\*\* Spring Data JPA (Hibernate)

\- \*\*Database:\*\* H2 in-memory

\- \*\*Security:\*\* Spring Security (Basic Auth + role-based access control)

\- \*\*Logging:\*\* Spring AOP + AspectJ (logging aspect)

\- \*\*API Docs:\*\* Swagger / OpenAPI (`springdoc-openapi-starter-webmvc-ui`)

\- \*\*Code generation:\*\* Lombok (for entities/DTOs)

\- \*\*Build:\*\* Maven



---



\## How to Run



From the project root:



```bash

mvn clean install

mvn spring-boot:run

```



Application endpoints:



\- REST base URL: `http://localhost:8080`

\- Swagger UI: `http://localhost:8080/swagger-ui.html`

\- H2 Console: `http://localhost:8080/h2-console`

\- GraphQL endpoint: `http://localhost:8080/graphql` (GraphiQL UI is enabled)



\### H2 Console



\- JDBC URL: `jdbc:h2:mem:space-db`

\- Username: `sa`

\- Password: \*(empty)\*



---



\## Preloaded Users (Basic Auth)



On startup, `DataInitializer` creates three users (passwords are stored as BCrypt hashes):



| Username | Raw Password | Role    |

|----------|--------------|---------|

| admin    | admin123     | ADMIN   |

| staff    | staff123     | STAFF   |

| student  | student123   | STUDENT |



Role permissions:



\- `ADMIN` – full access, including user management via GraphQL

\- `STAFF` – create/update/delete planets and moons

\- `STUDENT` – read-only access to planets and moons



---



\## High-Level Architecture



Main package structure:



```text

src/main/java/com/example/spaceapp

├── SpaceAppApplication.java

├── config

│   ├── DataInitializer.java

│   └── SecurityConfig.java

├── controller

│   ├── MoonController.java

│   ├── PlanetController.java

│   └── UserGraphqlController.java

├── dto

│   ├── ErrorResponse.java

│   ├── MoonCreateUpdateDto.java

│   ├── MoonDto.java

│   ├── PlanetCreateUpdateDto.java

│   ├── PlanetDto.java

│   ├── UserCreateDto.java

│   └── UserDto.java

├── exception

│   ├── BadRequestException.java

│   ├── GlobalExceptionHandler.java

│   └── ResourceNotFoundException.java

├── logging

│   └── LoggingAspect.java

├── model

│   ├── Moon.java

│   ├── Planet.java

│   ├── User.java

│   └── UserRole.java

├── repository

│   ├── MoonRepository.java

│   ├── PlanetRepository.java

│   └── UserRepository.java

├── security

│   └── UserPrincipal.java

└── service

&nbsp;   ├── MoonService.java

&nbsp;   ├── PlanetService.java

&nbsp;   └── UserService.java

```



Resources:



```text

src/main/resources

├── application.properties

├── data.sql

└── graphql

&nbsp;   └── user.graphqls

```



---



\## Classes and Method Signatures



\### 1. Main Application Class



\#### `com.example.spaceapp.SpaceAppApplication`



```java

@SpringBootApplication

public class SpaceAppApplication {



&nbsp;   public static void main(String\[] args);

}

```



---



\### 2. Configuration



\#### `com.example.spaceapp.config.SecurityConfig`



```java

@Configuration

@EnableMethodSecurity

public class SecurityConfig {



&nbsp;   @Bean

&nbsp;   public SecurityFilterChain filterChain(HttpSecurity http) throws Exception;



&nbsp;   @Bean

&nbsp;   public UserDetailsService userDetailsService(UserRepository userRepository);



&nbsp;   @Bean

&nbsp;   public PasswordEncoder passwordEncoder();



&nbsp;   @Bean

&nbsp;   public DaoAuthenticationProvider daoAuthenticationProvider(

&nbsp;           UserDetailsService userDetailsService,

&nbsp;           PasswordEncoder passwordEncoder

&nbsp;   );

}

```



Security rules inside `filterChain(HttpSecurity)` (conceptually):



\- Permit all:

&nbsp; - `/v3/api-docs/\*\*`

&nbsp; - `/swagger-ui.html`

&nbsp; - `/swagger-ui/\*\*`

&nbsp; - `/h2-console/\*\*`

\- Require roles: `ADMIN`, `STAFF`, `STUDENT` for:

&nbsp; - `GET /api/planets/\*\*`

&nbsp; - `GET /api/moons/\*\*`

\- Require roles: `ADMIN`, `STAFF` for:

&nbsp; - non-GET methods to `/api/planets/\*\*`, `/api/moons/\*\*`

\- Require role: `ADMIN` for:

&nbsp; - `/graphql/\*\*`

\- All other endpoints: authenticated.



\#### `com.example.spaceapp.config.DataInitializer`



```java

@Component

public class DataInitializer implements ApplicationRunner {



&nbsp;   public DataInitializer(UserRepository userRepository,

&nbsp;                          PasswordEncoder passwordEncoder);



&nbsp;   @Override

&nbsp;   public void run(ApplicationArguments args);



&nbsp;   // helper

&nbsp;   private void createUser(String username, String rawPassword, UserRole role);

}

```



On startup, checks if `app\_user` is empty and creates `admin`, `staff`, `student`.



---



\### 3. Domain Model (Entities)



> Entities use Lombok to avoid manual getters/setters.



\#### `com.example.spaceapp.model.Planet`



```java

@Entity

@Table(name = "planet")

@Data

@NoArgsConstructor

@AllArgsConstructor

@Builder

public class Planet {



&nbsp;   @Id

&nbsp;   @GeneratedValue(strategy = GenerationType.IDENTITY)

&nbsp;   private Long id;



&nbsp;   @Column(nullable = false, unique = true)

&nbsp;   private String name;



&nbsp;   @Column(nullable = false)

&nbsp;   private String type;



&nbsp;   @Column(name = "radius\_km", nullable = false)

&nbsp;   private Double radiusKm;



&nbsp;   @Column(name = "mass\_kg", nullable = false)

&nbsp;   private Double massKg;



&nbsp;   @Column(name = "orbital\_period\_days", nullable = false)

&nbsp;   private Double orbitalPeriodDays;



&nbsp;   @OneToMany(mappedBy = "planet", cascade = CascadeType.ALL, orphanRemoval = true)

&nbsp;   @Builder.Default

&nbsp;   private List<Moon> moons = new ArrayList<>();

}

```



\#### `com.example.spaceapp.model.Moon`



```java

@Entity

@Table(name = "moon")

@Data

@NoArgsConstructor

@AllArgsConstructor

@Builder

public class Moon {



&nbsp;   @Id

&nbsp;   @GeneratedValue(strategy = GenerationType.IDENTITY)

&nbsp;   private Long id;



&nbsp;   @Column(nullable = false)

&nbsp;   private String name;



&nbsp;   @Column(name = "diameter\_km", nullable = false)

&nbsp;   private Double diameterKm;



&nbsp;   @Column(name = "orbital\_period\_days", nullable = false)

&nbsp;   private Double orbitalPeriodDays;



&nbsp;   @ManyToOne(fetch = FetchType.LAZY)

&nbsp;   @JoinColumn(name = "planet\_id", nullable = false)

&nbsp;   private Planet planet;

}

```



\#### `com.example.spaceapp.model.UserRole`



```java

public enum UserRole {

&nbsp;   ADMIN,

&nbsp;   STAFF,

&nbsp;   STUDENT

}

```



\#### `com.example.spaceapp.model.User`



```java

@Entity

@Table(name = "app\_user")

@Data

@NoArgsConstructor

@AllArgsConstructor

@Builder

public class User {



&nbsp;   @Id

&nbsp;   @GeneratedValue(strategy = GenerationType.IDENTITY)

&nbsp;   private Long id;



&nbsp;   @Column(nullable = false, unique = true)

&nbsp;   private String username;



&nbsp;   @Column(nullable = false)

&nbsp;   private String password; // BCrypt hash



&nbsp;   @Enumerated(EnumType.STRING)

&nbsp;   @Column(nullable = false)

&nbsp;   private UserRole role;

}

```



---



\### 4. DTOs (Data Transfer Objects)



> DTOs also use Lombok and Jakarta Validation. Only fields and annotations are listed here.



\#### `com.example.spaceapp.dto.PlanetDto`



```java

@Data

@NoArgsConstructor

@AllArgsConstructor

@Builder

public class PlanetDto {



&nbsp;   private Long id;



&nbsp;   @NotNull

&nbsp;   @Size(min = 2, max = 100)

&nbsp;   private String name;



&nbsp;   @NotNull

&nbsp;   @Size(min = 3, max = 50)

&nbsp;   private String type;



&nbsp;   @NotNull

&nbsp;   @Positive

&nbsp;   private Double radiusKm;



&nbsp;   @NotNull

&nbsp;   @Positive

&nbsp;   private Double massKg;



&nbsp;   @NotNull

&nbsp;   @Positive

&nbsp;   private Double orbitalPeriodDays;

}

```



\#### `com.example.spaceapp.dto.PlanetCreateUpdateDto`



```java

@Data

@NoArgsConstructor

@AllArgsConstructor

@Builder

public class PlanetCreateUpdateDto {



&nbsp;   @NotNull

&nbsp;   @Size(min = 2, max = 100)

&nbsp;   private String name;



&nbsp;   @NotNull

&nbsp;   @Size(min = 3, max = 50)

&nbsp;   private String type;



&nbsp;   @NotNull

&nbsp;   @Positive

&nbsp;   private Double radiusKm;



&nbsp;   @NotNull

&nbsp;   @Positive

&nbsp;   private Double massKg;



&nbsp;   @NotNull

&nbsp;   @Positive

&nbsp;   private Double orbitalPeriodDays;

}

```



\#### `com.example.spaceapp.dto.MoonDto`



```java

@Data

@NoArgsConstructor

@AllArgsConstructor

@Builder

public class MoonDto {



&nbsp;   private Long id;



&nbsp;   @NotNull

&nbsp;   @Size(min = 1, max = 100)

&nbsp;   private String name;



&nbsp;   @NotNull

&nbsp;   @Positive

&nbsp;   private Double diameterKm;



&nbsp;   @NotNull

&nbsp;   @Positive

&nbsp;   private Double orbitalPeriodDays;



&nbsp;   private Long planetId;



&nbsp;   private String planetName;

}

```



\#### `com.example.spaceapp.dto.MoonCreateUpdateDto`



```java

@Data

@NoArgsConstructor

@AllArgsConstructor

@Builder

public class MoonCreateUpdateDto {



&nbsp;   @NotNull

&nbsp;   @Size(min = 1, max = 100)

&nbsp;   private String name;



&nbsp;   @NotNull

&nbsp;   @Positive

&nbsp;   private Double diameterKm;



&nbsp;   @NotNull

&nbsp;   @Positive

&nbsp;   private Double orbitalPeriodDays;



&nbsp;   @NotNull

&nbsp;   @Positive

&nbsp;   private Long planetId;

}

```



\#### `com.example.spaceapp.dto.UserDto`



```java

@Data

@NoArgsConstructor

@AllArgsConstructor

@Builder

public class UserDto {



&nbsp;   private Long id;



&nbsp;   @NotNull

&nbsp;   @Size(min = 3, max = 50)

&nbsp;   private String username;



&nbsp;   @NotNull

&nbsp;   private UserRole role;

}

```



\#### `com.example.spaceapp.dto.UserCreateDto`



```java

@Data

@NoArgsConstructor

@AllArgsConstructor

@Builder

public class UserCreateDto {



&nbsp;   @NotNull

&nbsp;   @Size(min = 3, max = 50)

&nbsp;   private String username;



&nbsp;   @NotNull

&nbsp;   @Size(min = 6, max = 100)

&nbsp;   private String password;



&nbsp;   @NotNull

&nbsp;   private UserRole role;

}

```



\#### `com.example.spaceapp.dto.ErrorResponse`



```java

@Data

@NoArgsConstructor

@AllArgsConstructor

@Builder

public class ErrorResponse {



&nbsp;   private LocalDateTime timestamp;

&nbsp;   private int status;

&nbsp;   private String error;

&nbsp;   private String message;

&nbsp;   private String path;

}

```



---



\### 5. Repositories (Spring Data JPA)



\#### `com.example.spaceapp.repository.PlanetRepository`



```java

public interface PlanetRepository extends JpaRepository<Planet, Long> {



&nbsp;   List<Planet> findByTypeIgnoreCase(String type);



&nbsp;   Optional<Planet> findByNameIgnoreCase(String name);



&nbsp;   @Query("select p.name from Planet p")

&nbsp;   List<String> findAllNames();

}

```



\#### `com.example.spaceapp.repository.MoonRepository`



```java

public interface MoonRepository extends JpaRepository<Moon, Long> {



&nbsp;   List<Moon> findByPlanet\_NameIgnoreCase(String planetName);



&nbsp;   long countByPlanet\_Id(Long planetId);

}

```



\#### `com.example.spaceapp.repository.UserRepository`



```java

public interface UserRepository extends JpaRepository<User, Long> {



&nbsp;   Optional<User> findByUsername(String username);

}

```



---



\### 6. Services



\#### `com.example.spaceapp.service.PlanetService`



```java

@Service

@Transactional

public class PlanetService {



&nbsp;   public PlanetService(PlanetRepository planetRepository);



&nbsp;   @Transactional(readOnly = true)

&nbsp;   public List<PlanetDto> getAllPlanets();



&nbsp;   @Transactional(readOnly = true)

&nbsp;   public PlanetDto getPlanetById(Long id);



&nbsp;   public PlanetDto createPlanet(PlanetCreateUpdateDto dto);



&nbsp;   public PlanetDto updatePlanet(Long id, PlanetCreateUpdateDto dto);



&nbsp;   public void deletePlanet(Long id);



&nbsp;   @Transactional(readOnly = true)

&nbsp;   public List<PlanetDto> findByType(String type);



&nbsp;   @Transactional(readOnly = true)

&nbsp;   public List<String> getAllNames();



&nbsp;   // internal mappers (not part of public API)

&nbsp;   private PlanetDto toDto(Planet planet);



&nbsp;   private void updateEntityFromDto(Planet planet, PlanetCreateUpdateDto dto);

}

```



\#### `com.example.spaceapp.service.MoonService`



```java

@Service

@Transactional

public class MoonService {



&nbsp;   public MoonService(MoonRepository moonRepository,

&nbsp;                      PlanetRepository planetRepository);



&nbsp;   @Transactional(readOnly = true)

&nbsp;   public List<MoonDto> getAllMoons();



&nbsp;   @Transactional(readOnly = true)

&nbsp;   public MoonDto getMoonById(Long id);



&nbsp;   public MoonDto createMoon(MoonCreateUpdateDto dto);



&nbsp;   public MoonDto updateMoon(Long id, MoonCreateUpdateDto dto);



&nbsp;   public void deleteMoon(Long id);



&nbsp;   @Transactional(readOnly = true)

&nbsp;   public List<MoonDto> listByPlanetName(String planetName);



&nbsp;   @Transactional(readOnly = true)

&nbsp;   public long countByPlanetId(Long planetId);



&nbsp;   // internal mappers

&nbsp;   private MoonDto toDto(Moon moon);



&nbsp;   private void updateEntityFromDto(Moon moon,

&nbsp;                                    MoonCreateUpdateDto dto,

&nbsp;                                    Planet planet);

}

```



\#### `com.example.spaceapp.service.UserService`



```java

@Service

@Transactional

public class UserService {



&nbsp;   public UserService(UserRepository userRepository,

&nbsp;                      PasswordEncoder passwordEncoder);



&nbsp;   @Transactional(readOnly = true)

&nbsp;   public UserDto getUserById(Long id);



&nbsp;   public UserDto createUser(UserCreateDto dto);



&nbsp;   private UserDto toDto(User user);

}

```



---



\### 7. Security: UserDetails Adapter



\#### `com.example.spaceapp.security.UserPrincipal`



```java

public class UserPrincipal implements UserDetails {



&nbsp;   public UserPrincipal(User user);



&nbsp;   public Long getId();



&nbsp;   public UserRole getRole();



&nbsp;   @Override

&nbsp;   public Collection<? extends GrantedAuthority> getAuthorities();



&nbsp;   @Override

&nbsp;   public String getPassword();



&nbsp;   @Override

&nbsp;   public String getUsername();



&nbsp;   @Override

&nbsp;   public boolean isAccountNonExpired();



&nbsp;   @Override

&nbsp;   public boolean isAccountNonLocked();



&nbsp;   @Override

&nbsp;   public boolean isCredentialsNonExpired();



&nbsp;   @Override

&nbsp;   public boolean isEnabled();

}

```



---



\### 8. REST Controllers



\#### `com.example.spaceapp.controller.PlanetController`



```java

@RestController

@RequestMapping("/api/planets")

public class PlanetController {



&nbsp;   public PlanetController(PlanetService planetService);



&nbsp;   @GetMapping

&nbsp;   public List<PlanetDto> getAll();



&nbsp;   @GetMapping("/{id}")

&nbsp;   public PlanetDto getById(@PathVariable Long id);



&nbsp;   @PostMapping

&nbsp;   @ResponseStatus(HttpStatus.CREATED)

&nbsp;   public PlanetDto create(@Valid @RequestBody PlanetCreateUpdateDto dto);



&nbsp;   @PutMapping("/{id}")

&nbsp;   public PlanetDto update(@PathVariable Long id,

&nbsp;                           @Valid @RequestBody PlanetCreateUpdateDto dto);



&nbsp;   @DeleteMapping("/{id}")

&nbsp;   @ResponseStatus(HttpStatus.NO\_CONTENT)

&nbsp;   public void delete(@PathVariable Long id);



&nbsp;   @GetMapping("/search/by-type")

&nbsp;   public List<PlanetDto> findByType(@RequestParam String type);



&nbsp;   @GetMapping("/names")

&nbsp;   public List<String> getNames();

}

```



\#### `com.example.spaceapp.controller.MoonController`



```java

@RestController

@RequestMapping("/api/moons")

public class MoonController {



&nbsp;   public MoonController(MoonService moonService);



&nbsp;   @GetMapping

&nbsp;   public List<MoonDto> getAll();



&nbsp;   @GetMapping("/{id}")

&nbsp;   public MoonDto getById(@PathVariable Long id);



&nbsp;   @PostMapping

&nbsp;   @ResponseStatus(HttpStatus.CREATED)

&nbsp;   public MoonDto create(@Valid @RequestBody MoonCreateUpdateDto dto);



&nbsp;   @PutMapping("/{id}")

&nbsp;   public MoonDto update(@PathVariable Long id,

&nbsp;                         @Valid @RequestBody MoonCreateUpdateDto dto);



&nbsp;   @DeleteMapping("/{id}")

&nbsp;   @ResponseStatus(HttpStatus.NO\_CONTENT)

&nbsp;   public void delete(@PathVariable Long id);



&nbsp;   @GetMapping("/by-planet-name/{planetName}")

&nbsp;   public List<MoonDto> listByPlanetName(@PathVariable String planetName);



&nbsp;   @GetMapping("/count/by-planet/{planetId}")

&nbsp;   public long countByPlanet(@PathVariable Long planetId);

}

```



---



\### 9. GraphQL Controller and Schema



\#### `com.example.spaceapp.controller.UserGraphqlController`



```java

@Controller

public class UserGraphqlController {



&nbsp;   public UserGraphqlController(UserService userService);



&nbsp;   @QueryMapping

&nbsp;   public UserDto userById(@Argument Long id);



&nbsp;   @MutationMapping

&nbsp;   public UserDto createUser(@Argument("input") @Valid UserCreateDto input);

}

```



\#### `src/main/resources/graphql/user.graphqls`



```graphql

type User {

&nbsp; id: ID!

&nbsp; username: String!

&nbsp; role: UserRole!

}



enum UserRole {

&nbsp; ADMIN

&nbsp; STAFF

&nbsp; STUDENT

}



input CreateUserInput {

&nbsp; username: String!

&nbsp; password: String!

&nbsp; role: UserRole!

}



type Query {

&nbsp; userById(id: ID!): User

}



type Mutation {

&nbsp; createUser(input: CreateUserInput!): User

}

```



---



\### 10. Exceptions and Global Error Handling



\#### `com.example.spaceapp.exception.ResourceNotFoundException`



```java

public class ResourceNotFoundException extends RuntimeException {



&nbsp;   public ResourceNotFoundException(String message);

}

```



\#### `com.example.spaceapp.exception.BadRequestException`



```java

public class BadRequestException extends RuntimeException {



&nbsp;   public BadRequestException(String message);

}

```



\#### `com.example.spaceapp.exception.GlobalExceptionHandler`



```java

@RestControllerAdvice

public class GlobalExceptionHandler {



&nbsp;   @ExceptionHandler(ResourceNotFoundException.class)

&nbsp;   @ResponseStatus(HttpStatus.NOT\_FOUND)

&nbsp;   public ErrorResponse handleNotFound(ResourceNotFoundException ex,

&nbsp;                                       HttpServletRequest request);



&nbsp;   @ExceptionHandler(BadRequestException.class)

&nbsp;   @ResponseStatus(HttpStatus.BAD\_REQUEST)

&nbsp;   public ErrorResponse handleBadRequest(BadRequestException ex,

&nbsp;                                         HttpServletRequest request);



&nbsp;   @ExceptionHandler(MethodArgumentNotValidException.class)

&nbsp;   @ResponseStatus(HttpStatus.BAD\_REQUEST)

&nbsp;   public ErrorResponse handleValidation(MethodArgumentNotValidException ex,

&nbsp;                                         HttpServletRequest request);



&nbsp;   @ExceptionHandler(Exception.class)

&nbsp;   @ResponseStatus(HttpStatus.INTERNAL\_SERVER\_ERROR)

&nbsp;   public ErrorResponse handleGeneric(Exception ex,

&nbsp;                                      HttpServletRequest request);



&nbsp;   private ErrorResponse buildError(HttpStatus status,

&nbsp;                                    String message,

&nbsp;                                    String path);

}

```



---



\### 11. Logging (AOP / AspectJ)



\#### `com.example.spaceapp.logging.LoggingAspect`



```java

@Aspect

@Component

public class LoggingAspect {



&nbsp;   private static final Logger log =

&nbsp;           LoggerFactory.getLogger(LoggingAspect.class);



&nbsp;   // REST controllers

&nbsp;   @Pointcut("within(@org.springframework.web.bind.annotation.RestController \*)")

&nbsp;   public void restControllerPointcut();



&nbsp;   // services

&nbsp;   @Pointcut("within(@org.springframework.stereotype.Service \*)")

&nbsp;   public void servicePointcut();



&nbsp;   // @ControllerAdvice (global exception handlers)

&nbsp;   @Pointcut("within(@org.springframework.web.bind.annotation.ControllerAdvice \*)")

&nbsp;   public void exceptionHandlerPointcut();



&nbsp;   @Around("restControllerPointcut()")

&nbsp;   public Object logAroundControllers(ProceedingJoinPoint joinPoint)

&nbsp;           throws Throwable;



&nbsp;   @Around("servicePointcut()")

&nbsp;   public Object logAroundServices(ProceedingJoinPoint joinPoint)

&nbsp;           throws Throwable;



&nbsp;   @AfterThrowing(

&nbsp;           pointcut = "restControllerPointcut() || servicePointcut()",

&nbsp;           throwing = "ex")

&nbsp;   public void logThrownExceptions(JoinPoint joinPoint, Throwable ex);



&nbsp;   @Around("exceptionHandlerPointcut()")

&nbsp;   public Object logExceptionHandlers(ProceedingJoinPoint joinPoint)

&nbsp;           throws Throwable;

}

```



---



\### 12. Tests



\#### `com.example.spaceapp.SpaceAppApplicationTests`



```java

@SpringBootTest

class SpaceAppApplicationTests {



&nbsp;   @Test

&nbsp;   void contextLoads();

}

```



---



This `README.md` describes the full structure of the project down to public method signatures and key class annotations, while assuming Lombok for getters/setters/constructors so that no manual accessors are listed.



