package com.example.demo.dto;

import com.example.demo.enums.Role;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void builder_ShouldCreateUserDTO() {
        // ACT
        UserDTO user = UserDTO.builder()
                .id(1L)
                .email("user@example.com")
                .password("password123")
                .role(Role.CLIENT)
                .active(true)
                .build();

        // ASSERT
        assertEquals(1L, user.getId());
        assertEquals("user@example.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertEquals(Role.CLIENT, user.getRole());
        assertTrue(user.getActive());
    }

    @Test
    void validation_WithValidData_ShouldPass() {
        // ARRANGE
        UserDTO user = UserDTO.builder()
                .email("user@example.com")
                .password("password123")
                .role(Role.ADMIN)
                .build();

        // ACT
        Set<ConstraintViolation<UserDTO>> violations = validator.validate(user);

        // ASSERT
        assertTrue(violations.isEmpty());
    }

    @Test
    void validation_WithBlankEmail_ShouldFail() {
        // ARRANGE
        UserDTO user = UserDTO.builder()
                .email("")
                .password("password123")
                .role(Role.CLIENT)
                .build();

        // ACT
        Set<ConstraintViolation<UserDTO>> violations = validator.validate(user);

        // ASSERT
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Email is required")));
    }

    @Test
    void validation_WithInvalidEmail_ShouldFail() {
        // ARRANGE
        UserDTO user = UserDTO.builder()
                .email("invalid-email")
                .password("password123")
                .role(Role.CLIENT)
                .build();

        // ACT
        Set<ConstraintViolation<UserDTO>> violations = validator.validate(user);

        // ASSERT
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Email must be valid")));
    }

    @Test
    void validation_WithBlankPassword_ShouldFail() {
        // ARRANGE
        UserDTO user = UserDTO.builder()
                .email("user@example.com")
                .password("")
                .role(Role.CLIENT)
                .build();

        // ACT
        Set<ConstraintViolation<UserDTO>> violations = validator.validate(user);

        // ASSERT
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Password is required")));
    }

    @Test
    void validation_WithNullRole_ShouldFail() {
        // ARRANGE
        UserDTO user = UserDTO.builder()
                .email("user@example.com")
                .password("password123")
                .role(null)
                .build();

        // ACT
        Set<ConstraintViolation<UserDTO>> violations = validator.validate(user);

        // ASSERT
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Role is required")));
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        // ARRANGE
        UserDTO user = new UserDTO();

        // ACT
        user.setId(2L);
        user.setEmail("admin@example.com");
        user.setPassword("securePassword");
        user.setRole(Role.ADMIN);
        user.setActive(false);

        // ASSERT
        assertEquals(2L, user.getId());
        assertEquals("admin@example.com", user.getEmail());
        assertEquals("securePassword", user.getPassword());
        assertEquals(Role.ADMIN, user.getRole());
        assertFalse(user.getActive());
    }

    @Test
    void equals_ShouldWorkCorrectly() {
        // ARRANGE
        UserDTO user1 = UserDTO.builder()
                .id(1L)
                .email("user@example.com")
                .password("password123")
                .role(Role.CLIENT)
                .build();

        UserDTO user2 = UserDTO.builder()
                .id(1L)
                .email("user@example.com")
                .password("password123")
                .role(Role.CLIENT)
                .build();

        UserDTO user3 = UserDTO.builder()
                .id(2L)
                .email("different@example.com")
                .password("password123")
                .role(Role.ADMIN)
                .build();

        // ASSERT
        assertEquals(user1, user2);
        assertNotEquals(user1, user3);
    }

    @Test
    void hashCode_ShouldWorkCorrectly() {
        // ARRANGE
        UserDTO user1 = UserDTO.builder()
                .id(1L)
                .email("user@example.com")
                .password("password123")
                .role(Role.CLIENT)
                .build();

        UserDTO user2 = UserDTO.builder()
                .id(1L)
                .email("user@example.com")
                .password("password123")
                .role(Role.CLIENT)
                .build();

        // ASSERT
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void toString_ShouldContainAllFields() {
        // ARRANGE
        UserDTO user = UserDTO.builder()
                .id(1L)
                .email("user@example.com")
                .password("password123")
                .role(Role.CLIENT)
                .build();

        // ACT
        String result = user.toString();

        // ASSERT
        assertTrue(result.contains("user@example.com"));
        assertTrue(result.contains("CLIENT"));
    }

    @Test
    void allArgsConstructor_ShouldCreateUserDTO() {
        // ACT
        UserDTO user = new UserDTO(1L, "user@example.com", "password", Role.CLIENT, true);

        // ASSERT
        assertEquals(1L, user.getId());
        assertEquals("user@example.com", user.getEmail());
        assertEquals("password", user.getPassword());
        assertEquals(Role.CLIENT, user.getRole());
        assertTrue(user.getActive());
    }

    @Test
    void noArgsConstructor_ShouldCreateEmptyUserDTO() {
        // ACT
        UserDTO user = new UserDTO();

        // ASSERT
        assertNull(user.getId());
        assertNull(user.getEmail());
        assertNull(user.getPassword());
        assertNull(user.getRole());
        assertNull(user.getActive());
    }
}
