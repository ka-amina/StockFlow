package com.example.demo.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class LoginDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void builder_ShouldCreateLoginDTO() {
        // ACT
        LoginDTO loginDTO = LoginDTO.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        // ASSERT
        assertEquals("test@example.com", loginDTO.getEmail());
        assertEquals("password123", loginDTO.getPassword());
    }

    @Test
    void validation_WithValidData_ShouldPass() {
        // ARRANGE
        LoginDTO loginDTO = LoginDTO.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        // ACT
        Set<ConstraintViolation<LoginDTO>> violations = validator.validate(loginDTO);

        // ASSERT
        assertTrue(violations.isEmpty());
    }

    @Test
    void validation_WithBlankEmail_ShouldFail() {
        // ARRANGE
        LoginDTO loginDTO = LoginDTO.builder()
                .email("")
                .password("password123")
                .build();

        // ACT
        Set<ConstraintViolation<LoginDTO>> violations = validator.validate(loginDTO);

        // ASSERT
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Email is required")));
    }

    @Test
    void validation_WithInvalidEmail_ShouldFail() {
        // ARRANGE
        LoginDTO loginDTO = LoginDTO.builder()
                .email("invalid-email")
                .password("password123")
                .build();

        // ACT
        Set<ConstraintViolation<LoginDTO>> violations = validator.validate(loginDTO);

        // ASSERT
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Email must be valid")));
    }

    @Test
    void validation_WithBlankPassword_ShouldFail() {
        // ARRANGE
        LoginDTO loginDTO = LoginDTO.builder()
                .email("test@example.com")
                .password("")
                .build();

        // ACT
        Set<ConstraintViolation<LoginDTO>> violations = validator.validate(loginDTO);

        // ASSERT
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Password is required")));
    }

    @Test
    void validation_WithNullEmail_ShouldFail() {
        // ARRANGE
        LoginDTO loginDTO = LoginDTO.builder()
                .email(null)
                .password("password123")
                .build();

        // ACT
        Set<ConstraintViolation<LoginDTO>> violations = validator.validate(loginDTO);

        // ASSERT
        assertFalse(violations.isEmpty());
    }

    @Test
    void validation_WithNullPassword_ShouldFail() {
        // ARRANGE
        LoginDTO loginDTO = LoginDTO.builder()
                .email("test@example.com")
                .password(null)
                .build();

        // ACT
        Set<ConstraintViolation<LoginDTO>> violations = validator.validate(loginDTO);

        // ASSERT
        assertFalse(violations.isEmpty());
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        // ARRANGE
        LoginDTO loginDTO = new LoginDTO();

        // ACT
        loginDTO.setEmail("user@example.com");
        loginDTO.setPassword("securePassword");

        // ASSERT
        assertEquals("user@example.com", loginDTO.getEmail());
        assertEquals("securePassword", loginDTO.getPassword());
    }

    @Test
    void equals_ShouldWorkCorrectly() {
        // ARRANGE
        LoginDTO dto1 = LoginDTO.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        LoginDTO dto2 = LoginDTO.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        LoginDTO dto3 = LoginDTO.builder()
                .email("different@example.com")
                .password("password123")
                .build();

        // ASSERT
        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
    }

    @Test
    void hashCode_ShouldWorkCorrectly() {
        // ARRANGE
        LoginDTO dto1 = LoginDTO.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        LoginDTO dto2 = LoginDTO.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        // ASSERT
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void toString_ShouldContainEmail() {
        // ARRANGE
        LoginDTO loginDTO = LoginDTO.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        // ACT
        String result = loginDTO.toString();

        // ASSERT
        assertTrue(result.contains("test@example.com"));
    }

    @Test
    void allArgsConstructor_ShouldCreateLoginDTO() {
        // ACT
        LoginDTO loginDTO = new LoginDTO("test@example.com", "password123");

        // ASSERT
        assertEquals("test@example.com", loginDTO.getEmail());
        assertEquals("password123", loginDTO.getPassword());
    }

    @Test
    void noArgsConstructor_ShouldCreateEmptyLoginDTO() {
        // ACT
        LoginDTO loginDTO = new LoginDTO();

        // ASSERT
        assertNull(loginDTO.getEmail());
        assertNull(loginDTO.getPassword());
    }
}
