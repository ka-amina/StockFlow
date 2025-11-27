package com.example.demo.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InventoryTest {

    private Inventory inventory;
    private Product product;
    private Warehouse warehouse;

    @BeforeEach
    void setUp() {
        product = Product.builder().id(1L).sku("SKU-001").name("Test Product").build();
        warehouse = Warehouse.builder().id(1L).code("WH-001").name("Test Warehouse").build();
        
        inventory = Inventory.builder()
                .id(1L)
                .product(product)
                .warehouse(warehouse)
                .qtyOnHand(100)
                .qtyReserved(20)
                .build();
    }

    @Test
    void getAvailable_ShouldReturnCorrectValue() {
        // ACT
        Integer available = inventory.getAvailable();

        // ASSERT
        assertEquals(80, available);
    }

    @Test
    void increaseQtyOnHand_ShouldIncreaseQuantity() {
        // ACT
        inventory.increaseQtyOnHand(50);

        // ASSERT
        assertEquals(150, inventory.getQtyOnHand());
    }

    @Test
    void increaseQtyOnHand_ShouldThrowExceptionForNegativeQuantity() {
        // ACT & ASSERT
        assertThrows(IllegalArgumentException.class, () -> inventory.increaseQtyOnHand(-10));
    }

    @Test
    void increaseQtyOnHand_ShouldThrowExceptionForZeroQuantity() {
        // ACT & ASSERT
        assertThrows(IllegalArgumentException.class, () -> inventory.increaseQtyOnHand(0));
    }

    @Test
    void decreaseQtyOnHand_ShouldDecreaseQuantity() {
        // ACT
        inventory.decreaseQtyOnHand(30);

        // ASSERT
        assertEquals(70, inventory.getQtyOnHand());
    }

    @Test
    void decreaseQtyOnHand_ShouldThrowExceptionIfBelowReserved() {
        // ACT & ASSERT
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> inventory.decreaseQtyOnHand(85)
        );
        assertTrue(exception.getMessage().contains("Cannot decrease quantity below reserved amount"));
    }

    @Test
    void decreaseQtyOnHand_ShouldThrowExceptionForNegativeQuantity() {
        // ACT & ASSERT
        assertThrows(IllegalArgumentException.class, () -> inventory.decreaseQtyOnHand(-10));
    }

    @Test
    void increaseQtyReserved_ShouldIncreaseReservedQuantity() {
        // ACT
        inventory.increaseQtyReserved(20);

        // ASSERT
        assertEquals(40, inventory.getQtyReserved());
        assertEquals(60, inventory.getAvailable());
    }

    @Test
    void increaseQtyReserved_ShouldThrowExceptionIfInsufficientAvailable() {
        // ACT & ASSERT
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> inventory.increaseQtyReserved(85)
        );
        assertTrue(exception.getMessage().contains("Insufficient available quantity"));
    }

    @Test
    void increaseQtyReserved_ShouldThrowExceptionForNegativeQuantity() {
        // ACT & ASSERT
        assertThrows(IllegalArgumentException.class, () -> inventory.increaseQtyReserved(-10));
    }

    @Test
    void decreaseQtyReserved_ShouldDecreaseReservedQuantity() {
        // ACT
        inventory.decreaseQtyReserved(10);

        // ASSERT
        assertEquals(10, inventory.getQtyReserved());
        assertEquals(90, inventory.getAvailable());
    }

    @Test
    void decreaseQtyReserved_ShouldThrowExceptionIfBelowZero() {
        // ACT & ASSERT
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> inventory.decreaseQtyReserved(25)
        );
        assertTrue(exception.getMessage().contains("Cannot decrease reserved quantity below zero"));
    }

    @Test
    void decreaseQtyReserved_ShouldThrowExceptionForNegativeQuantity() {
        // ACT & ASSERT
        assertThrows(IllegalArgumentException.class, () -> inventory.decreaseQtyReserved(-10));
    }

    @Test
    void builder_ShouldSetDefaultQuantitiesToZero() {
        // ACT
        Inventory newInventory = Inventory.builder()
                .product(product)
                .warehouse(warehouse)
                .build();

        // ASSERT
        assertEquals(0, newInventory.getQtyOnHand());
        assertEquals(0, newInventory.getQtyReserved());
        assertEquals(0, newInventory.getAvailable());
    }

    @Test
    void complexScenario_ShouldHandleMultipleOperations() {
        // ARRANGE
        Inventory inv = Inventory.builder()
                .product(product)
                .warehouse(warehouse)
                .qtyOnHand(100)
                .qtyReserved(0)
                .build();

        // ACT - Receive 50 units
        inv.increaseQtyOnHand(50);
        assertEquals(150, inv.getQtyOnHand());
        assertEquals(150, inv.getAvailable());

        // ACT - Reserve 80 units for an order
        inv.increaseQtyReserved(80);
        assertEquals(150, inv.getQtyOnHand());
        assertEquals(80, inv.getQtyReserved());
        assertEquals(70, inv.getAvailable());

        // ACT - Ship 80 units (decrease reserved first, then on hand)
        inv.decreaseQtyReserved(80);
        inv.decreaseQtyOnHand(80);
        assertEquals(70, inv.getQtyOnHand());
        assertEquals(0, inv.getQtyReserved());
        assertEquals(70, inv.getAvailable());
    }
}
