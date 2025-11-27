package com.example.demo.model;

import com.example.demo.enums.ShipmentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ShipmentTest {

    private Shipment shipment;
    private SalesOrder salesOrder;
    private Carrier carrier;

    @BeforeEach
    void setUp() {
        Client client = Client.builder().id(1L).name("Test Client").build();
        Warehouse warehouse = Warehouse.builder().id(1L).code("WH-001").build();
        salesOrder = SalesOrder.builder().id(1L).client(client).warehouse(warehouse).build();
        carrier = Carrier.builder().id(1L).code("CARR-001").name("Test Carrier").build();

        shipment = Shipment.builder()
                .id(1L)
                .salesOrder(salesOrder)
                .carrier(carrier)
                .trackingNumber("TRACK-12345")
                .status(ShipmentStatus.PLANNED)
                .build();
    }

    @Test
    void builder_ShouldCreateShipmentWithAllFields() {
        // ARRANGE
        LocalDateTime now = LocalDateTime.now();

        // ACT
        Shipment newShipment = Shipment.builder()
                .id(2L)
                .salesOrder(salesOrder)
                .carrier(carrier)
                .trackingNumber("TRACK-99999")
                .status(ShipmentStatus.PLANNED)
                .plannedDate(now)
                .notes("Test notes")
                .createdAt(now)
                .build();

        // ASSERT
        assertEquals(2L, newShipment.getId());
        assertEquals(salesOrder, newShipment.getSalesOrder());
        assertEquals(carrier, newShipment.getCarrier());
        assertEquals("TRACK-99999", newShipment.getTrackingNumber());
        assertEquals(ShipmentStatus.PLANNED, newShipment.getStatus());
        assertEquals(now, newShipment.getPlannedDate());
        assertEquals("Test notes", newShipment.getNotes());
        assertEquals(now, newShipment.getCreatedAt());
    }

    @Test
    void builder_ShouldSetStatusToPlannedByDefault() {
        // ACT
        Shipment newShipment = Shipment.builder()
                .salesOrder(salesOrder)
                .build();

        // ASSERT
        assertEquals(ShipmentStatus.PLANNED, newShipment.getStatus());
    }

    @Test
    void onCreate_ShouldSetCreatedAtTimestamp() {
        // ARRANGE
        Shipment newShipment = new Shipment();
        newShipment.setSalesOrder(salesOrder);

        // ACT
        newShipment.onCreate();

        // ASSERT
        assertNotNull(newShipment.getCreatedAt());
        assertTrue(newShipment.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void markInTransit_ShouldTransitionFromPlannedToInTransit() {
        // ACT
        shipment.markInTransit();

        // ASSERT
        assertEquals(ShipmentStatus.IN_TRANSIT, shipment.getStatus());
        assertNotNull(shipment.getShippedDate());
        assertTrue(shipment.getShippedDate().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void markInTransit_ShouldThrowExceptionIfNotPlanned() {
        // ARRANGE
        shipment.setStatus(ShipmentStatus.DELIVERED);

        // ACT & ASSERT
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> shipment.markInTransit()
        );
        assertTrue(exception.getMessage().contains("Can only mark PLANNED shipments as IN_TRANSIT"));
    }

    @Test
    void markDelivered_ShouldTransitionFromInTransitToDelivered() {
        // ARRANGE
        shipment.markInTransit();

        // ACT
        shipment.markDelivered();

        // ASSERT
        assertEquals(ShipmentStatus.DELIVERED, shipment.getStatus());
        assertNotNull(shipment.getDeliveredDate());
        assertTrue(shipment.getDeliveredDate().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void markDelivered_ShouldThrowExceptionIfNotInTransit() {
        // ACT & ASSERT
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> shipment.markDelivered()
        );
        assertTrue(exception.getMessage().contains("Can only mark IN_TRANSIT shipments as DELIVERED"));
    }

    @Test
    void completeShipmentLifecycle_ShouldTransitionThroughAllStates() {
        // ARRANGE
        Shipment lifecycle = Shipment.builder()
                .salesOrder(salesOrder)
                .carrier(carrier)
                .trackingNumber("TRACK-LIFECYCLE")
                .status(ShipmentStatus.PLANNED)
                .build();

        // ACT & ASSERT - PLANNED
        assertEquals(ShipmentStatus.PLANNED, lifecycle.getStatus());
        assertNull(lifecycle.getShippedDate());
        assertNull(lifecycle.getDeliveredDate());

        // ACT & ASSERT - IN_TRANSIT
        lifecycle.markInTransit();
        assertEquals(ShipmentStatus.IN_TRANSIT, lifecycle.getStatus());
        assertNotNull(lifecycle.getShippedDate());
        assertNull(lifecycle.getDeliveredDate());

        // ACT & ASSERT - DELIVERED
        lifecycle.markDelivered();
        assertEquals(ShipmentStatus.DELIVERED, lifecycle.getStatus());
        assertNotNull(lifecycle.getShippedDate());
        assertNotNull(lifecycle.getDeliveredDate());
    }

    @Test
    void setters_ShouldUpdateAllFields() {
        // ARRANGE
        LocalDateTime now = LocalDateTime.now();

        // ACT
        shipment.setTrackingNumber("UPDATED-TRACK");
        shipment.setStatus(ShipmentStatus.DELIVERED);
        shipment.setPlannedDate(now);
        shipment.setShippedDate(now);
        shipment.setDeliveredDate(now);
        shipment.setNotes("Updated notes");

        // ASSERT
        assertEquals("UPDATED-TRACK", shipment.getTrackingNumber());
        assertEquals(ShipmentStatus.DELIVERED, shipment.getStatus());
        assertEquals(now, shipment.getPlannedDate());
        assertEquals(now, shipment.getShippedDate());
        assertEquals(now, shipment.getDeliveredDate());
        assertEquals("Updated notes", shipment.getNotes());
    }
}
