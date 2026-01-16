package com.nitesh.usermanagement.service;

import com.nitesh.usermanagement.dto.UserRequestDTO;
import com.nitesh.usermanagement.dto.UserResponseDTO;
import com.nitesh.usermanagement.exception.ResourceNotFoundException;
import com.nitesh.usermanagement.model.User;
import com.nitesh.usermanagement.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    // Constructor injection (recommended)
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ================= CREATE USER =================
    public UserResponseDTO createUser(UserRequestDTO dto) {

        // Convert Request DTO → Entity
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());

        // Save entity
        User savedUser = userRepository.save(user);

        // Convert Entity → Response DTO
        return new UserResponseDTO(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail()
        );
    }

    // ================= GET ALL USERS =================
    public List<UserResponseDTO> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(user -> new UserResponseDTO(
                        user.getId(),
                        user.getName(),
                        user.getEmail()
                ))
                .collect(Collectors.toList());
    }

    // ================= GET USER BY ID =================
    public UserResponseDTO getUserById(Long id) {

        // Find user or throw custom exception
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with id " + id)
                );

        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    // ================= UPDATE USER =================
    public UserResponseDTO updateUser(Long id, UserRequestDTO dto) {

        // 1. Find existing user
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with id " + id)
                );

        // 2. Update fields
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());

        // 3. Save updated user
        User updatedUser = userRepository.save(user);

        // 4. Convert to Response DTO
        return new UserResponseDTO(
                updatedUser.getId(),
                updatedUser.getName(),
                updatedUser.getEmail()
        );
    }

    // ================= DELETE USER =================
    public void deleteUser(Long id) {

        // Check existence first
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with id " + id)
                );

        userRepository.delete(user);
    }

    // ================== GET USERS WITH PAGINATION ==================
    public Page<UserResponseDTO> getUsersPaginated(
            int page,
            int size,
            String sortBy,
            String direction
    ) {

        // 1. Decide sorting direction
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        // 2. Create Pageable object
        Pageable pageable = PageRequest.of(page, size, sort);

        // 3. Fetch paginated users
        Page<User> usersPage = userRepository.findAll(pageable);

        // 4. Convert Entity → DTO
        return usersPage.map(user ->
                new UserResponseDTO(
                        user.getId(),
                        user.getName(),
                        user.getEmail()
                )
        );
    }

    // ================== SEARCH USERS ==================
// Search users by name or email (partial + case-insensitive)
    public List<UserResponseDTO> searchUsers(String name, String email) {

        List<User> users;

        // If name is provided, search by name
        if (name != null && !name.isBlank()) {
            users = userRepository.findByNameContainingIgnoreCase(name);

            // Else if email is provided, search by email
        } else if (email != null && !email.isBlank()) {
            users = userRepository.findByEmailContainingIgnoreCase(email);

            // If nothing is provided, return empty list
        } else {
            return List.of();
        }

        // Convert Entity list → DTO list
        return users.stream()
                .map(user -> new UserResponseDTO(
                        user.getId(),
                        user.getName(),
                        user.getEmail()
                ))
                .toList();
    }


}
