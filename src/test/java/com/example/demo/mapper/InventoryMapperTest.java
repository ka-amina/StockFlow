package com.example.demo.mapper;

import com.example.demo.dto.InventoryDTO;
import com.example.demo.dto.InventoryMovementDTO;
import com.example.demo.enums.MovementType;
import com.example.demo.model.Inventory;
import com.example.demo.model.InventoryMovement;
import com.example.demo.model.Product;
import com.example.demo.model.Warehouse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class InventoryMapperTest {

    private InventoryMapper inventoryMapper;
    private Inventory inventory;
    private InventoryMovement inventoryMovement;
    private Product product;
    private Warehouse warehouse;

    @BeforeEach
    void setUp() {
        inventoryMapper = Mappers.getMapper(InventoryMapper.class);
        
        product = Product.builder()
                .id(1L)
                .sku("SKU-001")
                .name("Test Product")
                .build();
                
        warehouse = Warehouse.builder()
                .id(1L)
                .code("WH-001")
                .name("Main Warehouse")
                .build();
        
        inventory = Inventory.builder()
                .id(1L)
                .product(product)
                .warehouse(warehouse)
                .qtyOnHand(100)
                .qtyReserved(20)
                .build();
                
        inventoryMovement = InventoryMovement.builder()
                .id(1L)
                .product(product)
                .warehouse(warehouse)
                .type(MovementType.INBOUND)
                .quantity(50)
                .build();
    }

    @Test
    void toDto_ShouldMapAllInventoryFields() {
        // ARRANGE - Already set up in beforeEach

        // ACT
        InventoryDTO dto = inventoryMapper.toDto(inventory);

        // ASSERT
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals(1L, dto.getProductId());
        assertEquals("SKU-001", dto.getProductSku());
        assertEquals("Test Product", dto.getProductName());
        assertEquals(1L, dto.getWarehouseId());
        assertEquals("WH-001", dto.getWarehouseCode());
        assertEquals("Main Warehouse", dto.getWarehouseName());
        assertEquals(100, dto.getQtyOnHand());
        assertEquals(20, dto.getQtyReserved());
        assertEquals(80, dto.getAvailable()); // Calculated field
    }

    @Test
    void toDto_ShouldCalculateAvailableCorrectly() {
        // ARRANGE
        inventory.setQtyOnHand(200);
        inventory.setQtyReserved(50);

        // ACT
        InventoryDTO dto = inventoryMapper.toDto(inventory);

        // ASSERT
        assertEquals(150, dto.getAvailable());
    }

    @Test
    void toDto_WithZeroQuantities_ShouldMapCorrectly() {
        // ARRANGE
        inventory.setQtyOnHand(0);
        inventory.setQtyReserved(0);

        // ACT
        InventoryDTO dto = inventoryMapper.toDto(inventory);

        // ASSERT
        assertEquals(0, dto.getQtyOnHand());
        assertEquals(0, dto.getQtyReserved());
        assertEquals(0, dto.getAvailable());
    }

    @Test
    void toDto_ShouldMapAllInventoryMovementFields() {
        // ARRANGE - Already set up in beforeEach

        // ACT
        InventoryMovementDTO dto = inventoryMapper.toDto(inventoryMovement);

        // ASSERT
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals(1L, dto.getProductId());
        assertEquals("SKU-001", dto.getProductSku());
        assertEquals("Test Product", dto.getProductName());
        assertEquals(1L, dto.getWarehouseId());
        assertEquals("WH-001", dto.getWarehouseCode());
        assertEquals("Main Warehouse", dto.getWarehouseName());
        assertEquals("INBOUND", dto.getType());
        assertEquals(50, dto.getQuantity());
    }

    @Test
    void toDto_WithOutboundMovement_ShouldMapCorrectly() {
        // ARRANGE
        inventoryMovement.setType(MovementType.OUTBOUND);
        inventoryMovement.setQuantity(30);

        // ACT
        InventoryMovementDTO dto = inventoryMapper.toDto(inventoryMovement);

        // ASSERT
        assertEquals("OUTBOUND", dto.getType());
        assertEquals(30, dto.getQuantity());
    }
}
