package com.example.demo.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WarehouseTest {

    @Test
    void builder_ShouldCreateWarehouseWithAllFields() {
        // ARRANGE & ACT
        Warehouse warehouse = Warehouse.builder()
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
    void builder_ShouldSetActiveToTrueByDefault() {
        // ARRANGE & ACT
        Warehouse warehouse = Warehouse.builder()
                .code("WH-001")
                .name("Test Warehouse")
                .build();

        // ASSERT
        assertTrue(warehouse.isActive());
    }

    @Test
    void setters_ShouldUpdateFields() {
        // ARRANGE
        Warehouse warehouse = new Warehouse();

        // ACT
        warehouse.setId(1L);
        warehouse.setCode("WH-001");
        warehouse.setName("Updated Warehouse");
        warehouse.setActive(false);

        // ASSERT
        assertEquals(1L, warehouse.getId());
        assertEquals("WH-001", warehouse.getCode());
        assertEquals("Updated Warehouse", warehouse.getName());
        assertFalse(warehouse.isActive());
    }

    @Test
    void noArgsConstructor_ShouldCreateEmptyWarehouse() {
        // ACT
        Warehouse warehouse = new Warehouse();

        // ASSERT
        assertNotNull(warehouse);
        assertNull(warehouse.getId());
        assertNull(warehouse.getCode());
        assertNull(warehouse.getName());
    }

    @Test
    void allArgsConstructor_ShouldCreateWarehouseWithAllFields() {
        // ACT
        Warehouse warehouse = new Warehouse(1L, "WH-001", "Test Warehouse", true);

        // ASSERT
        assertEquals(1L, warehouse.getId());
        assertEquals("WH-001", warehouse.getCode());
        assertEquals("Test Warehouse", warehouse.getName());
        assertTrue(warehouse.isActive());
    }
}
