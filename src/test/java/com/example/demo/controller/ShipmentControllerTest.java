package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.CreateShipmentDTO;
import com.example.demo.dto.ShipmentDTO;
import com.example.demo.enums.ShipmentStatus;
import com.example.demo.service.ShipmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShipmentControllerTest {

    @Mock
    private ShipmentService shipmentService;

    @InjectMocks
    private ShipmentController shipmentController;

    @Test
    void createShipment_Success() {
        // ARRANGE
        CreateShipmentDTO requestDto = CreateShipmentDTO.builder()
                .salesOrderId(1L)
                .carrierId(1L)
                .trackingNumber("TRACK123456")
                .build();

        ShipmentDTO createdDto = new ShipmentDTO();
        createdDto.setId(1L);
        createdDto.setSalesOrderId(1L);
        createdDto.setStatus(ShipmentStatus.PLANNED);
        createdDto.setTrackingNumber("TRACK123456");

        when(shipmentService.createShipment(any(CreateShipmentDTO.class))).thenReturn(createdDto);

        // ACT
        ResponseEntity<ApiResponse<ShipmentDTO>> response = shipmentController.createShipment(requestDto);

        // ASSERT
        assertNotNull(response);
        assertEquals(201, response.getStatusCode().value());
        assertEquals("Shipment created successfully", response.getBody().getMessage());
        assertNotNull(response.getBody().getData());
        verify(shipmentService, times(1)).createShipment(requestDto);
    }

    @Test
    void getShipmentById_Success() {
        // ARRANGE
        ShipmentDTO shipment = new ShipmentDTO();
        shipment.setId(1L);
        shipment.setSalesOrderId(1L);
        shipment.setStatus(ShipmentStatus.PLANNED);

        when(shipmentService.getShipmentById(1L)).thenReturn(shipment);

        // ACT
        ResponseEntity<ApiResponse<ShipmentDTO>> response = shipmentController.getShipmentById(1L);

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Shipment retrieved successfully", response.getBody().getMessage());
        assertEquals(1L, response.getBody().getData().getId());
        verify(shipmentService, times(1)).getShipmentById(1L);
    }

    @Test
    void getAllShipments_Success() {
        // ARRANGE
        ShipmentDTO shipment1 = new ShipmentDTO();
        shipment1.setId(1L);
        shipment1.setStatus(ShipmentStatus.PLANNED);

        ShipmentDTO shipment2 = new ShipmentDTO();
        shipment2.setId(2L);
        shipment2.setStatus(ShipmentStatus.IN_TRANSIT);

        List<ShipmentDTO> shipments = Arrays.asList(shipment1, shipment2);
        when(shipmentService.getAllShipments()).thenReturn(shipments);

        // ACT
        ResponseEntity<ApiResponse<List<ShipmentDTO>>> response = shipmentController.getAllShipments(null);

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("All shipments retrieved successfully", response.getBody().getMessage());
        assertEquals(2, response.getBody().getData().size());
        verify(shipmentService, times(1)).getAllShipments();
    }

    @Test
    void getShipmentByTrackingNumber_Success() {
        // ARRANGE
        ShipmentDTO shipment = new ShipmentDTO();
        shipment.setId(1L);
        shipment.setTrackingNumber("TRACK123456");
        shipment.setStatus(ShipmentStatus.IN_TRANSIT);

        when(shipmentService.getShipmentByTrackingNumber("TRACK123456")).thenReturn(shipment);

        // ACT
        ResponseEntity<ApiResponse<ShipmentDTO>> response = shipmentController.getShipmentByTrackingNumber("TRACK123456");

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Shipment retrieved successfully", response.getBody().getMessage());
        assertEquals("TRACK123456", response.getBody().getData().getTrackingNumber());
        verify(shipmentService, times(1)).getShipmentByTrackingNumber("TRACK123456");
    }

    @Test
    void getShipmentsByCarrier_Success() {
        // ARRANGE
        ShipmentDTO shipment = new ShipmentDTO();
        shipment.setId(1L);
        shipment.setCarrierId(1L);
        shipment.setStatus(ShipmentStatus.PLANNED);

        when(shipmentService.getShipmentById(1L)).thenReturn(shipment);

        // ACT
        ResponseEntity<ApiResponse<ShipmentDTO>> response = shipmentController.getShipmentById(1L);

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Shipment retrieved successfully", response.getBody().getMessage());
        assertEquals(1L, response.getBody().getData().getCarrierId());
        verify(shipmentService, times(1)).getShipmentById(1L);
    }

    @Test
    void getShipmentsByStatus_Success() {
        // ARRANGE
        ShipmentDTO shipment1 = new ShipmentDTO();
        shipment1.setId(1L);
        shipment1.setStatus(ShipmentStatus.IN_TRANSIT);

        List<ShipmentDTO> shipments = Arrays.asList(shipment1);
        when(shipmentService.getShipmentsByStatus(ShipmentStatus.IN_TRANSIT)).thenReturn(shipments);

        // ACT
        ResponseEntity<ApiResponse<List<ShipmentDTO>>> response = shipmentController.getAllShipments(ShipmentStatus.IN_TRANSIT);

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Shipments retrieved by status: IN_TRANSIT", response.getBody().getMessage());
        assertEquals(1, response.getBody().getData().size());
        assertEquals(ShipmentStatus.IN_TRANSIT, response.getBody().getData().get(0).getStatus());
        verify(shipmentService, times(1)).getShipmentsByStatus(ShipmentStatus.IN_TRANSIT);
    }

    @Test
    void markInTransit_Success() {
        // ARRANGE
        ShipmentDTO shipment = new ShipmentDTO();
        shipment.setId(1L);
        shipment.setStatus(ShipmentStatus.IN_TRANSIT);

        when(shipmentService.markInTransit(1L)).thenReturn(shipment);

        // ACT
        ResponseEntity<ApiResponse<ShipmentDTO>> response = shipmentController.markInTransit(1L);

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Shipment marked as in transit", response.getBody().getMessage());
        assertEquals(ShipmentStatus.IN_TRANSIT, response.getBody().getData().getStatus());
        verify(shipmentService, times(1)).markInTransit(1L);
    }

    @Test
    void markDelivered_Success() {
        // ARRANGE
        ShipmentDTO shipment = new ShipmentDTO();
        shipment.setId(1L);
        shipment.setStatus(ShipmentStatus.DELIVERED);

        when(shipmentService.markDelivered(1L)).thenReturn(shipment);

        // ACT
        ResponseEntity<ApiResponse<ShipmentDTO>> response = shipmentController.markDelivered(1L);

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Shipment marked as delivered", response.getBody().getMessage());
        assertEquals(ShipmentStatus.DELIVERED, response.getBody().getData().getStatus());
        verify(shipmentService, times(1)).markDelivered(1L);
    }

    @Test
    void updateShipmentTracking_Success() {
        // ARRANGE
        ShipmentDTO shipment = new ShipmentDTO();
        shipment.setId(1L);
        shipment.setTrackingNumber("NEWTRACK789");

        when(shipmentService.updateTrackingNumber(1L, "NEWTRACK789")).thenReturn(shipment);

        // ACT
        ResponseEntity<ApiResponse<ShipmentDTO>> response = shipmentController.updateTrackingNumber(1L, "NEWTRACK789");

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Tracking number updated successfully", response.getBody().getMessage());
        assertEquals("NEWTRACK789", response.getBody().getData().getTrackingNumber());
        verify(shipmentService, times(1)).updateTrackingNumber(1L, "NEWTRACK789");
    }
}
