package com.example.demo.mapper;

import com.example.demo.dto.WarehouseDTO;
import com.example.demo.model.Warehouse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class WarehouseMapperTest {

    private WarehouseMapper warehouseMapper;
    private Warehouse warehouse;

    @BeforeEach
    void setUp() {
        warehouseMapper = Mappers.getMapper(WarehouseMapper.class);
        
        warehouse = Warehouse.builder()
                .id(1L)
                .code("WH-001")
                .name("Main Warehouse")
                .active(true)
                .build();
    }

    @Test
    void toDto_ShouldMapAllFields() {
        // ARRANGE - Already set up in beforeEach

        // ACT
        WarehouseDTO dto = warehouseMapper.toDto(warehouse);

        // ASSERT
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("WH-001", dto.getCode());
        assertEquals("Main Warehouse", dto.getName());
        assertTrue(dto.isActive());
    }

    @Test
    void toDto_WithInactiveWarehouse_ShouldMapCorrectly() {
        // ARRANGE
        warehouse.setActive(false);

        // ACT
        WarehouseDTO dto = warehouseMapper.toDto(warehouse);

        // ASSERT
        assertNotNull(dto);
        assertFalse(dto.isActive());
    }

    @Test
    void toEntity_ShouldMapAllFields() {
        // ARRANGE
        WarehouseDTO dto = new WarehouseDTO();
        dto.setId(2L);
        dto.setCode("WH-002");
        dto.setName("Secondary Warehouse");
        dto.setActive(false);

        // ACT
        Warehouse entity = warehouseMapper.toEntity(dto);

        // ASSERT
        assertNotNull(entity);
        assertEquals(2L, entity.getId());
        assertEquals("WH-002", entity.getCode());
        assertEquals("Secondary Warehouse", entity.getName());
        assertFalse(entity.isActive());
    }

    @Test
    void updateEntityFromDto_ShouldUpdateOnlyNonNullFields() {
        // ARRANGE
        WarehouseDTO dto = new WarehouseDTO();
        dto.setName("Updated Warehouse");
        dto.setActive(false);
        // Code is null

        // ACT
        warehouseMapper.updateEntityFromDto(dto, warehouse);

        // ASSERT
        assertEquals("Updated Warehouse", warehouse.getName());
        assertFalse(warehouse.isActive());
        // Original code should be preserved
        assertEquals("WH-001", warehouse.getCode());
    }

    @Test
    void updateEntityFromDto_WithAllNullFields_ShouldNotChangeEntity() {
        // ARRANGE
        WarehouseDTO dto = new WarehouseDTO();
        Warehouse originalWarehouse = Warehouse.builder()
                .id(warehouse.getId())
                .code(warehouse.getCode())
                .name(warehouse.getName())
                .active(warehouse.isActive())
                .build();

        // ACT
        warehouseMapper.updateEntityFromDto(dto, warehouse);

        // ASSERT
        assertEquals(originalWarehouse.getCode(), warehouse.getCode());
        assertEquals(originalWarehouse.getName(), warehouse.getName());
        assertEquals(originalWarehouse.isActive(), warehouse.isActive());
    }
}
