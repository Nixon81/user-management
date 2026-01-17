package com.nitesh.usermanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nitesh.usermanagement.dto.UserRequestDTO;
import com.nitesh.usermanagement.dto.UserResponseDTO;
import com.nitesh.usermanagement.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    // --------------------------------------------------
    // GET /api/users
    // --------------------------------------------------
    @Test
    void getAllUsers_success() throws Exception {

        List<UserResponseDTO> users = List.of(
                new UserResponseDTO(1L, "Nitesh", "nitesh@test.com")
        );

        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Users fetched successfully"))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].name").value("Nitesh"))
                .andExpect(jsonPath("$.data[0].email").value("nitesh@test.com"));
    }

    // --------------------------------------------------
    // POST /api/users
    // --------------------------------------------------
    @Test
    void createUser_success() throws Exception {

        UserRequestDTO request =
                new UserRequestDTO("Nitesh", "nitesh@test.com");

        UserResponseDTO response =
                new UserResponseDTO(1L, "Nitesh", "nitesh@test.com");

        when(userService.createUser(any(UserRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User created successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Nitesh"))
                .andExpect(jsonPath("$.data.email").value("nitesh@test.com"));
    }

    // --------------------------------------------------
    // POST /api/users — validation failure
    // --------------------------------------------------
    @Test
    void createUser_validationError() throws Exception {

        UserRequestDTO request =
                new UserRequestDTO("", "invalid-email");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Name is required"))
                .andExpect(jsonPath("$.email").value("Email should be valid"));
    }

    @Test
    void getAllUsers_emptyList() throws Exception {

        when(userService.getAllUsers()).thenReturn(List.of());

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void createUser_duplicateEmail() throws Exception {

        UserRequestDTO request =
                new UserRequestDTO("Nitesh", "nitesh@test.com");

        when(userService.createUser(any(UserRequestDTO.class)))
                .thenThrow(new RuntimeException("Email already exists"));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void createUser_missingBody() throws Exception {

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Internal server error"));
    }


    @Test
    void createUser_wrongContentType() throws Exception {

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("invalid"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Internal server error"));
    }


    @Test
    void getAllUsers_serviceCalledOnce() throws Exception {

        when(userService.getAllUsers()).thenReturn(List.of());

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk());

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void getAllUsers_responseStructure() throws Exception {

        when(userService.getAllUsers()).thenReturn(List.of());

        mockMvc.perform(get("/api/users"))
                .andExpect(jsonPath("$.success").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data").exists());
    }






}
