package com.nitesh.usermanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nitesh.usermanagement.dto.UserRequestDTO;
import com.nitesh.usermanagement.dto.UserResponseDTO;
import com.nitesh.usermanagement.exception.ResourceNotFoundException;
import com.nitesh.usermanagement.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

        when(userService.getAllUsers())
                .thenReturn(List.of(new UserResponseDTO(1L, "Nitesh", "n@test.com")));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value(1));
    }

    @Test
    void getAllUsers_emptyList() throws Exception {

        when(userService.getAllUsers()).thenReturn(List.of());

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void getAllUsers_serviceCalledOnce() throws Exception {

        when(userService.getAllUsers()).thenReturn(List.of());

        mockMvc.perform(get("/api/users")).andExpect(status().isOk());

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

    // --------------------------------------------------
    // POST /api/users
    // --------------------------------------------------
    @Test
    void createUser_success() throws Exception {

        UserRequestDTO request = new UserRequestDTO("Nitesh", "n@test.com");

        when(userService.createUser(any()))
                .thenReturn(new UserResponseDTO(1L, "Nitesh", "n@test.com"));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void createUser_validationError() throws Exception {

        UserRequestDTO request = new UserRequestDTO("", "bad-email");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.email").exists());
    }

    @Test
    void createUser_duplicateEmail() throws Exception {

        when(userService.createUser(any()))
                .thenThrow(new RuntimeException("Email exists"));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserRequestDTO("A", "a@test.com"))))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void createUser_missingBody() throws Exception {

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void createUser_wrongContentType() throws Exception {

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("bad"))
                .andExpect(status().isInternalServerError());
    }

    // --------------------------------------------------
    // GET /api/users/{id}
    // --------------------------------------------------
    @Test
    void getUserById_success() throws Exception {

        when(userService.getUserById(1L))
                .thenReturn(new UserResponseDTO(1L, "A", "a@test.com"));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void getUserById_notFound() throws Exception {

        when(userService.getUserById(99L))
                .thenThrow(new ResourceNotFoundException("Not found"));

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound());
    }

    // --------------------------------------------------
    // PUT /api/users/{id}
    // --------------------------------------------------
    @Test
    void updateUser_success() throws Exception {

        when(userService.updateUser(eq(1L), any()))
                .thenReturn(new UserResponseDTO(1L, "New", "new@test.com"));

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserRequestDTO("New", "new@test.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("New"));
    }

    @Test
    void updateUser_notFound() throws Exception {

        when(userService.updateUser(eq(1L), any()))
                .thenThrow(new ResourceNotFoundException("Not found"));

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserRequestDTO("X", "x@test.com"))))
                .andExpect(status().isNotFound());
    }

    // --------------------------------------------------
    // DELETE /api/users/{id}
    // --------------------------------------------------
    @Test
    void deleteUser_success() throws Exception {

        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(1L);
    }

    @Test
    void deleteUser_notFound() throws Exception {

        doThrow(new ResourceNotFoundException("Not found"))
                .when(userService).deleteUser(99L);

        mockMvc.perform(delete("/api/users/99"))
                .andExpect(status().isNotFound());
    }

    // --------------------------------------------------
    // GET /api/users/page
    // --------------------------------------------------
    @Test
    void getUsersPaginated_success() throws Exception {

        Page<UserResponseDTO> page =
                new PageImpl<>(List.of(
                        new UserResponseDTO(1L, "A", "a@test.com")));

        when(userService.getUsersPaginated(
                anyInt(),
                anyInt(),
                anyString(),
                anyString()
        )).thenReturn(page);

        mockMvc.perform(get("/api/users/page")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sortBy", "id")
                        .param("direction", "ASC")) // 👈 IMPORTANT
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content.length()").value(1));
    }



    // --------------------------------------------------
    // GET /api/users/search
    // --------------------------------------------------
    @Test
    void searchUsers_byName() throws Exception {

        when(userService.searchUsers("Nit", null))
                .thenReturn(List.of(
                        new UserResponseDTO(1L, "Nitesh", "n@test.com")));

        mockMvc.perform(get("/api/users/search")
                        .param("name", "Nit"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    void searchUsers_byEmail() throws Exception {

        when(userService.searchUsers(null, "gmail"))
                .thenReturn(List.of(
                        new UserResponseDTO(1L, "A", "a@gmail.com")));

        mockMvc.perform(get("/api/users/search")
                        .param("email", "gmail"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    void searchUsers_noParams() throws Exception {

        when(userService.searchUsers(null, null)).thenReturn(List.of());

        mockMvc.perform(get("/api/users/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }
}
