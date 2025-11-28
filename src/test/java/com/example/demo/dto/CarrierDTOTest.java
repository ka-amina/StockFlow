package com.example.demo.dto;

import com.example.demo.enums.CarrierStatus;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CarrierDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void builder_ShouldCreateCarrierDTO() {
        // ARRANGE
        LocalDateTime now = LocalDateTime.now();

        // ACT
        CarrierDTO carrier = CarrierDTO.builder()
                .id(1L)
                .code("CARR-001")
                .name("Test Carrier")
                .contactInfo("contact@carrier.com")
                .status(CarrierStatus.ACTIVE)
                .createdAt(now)
                .build();

        // ASSERT
        assertEquals(1L, carrier.getId());
        assertEquals("CARR-001", carrier.getCode());
        assertEquals("Test Carrier", carrier.getName());
        assertEquals("contact@carrier.com", carrier.getContactInfo());
        assertEquals(CarrierStatus.ACTIVE, carrier.getStatus());
        assertEquals(now, carrier.getCreatedAt());
    }

    @Test
    void validation_WithValidData_ShouldPass() {
        // ARRANGE
        CarrierDTO carrier = CarrierDTO.builder()
                .code("CARR-001")
                .name("Test Carrier")
                .status(CarrierStatus.ACTIVE)
                .build();

        // ACT
        Set<ConstraintViolation<CarrierDTO>> violations = validator.validate(carrier);

        // ASSERT
        assertTrue(violations.isEmpty());
    }

    @Test
    void validation_WithBlankCode_ShouldFail() {
        // ARRANGE
        CarrierDTO carrier = CarrierDTO.builder()
                .code("")
                .name("Test Carrier")
                .status(CarrierStatus.ACTIVE)
                .build();

        // ACT
        Set<ConstraintViolation<CarrierDTO>> violations = validator.validate(carrier);

        // ASSERT
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Carrier code is required")));
    }

    @Test
    void validation_WithBlankName_ShouldFail() {
        // ARRANGE
        CarrierDTO carrier = CarrierDTO.builder()
                .code("CARR-001")
                .name("")
                .status(CarrierStatus.ACTIVE)
                .build();

        // ACT
        Set<ConstraintViolation<CarrierDTO>> violations = validator.validate(carrier);

        // ASSERT
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Carrier name is required")));
    }

    @Test
    void validation_WithNullStatus_ShouldFail() {
        // ARRANGE
        CarrierDTO carrier = CarrierDTO.builder()
                .code("CARR-001")
                .name("Test Carrier")
                .status(null)
                .build();

        // ACT
        Set<ConstraintViolation<CarrierDTO>> violations = validator.validate(carrier);

        // ASSERT
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Status is required")));
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        // ARRANGE
        CarrierDTO carrier = new CarrierDTO();
        LocalDateTime now = LocalDateTime.now();

        // ACT
        carrier.setId(1L);
        carrier.setCode("CARR-002");
        carrier.setName("Carrier Name");
        carrier.setContactInfo("info@carrier.com");
        carrier.setStatus(CarrierStatus.INACTIVE);
        carrier.setCreatedAt(now);

        // ASSERT
        assertEquals(1L, carrier.getId());
        assertEquals("CARR-002", carrier.getCode());
        assertEquals("Carrier Name", carrier.getName());
        assertEquals("info@carrier.com", carrier.getContactInfo());
        assertEquals(CarrierStatus.INACTIVE, carrier.getStatus());
        assertEquals(now, carrier.getCreatedAt());
    }

    @Test
    void equals_ShouldWorkCorrectly() {
        // ARRANGE
        CarrierDTO carrier1 = CarrierDTO.builder()
                .id(1L)
                .code("CARR-001")
                .name("Carrier")
                .status(CarrierStatus.ACTIVE)
                .build();

        CarrierDTO carrier2 = CarrierDTO.builder()
                .id(1L)
                .code("CARR-001")
                .name("Carrier")
                .status(CarrierStatus.ACTIVE)
                .build();

        CarrierDTO carrier3 = CarrierDTO.builder()
                .id(2L)
                .code("CARR-002")
                .name("Different")
                .status(CarrierStatus.INACTIVE)
                .build();

        // ASSERT
        assertEquals(carrier1, carrier2);
        assertNotEquals(carrier1, carrier3);
    }

    @Test
    void hashCode_ShouldWorkCorrectly() {
        // ARRANGE
        CarrierDTO carrier1 = CarrierDTO.builder()
                .id(1L)
                .code("CARR-001")
                .name("Carrier")
                .status(CarrierStatus.ACTIVE)
                .build();

        CarrierDTO carrier2 = CarrierDTO.builder()
                .id(1L)
                .code("CARR-001")
                .name("Carrier")
                .status(CarrierStatus.ACTIVE)
                .build();

        // ASSERT
        assertEquals(carrier1.hashCode(), carrier2.hashCode());
    }

    @Test
    void toString_ShouldContainAllFields() {
        // ARRANGE
        CarrierDTO carrier = CarrierDTO.builder()
                .id(1L)
                .code("CARR-001")
                .name("Test Carrier")
                .status(CarrierStatus.ACTIVE)
                .build();

        // ACT
        String result = carrier.toString();

        // ASSERT
        assertTrue(result.contains("CARR-001"));
        assertTrue(result.contains("Test Carrier"));
        assertTrue(result.contains("ACTIVE"));
    }

    @Test
    void allArgsConstructor_ShouldCreateCarrierDTO() {
        // ARRANGE
        LocalDateTime now = LocalDateTime.now();

        // ACT
        CarrierDTO carrier = new CarrierDTO(1L, "CARR-001", "Carrier", "contact", CarrierStatus.ACTIVE, now);

        // ASSERT
        assertEquals(1L, carrier.getId());
        assertEquals("CARR-001", carrier.getCode());
        assertEquals("Carrier", carrier.getName());
        assertEquals("contact", carrier.getContactInfo());
        assertEquals(CarrierStatus.ACTIVE, carrier.getStatus());
        assertEquals(now, carrier.getCreatedAt());
    }

    @Test
    void noArgsConstructor_ShouldCreateEmptyCarrierDTO() {
        // ACT
        CarrierDTO carrier = new CarrierDTO();

        // ASSERT
        assertNull(carrier.getId());
        assertNull(carrier.getCode());
        assertNull(carrier.getName());
        assertNull(carrier.getContactInfo());
        assertNull(carrier.getStatus());
        assertNull(carrier.getCreatedAt());
    }
}
