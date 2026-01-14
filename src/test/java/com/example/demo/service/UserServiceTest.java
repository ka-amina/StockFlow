package com.example.demo.service;

import com.example.demo.dto.UserDTO;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.UserRoleRepository;
import com.example.demo.mapper.UsersMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private UserRoleRepository userRoleRepository;
    @Mock private UsersMapper usersMapper;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private UserService userService;

    @Test
    void registerUser_Success() {
        // Given
        UserDTO request = new UserDTO();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setRole(1L);

        Role role = new Role();
        role.setId(1L);
        role.setRoleName("CLIENT");

        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setRole(role);

        UserDTO expectedDTO = new UserDTO();
        expectedDTO.setId(1L);
        expectedDTO.setEmail("test@example.com");
        expectedDTO.setRole(1L);

        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(usersMapper.toEntity(request)).thenReturn(user);
        when(userRoleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(usersMapper.toDto(user)).thenReturn(expectedDTO);

        // When
        UserDTO result = userService.registerUser(request);

        // Then
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_DuplicateEmail() {
        // Given
        UserDTO request = new UserDTO();
        request.setEmail("existing@example.com");

        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        // When & Then
        assertThrows(ResponseStatusException.class, () ->
                userService.registerUser(request));
    }

    @Test
    void getUserById_Success() {
        // Given
        Role role = new Role();
        role.setId(1L);
        role.setRoleName("CLIENT");

        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setRole(role);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When
        User result = userService.getUserById(1L);

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
}
