package com.nitesh.usermanagement.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserDtoTest {

    @Test
    void userRequestDto_coverage() {
        UserRequestDTO req = new UserRequestDTO("Nitesh", "nitesh@test.com");

        assertEquals("Nitesh", req.getName());
        assertEquals("nitesh@test.com", req.getEmail());

        req.toString();
        req.hashCode();
    }

    @Test
    void userResponseDto_coverage() {
        UserResponseDTO res = new UserResponseDTO(1L, "Nitesh", "nitesh@test.com");

        assertEquals(1L, res.getId());
        assertEquals("Nitesh", res.getName());
        assertEquals("nitesh@test.com", res.getEmail());

        res.toString();
        res.hashCode();
    }

    @Test
    void apiResponse_coverage() {
        ApiResponse<String> response =
                ApiResponse.success("OK", "data");

        assertTrue(response.isSuccess());
        assertEquals("OK", response.getMessage());
        assertEquals("data", response.getData());

        response.toString();
        response.hashCode();
    }
}
