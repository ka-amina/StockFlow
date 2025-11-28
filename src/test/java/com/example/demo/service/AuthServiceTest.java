package com.example.demo.service;

import com.example.demo.dto.AuthResponseDTO;
import com.example.demo.dto.LoginDTO;
import com.example.demo.enums.Role;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private HttpSession session;

    @InjectMocks private AuthService authService;

    @Test
    void login_Success() {
        // Given
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("test@example.com");
        loginDTO.setPassword("password123");

        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPasswordHash("password123");
        user.setRole(Role.CLIENT);
        user.setActive(true);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // When
        AuthResponseDTO result = authService.login(loginDTO, session);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals("test@example.com", result.getEmail());
        assertEquals(Role.CLIENT, result.getRole());
        verify(session).setAttribute("authenticated_user", 1L);
    }

    @Test
    void login_InvalidEmail() {
        // Given
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("nonexistent@example.com");
        loginDTO.setPassword("password123");

        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResponseStatusException.class, () ->
                authService.login(loginDTO, session));
    }

    @Test
    void login_InvalidPassword() {
        // Given
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("test@example.com");
        loginDTO.setPassword("wrongpassword");

        User user = new User();
        user.setPasswordHash("password123");
        user.setActive(true);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // When & Then
        assertThrows(ResponseStatusException.class, () ->
                authService.login(loginDTO, session));
    }

    @Test
    void login_InactiveUser() {
        // Given
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("test@example.com");
        loginDTO.setPassword("password123");

        User user = new User();
        user.setPasswordHash("password123");
        user.setActive(false);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // When & Then
        assertThrows(ResponseStatusException.class, () ->
                authService.login(loginDTO, session));
    }

    @Test
    void logout_Success() {
        // When
        authService.logout(session);

        // Then
        verify(session).invalidate();
    }

    @Test
    void getCurrentUser_Success() {
        // Given
        User user = new User();
        user.setId(1L);

        when(session.getAttribute("authenticated_user")).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When
        User result = authService.getCurrentUser(session);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getCurrentUser_NotAuthenticated() {
        // Given
        when(session.getAttribute("authenticated_user")).thenReturn(null);

        // When & Then
        assertThrows(ResponseStatusException.class, () ->
                authService.getCurrentUser(session));
    }

    @Test
    void isAuthenticated_True() {
        // Given
        when(session.getAttribute("authenticated_user")).thenReturn(1L);

        // When
        boolean result = authService.isAuthenticated(session);

        // Then
        assertTrue(result);
    }

    @Test
    void isAuthenticated_False() {
        // Given
        when(session.getAttribute("authenticated_user")).thenReturn(null);

        // When
        boolean result = authService.isAuthenticated(session);

        // Then
        assertFalse(result);
    }

    @Test
    void getCurrentUser_UserNotFoundAfterAuth() {
        when(session.getAttribute("authenticated_user")).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> authService.getCurrentUser(session));
    }

    @Test
    void login_NullPassword() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("test@example.com");
        loginDTO.setPassword(null);

        User user = new User();
        user.setPasswordHash("password123");
        user.setActive(true);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        assertThrows(ResponseStatusException.class,
                () -> authService.login(loginDTO, session));
    }
}
