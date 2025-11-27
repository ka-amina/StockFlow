package com.example.demo.model;

import com.example.demo.enums.Role;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void builder_ShouldCreateUserWithAllFields() {
        // ACT
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .passwordHash("hashedPassword123")
                .role(Role.ADMIN)
                .active(true)
                .build();

        // ASSERT
        assertEquals(1L, user.getId());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("hashedPassword123", user.getPasswordHash());
        assertEquals(Role.ADMIN, user.getRole());
        assertTrue(user.getActive());
    }

    @Test
    void builder_ShouldSetActiveToTrueByDefault() {
        // ACT
        User user = User.builder()
                .email("test@example.com")
                .passwordHash("hashedPassword")
                .role(Role.CLIENT)
                .build();

        // ASSERT
        assertTrue(user.getActive());
    }

    @Test
    void setters_ShouldUpdateAllFields() {
        // ARRANGE
        User user = new User();

        // ACT
        user.setId(2L);
        user.setEmail("updated@example.com");
        user.setPasswordHash("newHashedPassword");
        user.setRole(Role.WAREHOUSE_MANAGER);
        user.setActive(false);

        // ASSERT
        assertEquals(2L, user.getId());
        assertEquals("updated@example.com", user.getEmail());
        assertEquals("newHashedPassword", user.getPasswordHash());
        assertEquals(Role.WAREHOUSE_MANAGER, user.getRole());
        assertFalse(user.getActive());
    }

    @Test
    void noArgsConstructor_ShouldCreateEmptyUser() {
        // ACT
        User user = new User();

        // ASSERT
        assertNotNull(user);
        assertNull(user.getId());
        assertNull(user.getEmail());
        assertNull(user.getPasswordHash());
        assertNull(user.getRole());
    }

    @Test
    void allArgsConstructor_ShouldCreateUserWithAllFields() {
        // ACT
        User user = new User(1L, "test@example.com", "hashedPass", Role.ADMIN, true);

        // ASSERT
        assertEquals(1L, user.getId());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("hashedPass", user.getPasswordHash());
        assertEquals(Role.ADMIN, user.getRole());
        assertTrue(user.getActive());
    }
}
