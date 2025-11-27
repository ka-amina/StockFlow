package com.example.demo.mapper;

import com.example.demo.dto.SupplierDTO;
import com.example.demo.model.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class SupplierMapperTest {

    private SupplierMapper supplierMapper;
    private Supplier supplier;

    @BeforeEach
    void setUp() {
        supplierMapper = Mappers.getMapper(SupplierMapper.class);
        
        supplier = new Supplier();
        supplier.setId(1L);
        supplier.setName("Test Supplier");
    }

    @Test
    void toDto_ShouldMapAllFields() {
        // ARRANGE - Already set up in beforeEach

        // ACT
        SupplierDTO dto = supplierMapper.toDto(supplier);

        // ASSERT
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Test Supplier", dto.getName());
    }

    @Test
    void toEntity_ShouldMapAllFields() {
        // ARRANGE
        SupplierDTO dto = new SupplierDTO();
        dto.setId(2L);
        dto.setName("New Supplier");

        // ACT
        Supplier entity = supplierMapper.toEntity(dto);

        // ASSERT
        assertNotNull(entity);
        assertEquals(2L, entity.getId());
        assertEquals("New Supplier", entity.getName());
    }

    @Test
    void toEntity_WithNullId_ShouldMapCorrectly() {
        // ARRANGE
        SupplierDTO dto = new SupplierDTO();
        dto.setName("Supplier Without ID");

        // ACT
        Supplier entity = supplierMapper.toEntity(dto);

        // ASSERT
        assertNotNull(entity);
        assertNull(entity.getId());
        assertEquals("Supplier Without ID", entity.getName());
    }

    @Test
    void toDto_WithDifferentSupplier_ShouldMapCorrectly() {
        // ARRANGE
        supplier.setId(100L);
        supplier.setName("Another Supplier");

        // ACT
        SupplierDTO dto = supplierMapper.toDto(supplier);

        // ASSERT
        assertEquals(100L, dto.getId());
        assertEquals("Another Supplier", dto.getName());
    }
}

