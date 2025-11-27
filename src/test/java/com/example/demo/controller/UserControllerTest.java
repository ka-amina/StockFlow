package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.UserDTO;
import com.example.demo.enums.Role;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void createUser_Success() {
        // ARRANGE
        UserDTO requestDto = new UserDTO();
        requestDto.setEmail("test@example.com");
        requestDto.setPassword("password123");
        requestDto.setRole(Role.ADMIN);

        UserDTO createdDto = new UserDTO();
        createdDto.setId(1L);
        createdDto.setEmail("test@example.com");
        createdDto.setRole(Role.ADMIN);
        createdDto.setActive(true);

        when(userService.createUser(any(UserDTO.class))).thenReturn(createdDto);

        // ACT
        ResponseEntity<ApiResponse<UserDTO>> response = userController.createUser(requestDto);

        // ASSERT
        assertNotNull(response);
        assertEquals(201, response.getStatusCodeValue());
        assertEquals("User created successfully", response.getBody().getMessage());
        assertNotNull(response.getBody().getData());
        verify(userService, times(1)).createUser(requestDto);
    }

    @Test
    void getUserById_Success() {
        // ARRANGE
        UserDTO user = new UserDTO();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setRole(Role.ADMIN);
        user.setActive(true);

        when(userService.getUserById(1L)).thenReturn(user);

        // ACT
        ResponseEntity<ApiResponse<UserDTO>> response = userController.getUserById(1L);

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User retrieved successfully", response.getBody().getMessage());
        assertEquals(1L, response.getBody().getData().getId());
        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void getAllUsers_Success() {
        // ARRANGE
        UserDTO user1 = new UserDTO();
        user1.setId(1L);
        user1.setEmail("user1@example.com");
        user1.setRole(Role.ADMIN);

        UserDTO user2 = new UserDTO();
        user2.setId(2L);
        user2.setEmail("user2@example.com");
        user2.setRole(Role.WAREHOUSE_MANAGER);

        List<UserDTO> users = Arrays.asList(user1, user2);
        when(userService.getAllUsers()).thenReturn(users);

        // ACT
        ResponseEntity<ApiResponse<List<UserDTO>>> response = userController.getAllUsers(null);

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("All users retrieved successfully", response.getBody().getMessage());
        assertEquals(2, response.getBody().getData().size());
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void getUsersByRole_Success() {
        // ARRANGE
        UserDTO user1 = new UserDTO();
        user1.setId(1L);
        user1.setEmail("admin@example.com");
        user1.setRole(Role.ADMIN);

        List<UserDTO> admins = Arrays.asList(user1);
        when(userService.getUsersByRole(Role.ADMIN)).thenReturn(admins);

        // ACT
        ResponseEntity<ApiResponse<List<UserDTO>>> response = userController.getAllUsers(Role.ADMIN);

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Users retrieved by role: ADMIN", response.getBody().getMessage());
        assertEquals(1, response.getBody().getData().size());
        assertEquals(Role.ADMIN, response.getBody().getData().get(0).getRole());
        verify(userService, times(1)).getUsersByRole(Role.ADMIN);
    }

    @Test
    void updateUser_Success() {
        // ARRANGE
        UserDTO requestDto = new UserDTO();
        requestDto.setEmail("updated@example.com");
        requestDto.setRole(Role.WAREHOUSE_MANAGER);

        UserDTO updatedDto = new UserDTO();
        updatedDto.setId(1L);
        updatedDto.setEmail("updated@example.com");
        updatedDto.setRole(Role.WAREHOUSE_MANAGER);
        updatedDto.setActive(true);

        when(userService.updateUser(anyLong(), any(UserDTO.class))).thenReturn(updatedDto);

        // ACT
        ResponseEntity<ApiResponse<UserDTO>> response = userController.updateUser(1L, requestDto);

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User updated successfully", response.getBody().getMessage());
        assertEquals("updated@example.com", response.getBody().getData().getEmail());
        verify(userService, times(1)).updateUser(1L, requestDto);
    }

    @Test
    void activateUser_Success() {
        // ARRANGE
        UserDTO user = new UserDTO();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setRole(Role.ADMIN);
        user.setActive(true);

        when(userService.getUserById(1L)).thenReturn(user);

        // ACT
        ResponseEntity<ApiResponse<UserDTO>> response = userController.getUserById(1L);

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().getData().getActive());
        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void deactivateUser_Success() {
        // ARRANGE
        doNothing().when(userService).deactivateUser(1L);

        // ACT
        ResponseEntity<ApiResponse<Void>> response = userController.deactivateUser(1L);

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("User deactivated successfully", response.getBody().getMessage());
        verify(userService, times(1)).deactivateUser(1L);
    }
}
