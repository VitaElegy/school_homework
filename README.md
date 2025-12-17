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
    - `controller`: Web controllers (`StudentController`, `BlogController`, `LoginController`).
    - `service`: Business logic interfaces and implementations.
    - `dao`: Data Access Objects (JPA Repositories).
    - `entity`: JPA Entities (`User`, `Student`, `Post`, `Comment`).
    - `config`: Web configuration (Interceptors).
    - `exception`: Global exception handling.
- `src/main/resources/templates`
    - `students`: Student management pages.
    - `blog`: Blog pages.
    - `fragments`: Shared UI components (Header).
    - `error`: Error pages.

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

