# Space App - Planetary System API

Spring Boot 3.5.8 RESTful + GraphQL API for managing information about planets and their moons with role-based access control.

## 📋 Functionality

### ✅ Fully Implemented

#### **Entities**
- `Planet` (planet_id, name, type, radius_km, mass_kg, orbital_period_days)
- `Moon` (moon_id, name, diameter_km, orbital_period_days, planet_id)
- `User` (user_id, username, password, role: ADMIN/STAFF/STUDENT)

#### **REST API Endpoints**

**Planets:**
- `POST /api/planets` - create a planet
- `GET /api/planets` - list all planets
- `GET /api/planets/{id}` - get planet by ID
- `PUT /api/planets/{id}` - update planet
- `DELETE /api/planets/{id}` - delete planet
- `GET /api/planets/search/by-type?type=...` - search by type
- `GET /api/planets/names` - get planet names only
- `GET /api/planets/fields/name-mass` - get only name and mass_kg

**Moons:**
- `POST /api/moons` - create a moon (with planet existence validation)
- `GET /api/moons` - list all moons
- `GET /api/moons/{id}` - get moon by ID
- `DELETE /api/moons/{id}` - delete moon
- `GET /api/moons/by-planet-name/{planetName}` - list moons by planet name
- `GET /api/moons/count/by-planet/{planetId}` - count moons for a planet

#### **GraphQL Endpoints**
- **Query:** `userById(id: ID!): User`
- **Mutation:** `createUser(input: CreateUserInput!): User`

#### **Security (Spring Security Basic Auth)**
- **ADMIN:** full access + user management (GraphQL)
- **STAFF:** CRUD operations for planets and moons
- **STUDENT:** read-only access to planets and moons
- Passwords hashed via BCrypt
- URL-based security + `@PreAuthorize` for fine-grained control

#### **AOP Logging (AspectJ)**
3 pointcuts implemented:
1. **Controller layer** - entry/exit logging
2. **Service layer** - execution time measurement
3. **Exception handling** - exception logging

#### **Best Practices**
- Layer separation: Controllers → Services → Repositories
- DTOs for API (not entities directly)
- Jakarta validation (`@NotNull`, `@Size`, `@Valid`)
- Centralized exception handling (`@ControllerAdvice`)
- Custom JPA queries (`@Query`)
- Using `@ResponseStatus` instead of `ResponseEntity<>`

#### **Additional Features**
- Swagger UI: http://localhost:8080/swagger-ui.html
- H2 Console: http://localhost:8080/h2-console
- GraphiQL: http://localhost:8080/graphiql
- Actuator: http://localhost:8080/actuator/mappings

---

## 🚀 How to Run

### Requirements
- Java 17+
- Maven 3.6+

### Steps

1. **Clone repository**
```bash
git clone <repo-url>
cd space-app
```

2. **Build project**
```bash
mvn clean install
```

3. **Run application**
```bash
mvn spring-boot:run
```

Application will be available at: http://localhost:8080

---

## 🔑 Preloaded Users

| Username | Password | Role    |
|----------|----------|---------|
| admin    | admin123 | ADMIN   |
| staff    | staff123 | STAFF   |
| student  | student123 | STUDENT |

---

## 🌱 Preloaded Sample Data

- Planets: Earth (Terrestrial), Jupiter (Gas Giant)
- Moons: Moon (Earth), Io (Jupiter), Europa (Jupiter)

## 📚 API Documentation

### Swagger UI
Open in browser: http://localhost:8080/swagger-ui.html

### GraphiQL
Open in browser: http://localhost:8080/graphiql

**GraphQL Query Example:**
```graphql
query {
  userById(id: 1) {
    id
    username
    role
  }
}
```

**GraphQL Mutation Example:**
```graphql
mutation {
  createUser(input: {
    username: "newuser"
    password: "password123"
    role: STUDENT
  }) {
    id
    username
    role
  }
}
```

---

## 🧪 Testing

### Run all tests
```bash
mvn test
```

### Run specific test
```bash
mvn test -Dtest=PlanetServiceTest
```

### Test Report
After running tests, report is generated at:
- `target/test-report.md`
- `test-report.md` (project root)

---

## 🗄️ Database

### H2 Console
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:spacedb`
- Username: `sa`
- Password: (empty)

---

## 📊 Actuator Endpoints

- Health: http://localhost:8080/actuator/health
- Mappings: http://localhost:8080/actuator/mappings
- Metrics: http://localhost:8080/actuator/metrics
- Info: http://localhost:8080/actuator/info

---

## 🛠️ Technologies

- **Spring Boot:** 3.5.8
- **Java:** 17
- **Build Tool:** Maven
- **Database:** H2 (in-memory)
- **Security:** Spring Security (Basic Auth, BCrypt)
- **API:** REST + GraphQL
- **Validation:** Jakarta Bean Validation
- **AOP:** AspectJ
- **Documentation:** Swagger/OpenAPI (springdoc)
- **Monitoring:** Spring Boot Actuator
- **Boilerplate Reduction:** Lombok

---

## ⚠️ Known Limitations

All functional requirements are implemented. No limitations.

---

## 📝 REST Request Examples

### Create planet (ADMIN/STAFF)
```bash
curl -X POST http://localhost:8080/api/planets \
  -u admin:admin123 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Earth",
    "type": "terrestrial",
    "radiusKm": 6371,
    "massKg": 5.972e24,
    "orbitalPeriodDays": 365
  }'
```

### Get all planets (any role)
```bash
curl -X GET http://localhost:8080/api/planets \
  -u student:student123
```

### Delete planet (ADMIN/STAFF only)
```bash
curl -X DELETE http://localhost:8080/api/planets/1 \
  -u admin:admin123
```

---

## 👥 Authors

[Your Name] - Manual Implementation

---

## 📄 License

This project was created for educational purposes (MTU App Development Frameworks, 2025).
