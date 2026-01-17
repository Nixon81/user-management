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
import static org.mockito.ArgumentMatchers.any;
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

        verify(userRepository, times(1)).save(any(User.class));
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

        assertEquals(1L, response.getId());
        assertEquals("Nitesh", response.getName());
        assertEquals("nitesh@gmail.com", response.getEmail());

        verify(userRepository).findById(1L);
    }

    @Test
    void searchUsers_bothParamsProvided_returnsEmpty() {
        // both name & email provided → current service logic returns empty
        assertTrue(userService.searchUsers("test", "gmail").isEmpty());
    }


    @Test
    void getUserById_notFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.getUserById(99L));

        verify(userRepository).findById(99L);
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

        verify(userRepository).findAll();
    }

    @Test
    void getAllUsers_empty() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserResponseDTO> users = userService.getAllUsers();

        assertTrue(users.isEmpty());

        verify(userRepository).findAll();
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

        verify(userRepository).findById(1L);
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_notFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.updateUser(1L, new UserRequestDTO("X", "x@gmail.com")));

        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any());
    }

    // ---------- DELETE ----------
    @Test
    void deleteUser_success() {
        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository).findById(1L);
        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_notFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.deleteUser(1L));

        verify(userRepository).findById(1L);
        verify(userRepository, never()).delete(any());
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

        verify(userRepository).findAll(any(Pageable.class));
    }

    @Test
    void getUsersPaginated_empty() {
        when(userRepository.findAll(any(Pageable.class)))
                .thenReturn(Page.empty());

        Page<UserResponseDTO> result =
                userService.getUsersPaginated(0, 5, "id", "asc");

        assertTrue(result.isEmpty());

        verify(userRepository).findAll(any(Pageable.class));
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

        List<UserResponseDTO> result = userService.searchUsers("Nit", null);

        assertEquals(1, result.size());

        verify(userRepository).findByNameContainingIgnoreCase("Nit");
    }

    @Test
    void searchUsers_byEmail() {
        User user = new User();
        user.setId(1L);
        user.setName("N");
        user.setEmail("n@gmail.com");

        when(userRepository.findByEmailContainingIgnoreCase("gmail"))
                .thenReturn(List.of(user));

        List<UserResponseDTO> result = userService.searchUsers(null, "gmail");

        assertEquals(1, result.size());

        verify(userRepository).findByEmailContainingIgnoreCase("gmail");
    }

    @Test
    void searchUsers_noParams() {
        List<UserResponseDTO> result = userService.searchUsers(null, null);

        assertTrue(result.isEmpty());

        verifyNoInteractions(userRepository);
    }
}
