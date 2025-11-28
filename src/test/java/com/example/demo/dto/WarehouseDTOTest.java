package com.example.demo.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class WarehouseDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void builder_ShouldCreateWarehouseDTO() {
        // ACT
        WarehouseDTO warehouse = WarehouseDTO.builder()
                .id(1L)
                .code("WH-001")
                .name("Main Warehouse")
                .active(true)
                .build();

        // ASSERT
        assertEquals(1L, warehouse.getId());
        assertEquals("WH-001", warehouse.getCode());
        assertEquals("Main Warehouse", warehouse.getName());
        assertTrue(warehouse.isActive());
    }

    @Test
    void defaultActive_ShouldBeTrue() {
        // ACT
        WarehouseDTO warehouse = WarehouseDTO.builder()
                .code("WH-001")
                .name("Main Warehouse")
                .build();

        // ASSERT
        assertTrue(warehouse.isActive());
    }

    @Test
    void validation_WithValidData_ShouldPass() {
        // ARRANGE
        WarehouseDTO warehouse = WarehouseDTO.builder()
                .code("WH-001")
                .name("Main Warehouse")
                .build();

        // ACT
        Set<ConstraintViolation<WarehouseDTO>> violations = validator.validate(warehouse);

        // ASSERT
        assertTrue(violations.isEmpty());
    }

    @Test
    void validation_WithBlankCode_ShouldFail() {
        // ARRANGE
        WarehouseDTO warehouse = WarehouseDTO.builder()
                .code("")
                .name("Main Warehouse")
                .build();

        // ACT
        Set<ConstraintViolation<WarehouseDTO>> violations = validator.validate(warehouse);

        // ASSERT
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Code must not be blank")));
    }

    @Test
    void validation_WithBlankName_ShouldFail() {
        // ARRANGE
        WarehouseDTO warehouse = WarehouseDTO.builder()
                .code("WH-001")
                .name("")
                .build();

        // ACT
        Set<ConstraintViolation<WarehouseDTO>> violations = validator.validate(warehouse);

        // ASSERT
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Name must not be blank")));
    }

    @Test
    void validation_WithNullCode_ShouldFail() {
        // ARRANGE
        WarehouseDTO warehouse = WarehouseDTO.builder()
                .code(null)
                .name("Main Warehouse")
                .build();

        // ACT
        Set<ConstraintViolation<WarehouseDTO>> violations = validator.validate(warehouse);

        // ASSERT
        assertFalse(violations.isEmpty());
    }

    @Test
    void validation_WithNullName_ShouldFail() {
        // ARRANGE
        WarehouseDTO warehouse = WarehouseDTO.builder()
                .code("WH-001")
                .name(null)
                .build();

        // ACT
        Set<ConstraintViolation<WarehouseDTO>> violations = validator.validate(warehouse);

        // ASSERT
        assertFalse(violations.isEmpty());
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        // ARRANGE
        WarehouseDTO warehouse = new WarehouseDTO();

        // ACT
        warehouse.setId(2L);
        warehouse.setCode("WH-002");
        warehouse.setName("Secondary Warehouse");
        warehouse.setActive(false);

        // ASSERT
        assertEquals(2L, warehouse.getId());
        assertEquals("WH-002", warehouse.getCode());
        assertEquals("Secondary Warehouse", warehouse.getName());
        assertFalse(warehouse.isActive());
    }

    @Test
    void equals_ShouldWorkCorrectly() {
        // ARRANGE
        WarehouseDTO warehouse1 = WarehouseDTO.builder()
                .id(1L)
                .code("WH-001")
                .name("Warehouse")
                .active(true)
                .build();

        WarehouseDTO warehouse2 = WarehouseDTO.builder()
                .id(1L)
                .code("WH-001")
                .name("Warehouse")
                .active(true)
                .build();

        WarehouseDTO warehouse3 = WarehouseDTO.builder()
                .id(2L)
                .code("WH-002")
                .name("Different")
                .active(false)
                .build();

        // ASSERT
        assertEquals(warehouse1, warehouse2);
        assertNotEquals(warehouse1, warehouse3);
    }

    @Test
    void hashCode_ShouldWorkCorrectly() {
        // ARRANGE
        WarehouseDTO warehouse1 = WarehouseDTO.builder()
                .id(1L)
                .code("WH-001")
                .name("Warehouse")
                .build();

        WarehouseDTO warehouse2 = WarehouseDTO.builder()
                .id(1L)
                .code("WH-001")
                .name("Warehouse")
                .build();

        // ASSERT
        assertEquals(warehouse1.hashCode(), warehouse2.hashCode());
    }

    @Test
    void toString_ShouldContainAllFields() {
        // ARRANGE
        WarehouseDTO warehouse = WarehouseDTO.builder()
                .id(1L)
                .code("WH-001")
                .name("Main Warehouse")
                .active(true)
                .build();

        // ACT
        String result = warehouse.toString();

        // ASSERT
        assertTrue(result.contains("WH-001"));
        assertTrue(result.contains("Main Warehouse"));
    }

    @Test
    void allArgsConstructor_ShouldCreateWarehouseDTO() {
        // ACT
        WarehouseDTO warehouse = new WarehouseDTO(1L, "WH-001", "Warehouse", true);

        // ASSERT
        assertEquals(1L, warehouse.getId());
        assertEquals("WH-001", warehouse.getCode());
        assertEquals("Warehouse", warehouse.getName());
        assertTrue(warehouse.isActive());
    }

    @Test
    void noArgsConstructor_ShouldCreateEmptyWarehouseDTO() {
        // ACT
        WarehouseDTO warehouse = new WarehouseDTO();

        // ASSERT
        assertNull(warehouse.getId());
        assertNull(warehouse.getCode());
        assertNull(warehouse.getName());
        assertTrue(warehouse.isActive()); // Default value is true from @Builder.Default
    }
}
