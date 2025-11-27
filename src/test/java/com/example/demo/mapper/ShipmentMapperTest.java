package com.example.demo.mapper;

import com.example.demo.dto.ShipmentDTO;
import com.example.demo.enums.ShipmentStatus;
import com.example.demo.model.Carrier;
import com.example.demo.model.Client;
import com.example.demo.model.SalesOrder;
import com.example.demo.model.Shipment;
import com.example.demo.model.Warehouse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ShipmentMapperTest {

    private ShipmentMapper shipmentMapper;
    private Shipment shipment;
    private SalesOrder salesOrder;
    private Carrier carrier;

    @BeforeEach
    void setUp() {
        shipmentMapper = Mappers.getMapper(ShipmentMapper.class);
        
        Client client = Client.builder().id(1L).name("Test Client").build();
        Warehouse warehouse = Warehouse.builder().id(1L).code("WH-001").build();
        
        salesOrder = SalesOrder.builder()
                .id(100L)
                .orderNumber("SO-12345")
                .client(client)
                .warehouse(warehouse)
                .build();
                
        carrier = Carrier.builder()
                .id(1L)
                .code("CARR-001")
                .name("Test Carrier")
                .build();
        
        shipment = Shipment.builder()
                .id(1L)
                .salesOrder(salesOrder)
                .carrier(carrier)
                .trackingNumber("TRACK-12345")
                .status(ShipmentStatus.PLANNED)
                .plannedDate(LocalDateTime.now())
                .notes("Test notes")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void toDto_ShouldMapAllFields() {
        // ARRANGE - Already set up in beforeEach

        // ACT
        ShipmentDTO dto = shipmentMapper.toDto(shipment);

        // ASSERT
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals(100L, dto.getSalesOrderId());
        assertEquals("SO-100", dto.getSalesOrderReference());
        assertEquals(1L, dto.getCarrierId());
        assertEquals("Test Carrier", dto.getCarrierName());
        assertEquals("CARR-001", dto.getCarrierCode());
        assertEquals("TRACK-12345", dto.getTrackingNumber());
        assertEquals(ShipmentStatus.PLANNED, dto.getStatus());
        assertEquals("Test notes", dto.getNotes());
    }

    @Test
    void toDto_WhenNullCarrier_ShouldHandleGracefully() {
        // ARRANGE
        shipment.setCarrier(null);

        // ACT
        ShipmentDTO dto = shipmentMapper.toDto(shipment);

        // ASSERT
        assertNotNull(dto);
        assertNull(dto.getCarrierId());
        assertNull(dto.getCarrierName());
        assertNull(dto.getCarrierCode());
    }

    @Test
    void toDto_WhenNullSalesOrder_ShouldHandleGracefully() {
        // ARRANGE
        shipment.setSalesOrder(null);

        // ACT
        ShipmentDTO dto = shipmentMapper.toDto(shipment);

        // ASSERT
        assertNotNull(dto);
        assertNull(dto.getSalesOrderId());
        assertNull(dto.getSalesOrderReference());
    }

    @Test
    void toDto_WithInTransitStatus_ShouldMapCorrectly() {
        // ARRANGE
        shipment.setStatus(ShipmentStatus.IN_TRANSIT);
        shipment.setShippedDate(LocalDateTime.now());

        // ACT
        ShipmentDTO dto = shipmentMapper.toDto(shipment);

        // ASSERT
        assertEquals(ShipmentStatus.IN_TRANSIT, dto.getStatus());
        assertNotNull(dto.getShippedDate());
    }

    @Test
    void toDto_WithDeliveredStatus_ShouldMapCorrectly() {
        // ARRANGE
        shipment.setStatus(ShipmentStatus.DELIVERED);
        shipment.setDeliveredDate(LocalDateTime.now());

        // ACT
        ShipmentDTO dto = shipmentMapper.toDto(shipment);

        // ASSERT
        assertEquals(ShipmentStatus.DELIVERED, dto.getStatus());
        assertNotNull(dto.getDeliveredDate());
    }

    @Test
    void generateOrderReference_ShouldCreateCorrectReference() {
        // ARRANGE - Already set up in beforeEach

        // ACT
        String reference = shipmentMapper.generateOrderReference(shipment);

        // ASSERT
        assertEquals("SO-100", reference);
    }

    @Test
    void generateOrderReference_WhenNullSalesOrder_ShouldReturnNull() {
        // ARRANGE
        shipment.setSalesOrder(null);

        // ACT
        String reference = shipmentMapper.generateOrderReference(shipment);

        // ASSERT
        assertNull(reference);
    }
}
