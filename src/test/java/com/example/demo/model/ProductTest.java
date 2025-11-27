package com.example.demo.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    void builder_ShouldCreateProductWithAllFields() {
        // ARRANGE & ACT
        Product product = Product.builder()
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
    void builder_ShouldSetActiveToTrueByDefault() {
        // ARRANGE & ACT
        Product product = Product.builder()
                .sku("SKU-001")
                .name("Test Product")
                .build();

        // ASSERT
        assertTrue(product.isActive());
    }

    @Test
    void setters_ShouldUpdateFields() {
        // ARRANGE
        Product product = new Product();

        // ACT
        product.setId(1L);
        product.setSku("SKU-001");
        product.setName("Test Product");
        product.setCategory("Category");
        product.setActive(false);
        product.setDescription("Description");
        product.setOriginalPrice(BigDecimal.valueOf(50.00));
        product.setProfit(BigDecimal.valueOf(10.00));

        // ASSERT
        assertEquals(1L, product.getId());
        assertEquals("SKU-001", product.getSku());
        assertEquals("Test Product", product.getName());
        assertEquals("Category", product.getCategory());
        assertFalse(product.isActive());
        assertEquals("Description", product.getDescription());
        assertEquals(BigDecimal.valueOf(50.00), product.getOriginalPrice());
        assertEquals(BigDecimal.valueOf(10.00), product.getProfit());
    }

    @Test
    void equals_ShouldReturnTrueForSameObject() {
        // ARRANGE
        Product product = Product.builder().id(1L).sku("SKU-001").name("Test").build();

        // ACT & ASSERT
        assertEquals(product, product);
    }

    @Test
    void hashCode_ShouldBeConsistent() {
        // ARRANGE
        Product product1 = Product.builder().id(1L).sku("SKU-001").name("Test").build();
        Product product2 = Product.builder().id(1L).sku("SKU-001").name("Test").build();

        // ACT & ASSERT
        assertEquals(product1.hashCode(), product2.hashCode());
    }
}
