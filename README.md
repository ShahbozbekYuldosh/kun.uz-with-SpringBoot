# 📰 Kun.uz Clone Backend Service

This repository contains the backend service for a news platform similar to Kun.uz, built using **Spring Boot 4**, focusing on robust security, clean architecture, and efficient data handling.

---

## 🚀 Key Features

<details>
<summary>Click to expand Features</summary>

- **Custom JWT Authentication**: Secure user and administrative access using JSON Web Tokens (JWT).
- **Role-Based Authorization**: Implemented with Spring Security `@PreAuthorize` to manage access control for roles (`ROLE_ADMIN`, `ROLE_USER`, `ROLE_MODERATOR`).
- **Automatic Admin Initialization**: Uses `CommandLineRunner` (`AdminInitializer`) to ensure a "Super Admin" exists on first startup.
- **Layered Architecture**: Clear separation of Controller, Service, Repository, Entity, and DTO layers.
- **Profile Management**: Endpoints for updating user profiles (photo, password, status).
- **Article Management**:
    - Create, update, delete, and publish articles
    - Article images, categories, sections, regions support
    - View count & share count tracking
- **Attach/File Management**:
    - Upload, open, download, delete files
    - Automatic folder organization by date
- **Filtering and Searching**:
    - Filter articles by publisher, moderator, category, section, region
    - Paginated responses
- **Multi-language Support**: Articles, categories, sections, and regions support **UZ, RU, EN, KRILL**
- **Soft Delete & Visibility Control**: Articles and attachments can be hidden instead of permanent deletion
- **REST API Documentation Ready**: Easily integrate with Swagger/OpenAPI

</details>

---

## 🛠️ Technology Stack

| Category      | Technology                  | Version |
|---------------|-----------------------------|---------|
| Backend       | Spring Boot                 | 4.0.0   |
| Security      | Spring Security             | 4.0.0   |
| Database      | PostgreSQL (or any RDBMS)  | -       |
| ORM           | Spring Data JPA (Hibernate) | -       |
| Utility       | Lombok                      | -       |
| Testing       | JUnit 5                     | -       |

---

## 📦 Project Setup

### Prerequisites

- Java 17 or higher
- Maven or Gradle
- PostgreSQL instance

### Configuration

Update your database connection settings in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/kunuz_db
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update # 'create' or 'create-drop' for fresh setup
