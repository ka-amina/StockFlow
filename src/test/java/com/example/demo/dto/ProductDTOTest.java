package com.example.demo.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ProductDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void builder_ShouldCreateProductDTO() {
        // ACT
        ProductDTO product = ProductDTO.builder()
                .id(1L)
                .sku("SKU-001")
                .name("Test Product")
                .category("Electronics")
                .active(true)
                .description("Test description")
                .originalPrice(BigDecimal.valueOf(100.00))
                .profit(BigDecimal.valueOf(20.00))
                .build();

        // ASSERT
        assertEquals(1L, product.getId());
        assertEquals("SKU-001", product.getSku());
        assertEquals("Test Product", product.getName());
        assertEquals("Electronics", product.getCategory());
        assertTrue(product.isActive());
        assertEquals("Test description", product.getDescription());
        assertEquals(BigDecimal.valueOf(100.00), product.getOriginalPrice());
        assertEquals(BigDecimal.valueOf(20.00), product.getProfit());
    }

    @Test
    void defaultActive_ShouldBeTrue() {
        // ACT
        ProductDTO product = ProductDTO.builder()
                .sku("SKU-001")
                .name("Test Product")
                .build();

        // ASSERT
        assertTrue(product.isActive());
    }

    @Test
    void validation_WithBlankSku_ShouldFail() {
        // ARRANGE
        ProductDTO product = ProductDTO.builder()
                .sku("")
                .name("Test Product")
                .build();

        // ACT
        Set<ConstraintViolation<ProductDTO>> violations = validator.validate(product);

        // ASSERT
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("SKU must not be blank")));
    }

    @Test
    void validation_WithBlankName_ShouldFail() {
        // ARRANGE
        ProductDTO product = ProductDTO.builder()
                .sku("SKU-001")
                .name("")
                .build();

        // ACT
        Set<ConstraintViolation<ProductDTO>> violations = validator.validate(product);

        // ASSERT
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("Name must not be blank")));
    }

    @Test
    void validation_WithNegativePrice_ShouldFail() {
        // ARRANGE
        ProductDTO product = ProductDTO.builder()
                .sku("SKU-001")
                .name("Test Product")
                .originalPrice(BigDecimal.valueOf(-10.00))
                .build();

        // ACT
        Set<ConstraintViolation<ProductDTO>> violations = validator.validate(product);

        // ASSERT
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("originalPrice must be >= 0")));
    }

    @Test
    void validation_WithValidData_ShouldPass() {
        // ARRANGE
        ProductDTO product = ProductDTO.builder()
                .sku("SKU-001")
                .name("Test Product")
                .originalPrice(BigDecimal.valueOf(100.00))
                .build();

        // ACT
        Set<ConstraintViolation<ProductDTO>> violations = validator.validate(product);

        // ASSERT
        assertTrue(violations.isEmpty());
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        // ARRANGE
        ProductDTO product = new ProductDTO();

        // ACT
        product.setId(1L);
        product.setSku("SKU-001");
        product.setName("Product Name");
        product.setCategory("Category");
        product.setActive(false);
        product.setDescription("Description");
        product.setOriginalPrice(BigDecimal.valueOf(50.00));
        product.setProfit(BigDecimal.valueOf(10.00));

        // ASSERT
        assertEquals(1L, product.getId());
        assertEquals("SKU-001", product.getSku());
        assertEquals("Product Name", product.getName());
        assertEquals("Category", product.getCategory());
        assertFalse(product.isActive());
        assertEquals("Description", product.getDescription());
        assertEquals(BigDecimal.valueOf(50.00), product.getOriginalPrice());
        assertEquals(BigDecimal.valueOf(10.00), product.getProfit());
    }

    @Test
    void equals_ShouldWorkCorrectly() {
        // ARRANGE
        ProductDTO product1 = ProductDTO.builder()
                .id(1L)
                .sku("SKU-001")
                .name("Product")
                .build();

        ProductDTO product2 = ProductDTO.builder()
                .id(1L)
                .sku("SKU-001")
                .name("Product")
                .build();

        ProductDTO product3 = ProductDTO.builder()
                .id(2L)
                .sku("SKU-002")
                .name("Different")
                .build();

        // ASSERT
        assertEquals(product1, product2);
        assertNotEquals(product1, product3);
    }

    @Test
    void hashCode_ShouldWorkCorrectly() {
        // ARRANGE
        ProductDTO product1 = ProductDTO.builder()
                .id(1L)
                .sku("SKU-001")
                .name("Product")
                .build();

        ProductDTO product2 = ProductDTO.builder()
                .id(1L)
                .sku("SKU-001")
                .name("Product")
                .build();

        // ASSERT
        assertEquals(product1.hashCode(), product2.hashCode());
    }

    @Test
    void toString_ShouldContainAllFields() {
        // ARRANGE
        ProductDTO product = ProductDTO.builder()
                .id(1L)
                .sku("SKU-001")
                .name("Test Product")
                .build();

        // ACT
        String result = product.toString();

        // ASSERT
        assertTrue(result.contains("SKU-001"));
        assertTrue(result.contains("Test Product"));
    }
}
