# Space Exploration API - Manual Implementation

## Project Overview

This is a Spring Boot 3.5.8 REST and GraphQL API for managing planets, moons, and users with role-based security. The project was developed manually by our team as part of an assignment to compare human-written code with AI-generated implementations.

## Technologies Used

- **Java 17**
- **Spring Boot 3.5.8**
- **Spring Data JPA** (Hibernate)
- **H2 Database** (in-memory)
- **Spring Security** (Basic Authentication)
- **Spring Boot GraphQL**
- **Maven** (build tool)
- **Lombok** (boilerplate reduction)
- **Swagger/OpenAPI** (API documentation)
- **AspectJ** (AOP logging)

## Features

### Entities

1. **Planet**
   - Fields: id, name, type, radiusKm, massKg, orbitalPeriodDays
   - One-to-many relationship with Moon

2. **Moon**
   - Fields: id, name, diameterKm, orbitalPeriodDays, planetId (FK)
   - Many-to-one relationship with Planet

3. **User**
   - Fields: id, username, password (BCrypt hashed), role (ADMIN/STAFF/STUDENT)
   - Used for authentication and authorization

### REST API Endpoints

**Planets** (`/api/planets`)
- `GET /api/planets` - List all planets
- `GET /api/planets/{id}` - Get planet by ID
- `POST /api/planets` - Create new planet (ADMIN/STAFF only)
- `PUT /api/planets/{id}` - Update planet (ADMIN/STAFF only)
- `DELETE /api/planets/{id}` - Delete planet (ADMIN/STAFF only)
- `GET /api/planets/search/by-type?type={type}` - Search by type
- `GET /api/planets/names` - Get all planet names
- `GET /api/planets/fields/name-mass` - Get name and mass only

**Moons** (`/api/moons`)
- `GET /api/moons` - List all moons
- `GET /api/moons/{id}` - Get moon by ID
- `POST /api/moons` - Create new moon (ADMIN/STAFF only)
- `PUT /api/moons/{id}` - Update moon (ADMIN/STAFF only)
- `DELETE /api/moons/{id}` - Delete moon (ADMIN/STAFF only)
- `GET /api/moons/by-planet-name/{planetName}` - List moons by planet name
- `GET /api/moons/count/by-planet/{planetId}` - Count moons for a planet

### GraphQL API (`/graphql`)

**Queries**
- `userById(id: ID!)` - Find user by ID (ADMIN only)

**Mutations**
- `createUser(username: String!, password: String!, role: String!)` - Create user (ADMIN only)

### Security

- **Authentication**: HTTP Basic Authentication
- **Authorization**: Role-based access control
  - **ADMIN**: Full access to all endpoints including user management
  - **STAFF**: Can create, update, delete planets and moons
  - **STUDENT**: Read-only access to planets and moons
- **Password Hashing**: BCrypt with strength 10

### Preloaded Test Users

| Username | Password    | Role    |
|----------|-------------|---------|
| admin    | admin123    | ADMIN   |
| staff    | staff123    | STAFF   |
| student  | student123  | STUDENT |

### Best Practices Implemented

1. **Layered Architecture**: Controllers → Services → Repositories
2. **DTOs**: Separate DTOs for input/output, not exposing entities directly
3. **Validation**: Jakarta validation annotations (@NotNull, @Size, @Positive)
4. **Centralized Exception Handling**: @RestControllerAdvice with structured error responses
5. **AOP Logging**: AspectJ with 3 pointcuts:
   - Controller entry/exit logging (@Before/@AfterReturning)
   - Service method execution timing (@Around)
   - Exception logging (@AfterThrowing)
6. **Security**: Method-level security with @PreAuthorize and URL-based configuration
7. **API Documentation**: Swagger/OpenAPI accessible at `/swagger-ui.html`

## How to Run

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Option 1: From Submission ZIP (Recommended)

After extracting the submission ZIP file:

**1. Human Implementation (Main Project)**
```bash
# Navigate to extracted directory
cd space-app

# Build and run
mvn clean install
mvn spring-boot:run
```
The application will start on `http://localhost:8080`

**2. GPT-Generated Project**
```bash
# Navigate to GPT project
cd gpt5.1_space-app

# Build and run
mvn clean install
mvn spring-boot:run
```
The application will start on `http://localhost:8080`

**3. Gemini-Generated Project**
```bash
# Navigate to Gemini project
cd Gemini3.1/universe-api

# Build and run
mvn clean install
mvn spring-boot:run
```
The application will start on `http://localhost:8080`

**Note**: Only run one application at a time as they all use port 8080.

### Option 2: From Git Repository

```bash
# Clone the repository
git clone https://github.com/welsol21/space-app.git
cd space-app

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Access Points

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **H2 Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:testdb`
  - Username: `sa`
  - Password: (leave blank)
- **GraphQL**: http://localhost:8080/graphql
- **Actuator**: http://localhost:8080/actuator

### Testing with Swagger

1. Navigate to http://localhost:8080/swagger-ui.html
2. Click "Authorize" button
3. Enter credentials (e.g., `admin` / `admin123`)
4. Try various endpoints

## AI-Generated Implementations

This repository also includes two AI-generated versions of the same project for comparison:

### 1. GPT-Generated Project (`gpt5.1_space-app/`)
- Generated using ChatGPT
- **Status**: Compiles and runs successfully
- **Test Results**: 6/9 controller tests pass
- **Missing Features**: 1 endpoint (`/api/planets/fields/name-mass`)
- **Completeness**: ~90%

### 2. Gemini-Generated Project (`Gemini3.1/universe-api/`)
- Generated using Google Gemini AI Studio
- **Status**: Compiles and runs successfully
- **Test Results**: 6/7 controller tests pass
- **Missing Features**: 2 endpoints (names, name+mass)
- **Known Issues**: GraphQL query not restricted to ADMIN; moon update bug
- **Completeness**: ~85%

See `AI-CRITICAL-ANALYSIS.md` for a detailed comparison of all three implementations.

## Project Structure

```
space-app/
├── src/                                    # Manual implementation 
│   ├── main/
│   │   ├── java/com/example/spaceapp/
│   │   │   ├── SpaceAppApplication.java
│   │   │   ├── aspect/
│   │   │   │   └── LoggingAspect.java
│   │   │   ├── config/
│   │   │   │   ├── DataInitializer.java
│   │   │   │   └── SecurityConfig.java
│   │   │   ├── controller/
│   │   │   │   ├── MoonController.java
│   │   │   │   ├── PlanetController.java
│   │   │   │   └── UserGraphqlController.java
│   │   │   ├── dto/
│   │   │   ├── exception/
│   │   │   │   ├── BadRequestException.java
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   └── ResourceNotFoundException.java
│   │   │   ├── model/
│   │   │   │   ├── Moon.java
│   │   │   │   ├── Planet.java
│   │   │   │   ├── User.java
│   │   │   │   └── UserRole.java
│   │   │   ├── repository/
│   │   │   │   ├── MoonRepository.java
│   │   │   │   ├── PlanetRepository.java
│   │   │   │   └── UserRepository.java
│   │   │   ├── security/
│   │   │   │   └── UserPrincipal.java
│   │   │   └── service/
│   │   │       ├── MoonService.java
│   │   │       ├── PlanetService.java
│   │   │       └── UserService.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── graphql/
│   │           └── user.graphqls
│   └── test/
│       └── java/com/example/spaceapp/
│           ├── controller/
│           │   ├── MoonControllerIntegrationTest.java
│           │   ├── PlanetControllerIntegrationTest.java
│           │   └── UserGraphqlControllerIntegrationTest.java
│           ├── repository/
│           └── service/
├── gpt5.1_space-app/                      # GPT-generated implementation
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/spaceapp/
│   │   │   │   ├── SpaceAppApplication.java
│   │   │   │   ├── aspect/
│   │   │   │   │   └── LoggingAspect.java
│   │   │   │   ├── config/
│   │   │   │   │   ├── DataInitializer.java
│   │   │   │   │   └── SecurityConfig.java
│   │   │   │   ├── controller/
│   │   │   │   │   ├── MoonController.java
│   │   │   │   │   ├── PlanetController.java
│   │   │   │   │   └── UserGraphqlController.java
│   │   │   │   ├── dto/
│   │   │   │   ├── exception/
│   │   │   │   │   └── GlobalExceptionHandler.java
│   │   │   │   ├── model/
│   │   │   │   │   ├── Moon.java
│   │   │   │   │   ├── Planet.java
│   │   │   │   │   └── User.java
│   │   │   │   ├── repository/
│   │   │   │   │   ├── MoonRepository.java
│   │   │   │   │   ├── PlanetRepository.java
│   │   │   │   │   └── UserRepository.java
│   │   │   │   └── service/
│   │   │   │       ├── MoonService.java
│   │   │   │       ├── PlanetService.java
│   │   │   │       └── UserService.java
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       └── graphql/
│   │   │           └── user.graphqls
│   │   └── test/
│   │       └── java/com/example/spaceapp/controller/
│   │           ├── MoonControllerIntegrationTest.java
│   │           ├── PlanetControllerIntegrationTest.java
│   │           ├── UserGraphqlControllerIntegrationTest.java
│   │           └── testutil/
│   │               ├── JsonUtil.java
│   │               └── TestUsers.java
│   └── pom.xml
├── Gemini3.1/                             # Gemini-generated implementation
│   └── universe-api/
│       ├── src/
│       │   ├── main/
│       │   │   ├── java/com/example/universe/
│       │   │   │   ├── UniverseApplication.java
│       │   │   │   ├── aspect/
│       │   │   │   │   └── LoggingAspect.java
│       │   │   │   ├── config/
│       │   │   │   │   ├── DataLoader.java
│       │   │   │   │   ├── OpenApiConfig.java
│       │   │   │   │   └── SecurityConfig.java
│       │   │   │   ├── controller/
│       │   │   │   │   ├── MoonController.java
│       │   │   │   │   ├── PlanetController.java
│       │   │   │   │   └── UserGraphqlController.java
│       │   │   │   ├── dto/
│       │   │   │   ├── exception/
│       │   │   │   │   └── GlobalExceptionHandler.java
│       │   │   │   ├── model/
│       │   │   │   │   ├── Moon.java
│       │   │   │   │   ├── Planet.java
│       │   │   │   │   ├── User.java
│       │   │   │   │   └── UserRole.java
│       │   │   │   ├── repository/
│       │   │   │   │   ├── MoonRepository.java
│       │   │   │   │   ├── PlanetRepository.java
│       │   │   │   │   └── UserRepository.java
│       │   │   │   └── service/
│       │   │   │       ├── MoonService.java
│       │   │   │       ├── PlanetService.java
│       │   │   │       └── UserService.java
│       │   │   └── resources/
│       │   │       ├── application.properties
│       │   │       └── graphql/
│       │   │           └── schema.graphqls
│       │   └── test/
│       │       └── java/com/example/universe/controller/
│       │           ├── MoonControllerIntegrationTest.java
│       │           ├── PlanetControllerIntegrationTest.java
│       │           ├── UserGraphqlControllerIntegrationTest.java
│       │           └── testutil/
│       │               ├── JsonUtil.java
│       │               └── TestUsers.java
│       └── pom.xml
├── AI-CRITICAL-ANALYSIS.md                # Comparison report
├── README.md                              # This file
└── pom.xml                                # Main project POM
```

## Running Tests

```bash
mvn test
```

All 32 tests pass successfully:
- 15 controller integration tests
- 6 repository tests
- 9 service integration tests
- 1 data initializer test
- 1 application context test

## Design Decisions

1. **Code-based Data Initialization**: Used `DataInitializer` (ApplicationRunner) instead of `data.sql` to avoid primary key conflicts in tests
2. **Transactional Tests**: All integration tests use `@Transactional` to ensure data isolation
3. **Custom Repository Methods**: Used Spring Data JPA query derivation for most queries; `@Query` for specific field projections
4. **DTO Mapping**: Manual mapping methods in services (toDto/fromDto) for full control
5. **GraphQL Limited Scope**: Only User entity exposed via GraphQL as per specification
6. **Response Status Annotations**: Controllers use `@ResponseStatus` instead of `ResponseEntity<>` as per best practice

## Known Limitations

None - all required functionality is implemented and tested.

## Authors

Development Team (Manual Implementation)

## License

Educational project - MTU App Development Frameworks course
