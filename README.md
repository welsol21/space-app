# Space App - Planetary System API

Spring Boot 3.5.8 RESTful + GraphQL API для управления информацией о планетах и их лунах с ролевой системой доступа.

## 📋 Функциональность

### ✅ Полностью реализовано

#### **Entities**
- `Planet` (planet_id, name, type, radius_km, mass_kg, orbital_period_days)
- `Moon` (moon_id, name, diameter_km, orbital_period_days, planet_id)
- `User` (user_id, username, password, role: ADMIN/STAFF/STUDENT)

#### **REST API Endpoints**

**Planets:**
- `POST /api/planets` - создать планету
- `GET /api/planets` - список всех планет
- `GET /api/planets/{id}` - планета по ID
- `PUT /api/planets/{id}` - обновить планету
- `DELETE /api/planets/{id}` - удалить планету
- `GET /api/planets/search/by-type?type=...` - поиск по типу
- `GET /api/planets/names` - получить только имена планет

**Moons:**
- `POST /api/moons` - создать луну (с проверкой существования планеты)
- `GET /api/moons` - список всех лун
- `GET /api/moons/{id}` - луна по ID
- `DELETE /api/moons/{id}` - удалить луну
- `GET /api/moons/by-planet-name/{planetName}` - луны по имени планеты
- `GET /api/moons/count/by-planet/{planetId}` - количество лун у планеты

#### **GraphQL Endpoints**
- **Query:** `userById(id: ID!): User`
- **Mutation:** `createUser(input: CreateUserInput!): User`

#### **Security (Spring Security Basic Auth)**
- **ADMIN:** полный доступ + управление пользователями (GraphQL)
- **STAFF:** CRUD операции для планет и лун
- **STUDENT:** только чтение планет и лун
- Пароли хешируются через BCrypt
- URL-based security + `@PreAuthorize` для fine-grained control

#### **AOP Logging (AspectJ)**
Реализовано 3 pointcut:
1. **Controller layer** - логирование входа/выхода методов
2. **Service layer** - измерение времени выполнения
3. **Exception handling** - логирование исключений

#### **Best Practices**
- Разделение слоёв: Controllers → Services → Repositories
- DTOs для API (не entities напрямую)
- Валидация через Jakarta (`@NotNull`, `@Size`, `@Valid`)
- Централизованная обработка исключений (`@ControllerAdvice`)
- Custom JPA queries (`@Query`)
- Использование `@ResponseStatus` вместо `ResponseEntity<>`

#### **Дополнительные возможности**
- Swagger UI: http://localhost:8080/swagger-ui.html
- H2 Console: http://localhost:8080/h2-console
- GraphiQL: http://localhost:8080/graphiql
- Actuator: http://localhost:8080/actuator/mappings

---

## 🚀 Как запустить

### Требования
- Java 17+
- Maven 3.6+

### Шаги

1. **Клонировать репозиторий**
```bash
git clone <repo-url>
cd space-app
```

2. **Сборка проекта**
```bash
mvn clean install
```

3. **Запуск приложения**
```bash
mvn spring-boot:run
```

Приложение будет доступно на: http://localhost:8080

---

## 🔑 Предзагруженные пользователи

| Username | Password | Role    |
|----------|----------|---------|
| admin    | admin123 | ADMIN   |
| staff    | staff123 | STAFF   |
| student  | stud123  | STUDENT |

---

## 📚 API Documentation

### Swagger UI
Открой в браузере: http://localhost:8080/swagger-ui.html

### GraphiQL
Открой в браузере: http://localhost:8080/graphiql

**Пример GraphQL Query:**
```graphql
query {
  userById(id: 1) {
    id
    username
    role
  }
}
```

**Пример GraphQL Mutation:**
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

## 🧪 Тестирование

### Запустить все тесты
```bash
mvn test
```

### Запустить конкретный тест
```bash
mvn test -Dtest=PlanetServiceTest
```

### Отчёт о тестах
После выполнения тестов отчёт генерируется в:
- `target/test-report.md`
- `test-report.md` (корень проекта)

---

## 🗄️ База данных

### H2 Console
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:spacedb`
- Username: `sa`
- Password: *(пусто)*

---

## 📊 Actuator Endpoints

- Health: http://localhost:8080/actuator/health
- Mappings: http://localhost:8080/actuator/mappings
- Metrics: http://localhost:8080/actuator/metrics
- Info: http://localhost:8080/actuator/info

---

## 🛠️ Технологии

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

## ⚠️ Известные ограничения

Все функциональные требования реализованы. Никаких ограничений нет.

---

## 📝 Примеры запросов (REST)

### Создать планету (ADMIN/STAFF)
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

### Получить все планеты (любая роль)
```bash
curl -X GET http://localhost:8080/api/planets \
  -u student:stud123
```

### Удалить планету (только ADMIN/STAFF)
```bash
curl -X DELETE http://localhost:8080/api/planets/1 \
  -u admin:admin123
```

---

## 👥 Авторы

[Ваше имя] - Manual Implementation

---

## 📄 Лицензия

Этот проект создан для учебных целей (MTU App Development Frameworks, 2025).
