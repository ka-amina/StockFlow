package com.example.demo.mapper;

import com.example.demo.dto.ProductDTO;
import com.example.demo.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProductMapperTest {

    private ProductMapper productMapper;
    private Product product;

    @BeforeEach
    void setUp() {
        productMapper = Mappers.getMapper(ProductMapper.class);
        
        product = Product.builder()
                .id(1L)
                .sku("SKU-001")
                .name("Test Product")
                .category("Electronics")
                .active(true)
                .description("Test description")
                .originalPrice(BigDecimal.valueOf(100.00))
                .profit(BigDecimal.valueOf(20.00))
                .build();
    }

    @Test
    void toDto_ShouldMapAllFields() {
        // ARRANGE - Already set up in beforeEach

        // ACT
        ProductDTO dto = productMapper.toDto(product);

        // ASSERT
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("SKU-001", dto.getSku());
        assertEquals("Test Product", dto.getName());
        assertEquals("Electronics", dto.getCategory());
        assertTrue(dto.isActive());
        assertEquals("Test description", dto.getDescription());
        assertEquals(BigDecimal.valueOf(100.00), dto.getOriginalPrice());
        assertEquals(BigDecimal.valueOf(20.00), dto.getProfit());
    }

    @Test
    void toDto_WhenNullOptionalFields_ShouldHandleGracefully() {
        // ARRANGE
        product.setCategory(null);
        product.setDescription(null);

        // ACT
        ProductDTO dto = productMapper.toDto(product);

        // ASSERT
        assertNotNull(dto);
        assertNull(dto.getCategory());
        assertNull(dto.getDescription());
    }

    @Test
    void toEntity_ShouldMapAllFields() {
        // ARRANGE
        ProductDTO dto = new ProductDTO();
        dto.setId(2L);
        dto.setSku("SKU-002");
        dto.setName("New Product");
        dto.setCategory("Category");
        dto.setActive(false);
        dto.setDescription("Description");
        dto.setOriginalPrice(BigDecimal.valueOf(50.00));
        dto.setProfit(BigDecimal.valueOf(10.00));

        // ACT
        Product entity = productMapper.toEntity(dto);

        // ASSERT
        assertNotNull(entity);
        assertEquals(2L, entity.getId());
        assertEquals("SKU-002", entity.getSku());
        assertEquals("New Product", entity.getName());
        assertEquals("Category", entity.getCategory());
        assertFalse(entity.isActive());
        assertEquals("Description", entity.getDescription());
        assertEquals(BigDecimal.valueOf(50.00), entity.getOriginalPrice());
        assertEquals(BigDecimal.valueOf(10.00), entity.getProfit());
    }

    @Test
    void updateEntityFromDto_ShouldUpdateOnlyNonNullFields() {
        // ARRANGE
        ProductDTO dto = new ProductDTO();
        dto.setName("Updated Name");
        dto.setCategory("Updated Category");
        // Other fields are null

        // ACT
        productMapper.updateEntityFromDto(dto, product);

        // ASSERT
        assertEquals("Updated Name", product.getName());
        assertEquals("Updated Category", product.getCategory());
        // Original values should be preserved
        assertEquals("SKU-001", product.getSku());
        assertEquals(BigDecimal.valueOf(100.00), product.getOriginalPrice());
    }
}
