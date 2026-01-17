package com.nitesh.usermanagement.controller;

import com.nitesh.usermanagement.dto.ApiResponse;
import com.nitesh.usermanagement.dto.UserRequestDTO;
import com.nitesh.usermanagement.dto.UserResponseDTO;
import com.nitesh.usermanagement.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // Constructor injection
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ================= CREATE USER =================
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponseDTO>> createUser(
            @Valid @RequestBody UserRequestDTO dto) {

        UserResponseDTO response = userService.createUser(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User created successfully", response));
    }

    // ================= GET ALL USERS =================
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> getAllUsers() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Users fetched successfully",
                        userService.getAllUsers()
                )
        );
    }

    // ================= GET USER BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUserById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "User fetched successfully",
                        userService.getUserById(id)
                )
        );
    }

    // ================= UPDATE USER =================
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDTO dto) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "User updated successfully",
                        userService.updateUser(id, dto)
                )
        );
    }

    // ================= DELETE USER =================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build(); // 204
    }

    // ================= GET USERS WITH PAGINATION =================
    @GetMapping("/page")
    public ResponseEntity<ApiResponse<Page<UserResponseDTO>>> getUsersPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {

        Page<UserResponseDTO> users =
                userService.getUsersPaginated(page, size, sortBy, direction);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Users fetched with pagination",
                        users
                )
        );
    }

    // ================= SEARCH USERS =================
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> searchUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email
    ) {

        List<UserResponseDTO> users = userService.searchUsers(name, email);

        return ResponseEntity.ok(
                ApiResponse.success("Users fetched successfully", users)
        );
    }
}
