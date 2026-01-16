package com.nitesh.usermanagement.dto;

/**
 * DTO for sending user data in API responses.
 */
public class UserResponseDTO {

    private Long id;
    private String name;
    private String email;

    // constructor
    public UserResponseDTO(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    // getters only (immutable response)
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
