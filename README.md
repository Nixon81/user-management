# User Management API

A Spring Boot–based REST API for user management, designed with clean layered architecture, centralized validation and error handling, and a comprehensive testing strategy covering controller, service, and integration layers.

## Overview
This service provides APIs to manage users, including:
- Create, update, delete users
- Fetch users (all, by ID, paginated)
- Search users by name or email
- Input validation and centralized exception handling

The project is designed with clear separation of concerns and comprehensive automated testing.
## Tech Stack:
- Java 17
- Spring Boot
- Spring Web (REST)
- Spring Data JPA
- H2 In-Memory Database
- Maven
- Jakarta Validation 
- JUnit 5, Mockito, MockMvc
## API Endpoints (Summary)
| Method | Endpoint            | Description          |
| ------ | ------------------- | -------------------- |
| POST   | `/api/users`        | Create user          |
| GET    | `/api/users`        | Get all users        |
| GET    | `/api/users/{id}`   | Get user by ID       |
| PUT    | `/api/users/{id}`   | Update user          |
| DELETE | `/api/users/{id}`   | Delete user          |
| GET    | `/api/users/page`   | Paginated users      |
| GET    | `/api/users/search` | Search by name/email |
## Validation & Error Handling
### Request Validation
- Input validation using **Jakarta Validation**
- Invalid requests return `400 Bad Request`
### Error Handling
- Centralized exception handling using `@ControllerAdvice`
- Consistent error responses across the API
### HTTP Status Codes
- `400 Bad Request` – Validation failures
- `404 Not Found` – Resource not found
- `201 Created` – Resource successfully created
- `204 No Content` – Resource successfully deleted
- `500 Internal Server Error` – Unexpected server errors
## Testing Strategy
The project follows a layered testing approach to ensure correctness, maintainability, and fast feedback.
### Controller Tests
- Implemented using `@WebMvcTest`
- Focus on request mapping, validation, and response structure
- Service layer is mocked to isolate the web layer
- Covers success paths, validation errors, and not-found scenarios
### Service Tests
- Implemented with **JUnit 5** and **Mockito**
- Tests business logic independently of the web layer
- Repository interactions are mocked
- Covers edge cases and exception handling
### Integration Tests
- Implemented using `@SpringBootTest`
- Loads the full application context
- Uses an in-memory **H2** database
- Verifies end-to-end request → persistence → response flow
### Test Coverage Notes
- Overall coverage is **~90%+**
- Business-critical layers (Controller and Service) have high coverage
- DTO coverage is intentionally not forced to 100%
    - DTOs contain no business logic
    - Remaining uncovered lines are Lombok-generated boilerplate
    - This aligns with industry best practices
##  Features
### User Management
- Create, update, delete users
- Fetch all users or retrieve a user by ID
- Input validation with meaningful error messages
### Pagination & Search
- Paginated user listing with configurable page size and sorting
- Search users by name or email
### API Design
- RESTful endpoints with consistent response structure
- Proper HTTP status codes for all operations
- Centralized error handling
### Testing & Quality
- Layered testing strategy (Controller, Service, Integration)
- High test coverage on business-critical components
- Clean separation of concerns and maintainable architecture
## Running the Application
### Prerequisites
- Java 17
- Maven
### Application URL
- Base URL: http://localhost:8080
- H2 Console: http://localhost:8080/h2-console
### Notes
- Uses H2 in-memory database (data resets on restart)
- DTO-based request and response structure
- Request validation enabled using Jakarta Validation
- Centralized global exception handling
- Standardized API response wrapper
## Running Tests
Run all unit and integration tests using:
```bash
mvn clean test
