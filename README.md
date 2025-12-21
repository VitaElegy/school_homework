# School Homework Project

This is a Spring Boot application that manages Students and a Blog system.

## Features

- **Student Management**: CRUD operations for Students.
- **Blog System**:
  - User Registration and Login (Session-based).
  - Create, View, and Delete Blog Posts.
  - Comment on Posts.
- **Validation**: Data validation for all forms using Hibernate Validator.
- **Exception Handling**: Global exception handling with a user-friendly error page.
- **UI**: Thymeleaf templates with Bootstrap 4 styling and a common navigation bar.

## Project Structure

- `src/main/java/com/school/homework`
    - `controller`: Web controllers (`BlogController`, `LoginController`).
    - `service`: Business logic interfaces and implementations.
    - `dao`: Data Access Objects (JPA Repositories).
    - `entity`: JPA Entities (`User`, `Post`, `Comment`).
    - `config`: Web and Security configuration.
    - `exception`: Global exception handling.
    - `dto`: Data Transfer Objects.
- `src/main/resources/templates`
    - `blog`: Blog pages.
    - `fragments`: Shared UI components (Header).
    - `error`: Error pages.

## Code Quality Improvements

This project has been refined to meet professional standards:

-   **Optimized Data Access**: Tag processing now uses batch fetching to prevent N+1 Select issues.
-   **Security**: Internal error details are hidden from users; proper Exception Handling mechanism is in place.
-   **Clean Architecture**: Controllers are decoupled from Repositories and `UserService`, delegating all business logic to dedicated Services.
-   **Robustness**: Standardized `ResourceNotFoundException` usage across the application.

## How to Run

1.  **Prerequisites**: Java 17+, Maven.
2.  **Build and Run**:
    ```bash
    cd school_homework
    mvn spring-boot:run
    ```
3.  **Access the Application**:
    - Home: http://localhost:8080/
    - Login: http://localhost:8080/login (Create an account first via Register)
    - H2 Console: http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:mem:testdb`)

## Tests

Run unit and integration tests:
```bash
mvn test
```

