package com.nitesh.usermanagement.service;

import com.nitesh.usermanagement.dto.UserRequestDTO;
import com.nitesh.usermanagement.dto.UserResponseDTO;
import com.nitesh.usermanagement.exception.ResourceNotFoundException;
import com.nitesh.usermanagement.model.User;
import com.nitesh.usermanagement.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    // ---------- CREATE ----------
    @Test
    void createUser_success() {
        UserRequestDTO dto = new UserRequestDTO("Nitesh", "nitesh@gmail.com");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("Nitesh");
        savedUser.setEmail("nitesh@gmail.com");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponseDTO response = userService.createUser(dto);

        assertEquals(1L, response.getId());
        assertEquals("Nitesh", response.getName());
        assertEquals("nitesh@gmail.com", response.getEmail());
    }

    // ---------- GET BY ID ----------
    @Test
    void getUserById_success() {
        User user = new User();
        user.setId(1L);
        user.setName("Nitesh");
        user.setEmail("nitesh@gmail.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponseDTO response = userService.getUserById(1L);

        assertEquals("Nitesh", response.getName());
    }

    @Test
    void getUserById_notFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.getUserById(99L));
    }

    // ---------- GET ALL ----------
    @Test
    void getAllUsers_success() {
        User u1 = new User();
        u1.setId(1L);
        u1.setName("A");
        u1.setEmail("a@gmail.com");

        User u2 = new User();
        u2.setId(2L);
        u2.setName("B");
        u2.setEmail("b@gmail.com");

        when(userRepository.findAll()).thenReturn(List.of(u1, u2));

        List<UserResponseDTO> users = userService.getAllUsers();

        assertEquals(2, users.size());
    }

    @Test
    void getAllUsers_empty() {
        when(userRepository.findAll()).thenReturn(List.of());
        assertTrue(userService.getAllUsers().isEmpty());
    }

    // ---------- UPDATE ----------
    @Test
    void updateUser_success() {
        User user = new User();
        user.setId(1L);
        user.setName("Old");
        user.setEmail("old@gmail.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserRequestDTO dto = new UserRequestDTO("New", "new@gmail.com");

        UserResponseDTO response = userService.updateUser(1L, dto);

        assertEquals("New", response.getName());
        assertEquals("new@gmail.com", response.getEmail());
    }

    @Test
    void updateUser_notFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.updateUser(1L, new UserRequestDTO("X", "x@gmail.com")));
    }

    // ---------- DELETE ----------
    @Test
    void deleteUser_success() {
        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_notFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.deleteUser(1L));
    }

    // ---------- PAGINATION ----------
    @Test
    void getUsersPaginated_success() {
        User user = new User();
        user.setId(1L);
        user.setName("N");
        user.setEmail("n@gmail.com");

        Page<User> page = new PageImpl<>(List.of(user));

        when(userRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<UserResponseDTO> result =
                userService.getUsersPaginated(0, 5, "id", "asc");

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getUsersPaginated_empty() {
        when(userRepository.findAll(any(Pageable.class)))
                .thenReturn(Page.empty());

        Page<UserResponseDTO> result =
                userService.getUsersPaginated(0, 5, "id", "asc");

        assertTrue(result.isEmpty());
    }

    // ---------- SEARCH ----------
    @Test
    void searchUsers_byName() {
        User user = new User();
        user.setId(1L);
        user.setName("Nitesh");
        user.setEmail("n@gmail.com");

        when(userRepository.findByNameContainingIgnoreCase("Nit"))
                .thenReturn(List.of(user));

        assertEquals(1, userService.searchUsers("Nit", null).size());
    }

    @Test
    void searchUsers_byEmail() {
        User user = new User();
        user.setId(1L);
        user.setName("N");
        user.setEmail("n@gmail.com");

        when(userRepository.findByEmailContainingIgnoreCase("gmail"))
                .thenReturn(List.of(user));

        assertEquals(1, userService.searchUsers(null, "gmail").size());
    }

    @Test
    void searchUsers_noParams() {
        assertTrue(userService.searchUsers(null, null).isEmpty());
    }
}
