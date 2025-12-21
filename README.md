# School Blog Project

This is a simple Spring Boot Blog application.

## Prerequisites

*   JDK 17 or higher
*   Maven 3.x

## How to Run

1.  **Open a terminal** in the project root.
2.  **Run the application** using Maven:
    ```bash
    mvn spring-boot:run
    ```
    *Alternatively, you can build the JAR and run it:*
    ```bash
    mvn clean package
    java -jar target/blog-0.0.1-SNAPSHOT.jar
    ```

## Accessing the Application

*   **Home Page**: [http://localhost:8080](http://localhost:8080)
*   **H2 Database Console**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
    *   **JDBC URL**: `jdbc:h2:mem:testdb`
    *   **User**: `sa`
    *   **Password**: `password`

## Default Accounts

The application initializes with two default users:

| Role | Username | Password |
| :--- | :--- | :--- |
| **Admin** | `admin` | `admin123` |
| **User** | `user` | `password` |

## Features

*   **Public Access**: View posts and comments.
*   **User Role**: Create posts, comment on posts.
*   **Admin Role**: Full control (manage all posts/comments).
*   **Security**: Role-based access control (RBAC) using Spring Security.
*   **UI**: Modern, responsive design using Bootstrap 4 and Thymeleaf.
