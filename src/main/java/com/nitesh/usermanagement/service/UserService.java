package com.nitesh.usermanagement.service;

import com.nitesh.usermanagement.dto.UserRequestDTO;
import com.nitesh.usermanagement.dto.UserResponseDTO;
import com.nitesh.usermanagement.exception.ResourceNotFoundException;
import com.nitesh.usermanagement.model.User;
import com.nitesh.usermanagement.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    // Constructor injection
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ================= CREATE USER =================
    public UserResponseDTO createUser(UserRequestDTO dto) {
        log.info("Creating user with email: {}", dto.getEmail());

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());

        User savedUser = userRepository.save(user);

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
        log.info("Fetching user with id: {}", id);

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

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with id " + id)
                );

        user.setName(dto.getName());
        user.setEmail(dto.getEmail());

        User updatedUser = userRepository.save(user);

        return new UserResponseDTO(
                updatedUser.getId(),
                updatedUser.getName(),
                updatedUser.getEmail()
        );
    }

    // ================= DELETE USER =================
    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with id " + id)
                );

        userRepository.delete(user);
    }

    // ================= PAGINATION =================
    public Page<UserResponseDTO> getUsersPaginated(
            int page,
            int size,
            String sortBy,
            String direction
    ) {

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<User> usersPage = userRepository.findAll(pageable);

        return usersPage.map(user ->
                new UserResponseDTO(
                        user.getId(),
                        user.getName(),
                        user.getEmail()
                )
        );
    }

    // ================= SEARCH =================
    public List<UserResponseDTO> searchUsers(String name, String email) {

        List<User> users;

        if (name != null && !name.isBlank()) {
            users = userRepository.findByNameContainingIgnoreCase(name);
        } else if (email != null && !email.isBlank()) {
            users = userRepository.findByEmailContainingIgnoreCase(email);
        } else {
            return List.of();
        }

        return users.stream()
                .map(user -> new UserResponseDTO(
                        user.getId(),
                        user.getName(),
                        user.getEmail()
                ))
                .toList();
    }
}
