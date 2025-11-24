package com.example.demo.service;

import com.example.demo.dto.UserDTO;
import com.example.demo.enums.Role;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;

    @InjectMocks private UserService userService;

    @Test
    void createUser_Success() {
        // Given
        UserDTO request = new UserDTO();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setRole(Role.CLIENT);

        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        UserDTO result = userService.createUser(request);

        // Then
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_DuplicateEmail() {
        // Given
        UserDTO request = new UserDTO();
        request.setEmail("existing@example.com");

        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        // When & Then
        assertThrows(ResponseStatusException.class, () ->
                userService.createUser(request));
    }

    @Test
    void getUserById_Success() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setRole(Role.CLIENT);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When
        UserDTO result = userService.getUserById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getUserById_NotFound() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResponseStatusException.class, () ->
                userService.getUserById(999L));
    }

    @Test
    void getUsersByRole_Success() {
        // Given
        User user1 = new User();
        user1.setRole(Role.ADMIN);
        User user2 = new User();
        user2.setRole(Role.ADMIN);

        when(userRepository.findByRole(Role.ADMIN)).thenReturn(List.of(user1, user2));

        // When
        List<UserDTO> result = userService.getUsersByRole(Role.ADMIN);

        // Then
        assertEquals(2, result.size());
    }

    @Test
    void deactivateUser_Success() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setActive(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When
        userService.deactivateUser(1L);

        // Then
        assertFalse(user.getActive());
        verify(userRepository).save(user);
    }
}
