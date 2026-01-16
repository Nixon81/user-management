# User Management API

Spring Boot REST API for managing users.  
Completed till **Step 9**.

## Tech Stack:
- Java 17
- Spring Boot
- Spring Web
- Spring Data JPA
- H2 In-Memory Database
- Maven

## Features (Completed till Step 9)
- Create User
- Get All Users (Pagination & Sorting)
- Get User by ID
- Update User
- Delete User
- Request & Response DTOs
- Validation (Jakarta Validation)
- Global Exception Handling
- Standard API Response Wrapper
- Pagination & Sorting

---
Base URL:
http://localhost:8080

==================================================

### CREATE USER
**Method**: POST

**Endpoint:** /api/users

### Request Body:
{
"name": "Nitesh",
"email": "nitesh@gmail.com"
}

### Success Response:
{
"success": true,
"message": "User created successfully",
"data": {
"id": 1,
"name": "Nitesh",
"email": "nitesh@gmail.com"
}
}

### Validation Error Response:
{
"success": false,
"message": "Validation failed",
"data": {
"name": "Name is required",
"email": "Email should be valid"
}
}

==================================================

### GET ALL USERS (Pagination & Sorting)
Method: GET
Endpoint: /api/users

### Query Params:
page=0
size=5
sort=id,desc

### Example Request:
/api/users?page=0&size=5&sort=id,desc

Success Response:
{
"success": true,
"message": "Users fetched with pagination",
"data": {
"content": [
{
"id": 2,
"name": "Alex",
"email": "alex@gmail.com"
},
{
"id": 1,
"name": "Nitesh",
"email": "nitesh@gmail.com"
}
],
"totalElements": 5,
"totalPages": 3,
"size": 5,
"number": 0
}
}

==================================================

### GET USER BY ID
Method: GET
Endpoint: /api/users/{id}

### Example Request:
/api/users/1

### Success Response:
{
"success": true,
"message": "User fetched successfully",
"data": {
"id": 1,
"name": "Nitesh",
"email": "nitesh@gmail.com"
}
}

### Not Found Response:
{
"success": false,
"message": "User not found with id 999",
"data": null
}

==================================================

### UPDATE USER
Method: PUT
Endpoint: /api/users/{id}

### Request Body:
{
"name": "Nitesh Updated",
"email": "nitesh.updated@gmail.com"
}

### Success Response:
{
"success": true,
"message": "User updated successfully",
"data": {
"id": 1,
"name": "Nitesh Updated",
"email": "nitesh.updated@gmail.com"
}
}

==================================================

### DELETE USER
Method: DELETE
Endpoint: /api/users/{id}

### Example Request:
/api/users/1

### Success Response:
204 No Content

### Not Found Response:
{
"success": false,
"message": "User not found with id 999",
"data": null
}

==================================================

### Notes:
- Uses H2 In-Memory DB (data resets on restart)
- DTO based request/response
- Validation enabled
- Global exception handling
- Standard API response wrapper
