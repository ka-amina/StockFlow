package com.example.demo.model;

import com.example.demo.enums.SalesOrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SalesOrderTest {

    private SalesOrder salesOrder;
    private Client client;
    private Warehouse warehouse;

    @BeforeEach
    void setUp() {
        client = Client.builder().id(1L).name("Test Client").build();
        warehouse = Warehouse.builder().id(1L).code("WH-001").name("Test Warehouse").build();
        
        salesOrder = SalesOrder.builder()
                .id(1L)
                .orderNumber("SO-12345")
                .client(client)
                .warehouse(warehouse)
                .status(SalesOrderStatus.CREATED)
                .build();
    }

    @Test
    void onCreate_ShouldSetTimestampsAndOrderNumber() {
        // ARRANGE
        SalesOrder order = new SalesOrder();
        order.setClient(client);
        order.setWarehouse(warehouse);

        // ACT
        order.onCreate();

        // ASSERT
        assertNotNull(order.getCreatedAt());
        assertNotNull(order.getOrderDate());
        assertNotNull(order.getOrderNumber());
        assertTrue(order.getOrderNumber().startsWith("SO-"));
    }

    @Test
    void addOrderLine_ShouldAddLineAndSetBidirectionalRelationship() {
        // ARRANGE
        SalesOrderLine line = new SalesOrderLine();
        line.setId(1L);

        // ACT
        salesOrder.addOrderLine(line);

        // ASSERT
        assertEquals(1, salesOrder.getOrderLines().size());
        assertEquals(salesOrder, line.getSalesOrder());
    }

    @Test
    void removeOrderLine_ShouldRemoveLineAndClearRelationship() {
        // ARRANGE
        SalesOrderLine line = new SalesOrderLine();
        salesOrder.addOrderLine(line);

        // ACT
        salesOrder.removeOrderLine(line);

        // ASSERT
        assertEquals(0, salesOrder.getOrderLines().size());
        assertNull(line.getSalesOrder());
    }

    @Test
    void reserve_ShouldTransitionFromCreatedToReserved() {
        // ACT
        salesOrder.reserve();

        // ASSERT
        assertEquals(SalesOrderStatus.RESERVED, salesOrder.getStatus());
        assertNotNull(salesOrder.getReservedAt());
    }

    @Test
    void reserve_ShouldThrowExceptionIfNotInCreatedStatus() {
        // ARRANGE
        salesOrder.setStatus(SalesOrderStatus.RESERVED);

        // ACT & ASSERT
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> salesOrder.reserve()
        );
        assertTrue(exception.getMessage().contains("Can only reserve orders in CREATED status"));
    }

    @Test
    void ship_ShouldTransitionFromReservedToShipped() {
        // ARRANGE
        salesOrder.reserve();

        // ACT
        salesOrder.ship();

        // ASSERT
        assertEquals(SalesOrderStatus.SHIPPED, salesOrder.getStatus());
        assertNotNull(salesOrder.getShippedAt());
    }

    @Test
    void ship_ShouldThrowExceptionIfNotInReservedStatus() {
        // ACT & ASSERT
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> salesOrder.ship()
        );
        assertTrue(exception.getMessage().contains("Can only ship orders in RESERVED status"));
    }

    @Test
    void builder_ShouldSetDefaultStatusToCreated() {
        // ACT
        SalesOrder order = SalesOrder.builder()
                .client(client)
                .warehouse(warehouse)
                .build();

        // ASSERT
        assertEquals(SalesOrderStatus.CREATED, order.getStatus());
    }

    @Test
    void builder_ShouldInitializeEmptyOrderLinesList() {
        // ACT
        SalesOrder order = SalesOrder.builder()
                .client(client)
                .warehouse(warehouse)
                .build();

        // ASSERT
        assertNotNull(order.getOrderLines());
        assertEquals(0, order.getOrderLines().size());
    }

    @Test
    void setters_ShouldUpdateAllFields() {
        // ARRANGE
        LocalDateTime now = LocalDateTime.now();
        User user = User.builder().id(1L).email("user@test.com").build();

        // ACT
        salesOrder.setOrderNumber("SO-99999");
        salesOrder.setUser(user);
        salesOrder.setStatus(SalesOrderStatus.DELIVERED);
        salesOrder.setNotes("Test notes");
        salesOrder.setReservedAt(now);
        salesOrder.setShippedAt(now);
        salesOrder.setDeliveredAt(now);
        salesOrder.setCanceledAt(now);

        // ASSERT
        assertEquals("SO-99999", salesOrder.getOrderNumber());
        assertEquals(user, salesOrder.getUser());
        assertEquals(SalesOrderStatus.DELIVERED, salesOrder.getStatus());
        assertEquals("Test notes", salesOrder.getNotes());
        assertEquals(now, salesOrder.getReservedAt());
        assertEquals(now, salesOrder.getShippedAt());
        assertEquals(now, salesOrder.getDeliveredAt());
        assertEquals(now, salesOrder.getCanceledAt());
    }
}
