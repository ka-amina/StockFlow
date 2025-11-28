package com.example.demo.service;

import com.example.demo.dto.CreateShipmentDTO;
import com.example.demo.dto.ShipmentDTO;
import com.example.demo.enums.ShipmentStatus;
import com.example.demo.mapper.ShipmentMapper;
import com.example.demo.model.Carrier;
import com.example.demo.model.SalesOrder;
import com.example.demo.model.Shipment;
import com.example.demo.model.Warehouse;
import com.example.demo.repository.CarrierRepository;
import com.example.demo.repository.SalesOrderRepository;
import com.example.demo.repository.ShipmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShipmentServiceTest {

    @Mock private ShipmentRepository shipmentRepository;
    @Mock private SalesOrderRepository salesOrderRepository;
    @Mock private CarrierRepository carrierRepository;
    @Mock private ShipmentMapper shipmentMapper;

    @InjectMocks private ShipmentService shipmentService;

    @Test
    void createShipment_Success() {
        // Given
        CreateShipmentDTO request = new CreateShipmentDTO();
        request.setSalesOrderId(1L);
        request.setCarrierId(1L);
        request.setTrackingNumber("TRACK123");

        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setId(1L);
        salesOrder.setStatus(com.example.demo.enums.SalesOrderStatus.RESERVED);
        salesOrder.setWarehouse(new Warehouse());

        Carrier carrier = new Carrier();
        carrier.setId(1L);

        Shipment shipment = new Shipment();
        shipment.setId(1L);

        when(salesOrderRepository.findById(1L)).thenReturn(Optional.of(salesOrder));
        when(carrierRepository.findById(1L)).thenReturn(Optional.of(carrier));
        when(shipmentRepository.findBySalesOrder(salesOrder)).thenReturn(Optional.empty());
        when(shipmentRepository.save(any(Shipment.class))).thenReturn(shipment);
        when(shipmentMapper.toDto(shipment)).thenReturn(new ShipmentDTO());

        // When
        ShipmentDTO result = shipmentService.createShipment(request);

        // Then
        assertNotNull(result);
        verify(shipmentRepository).save(any(Shipment.class));
    }

    @Test
    void getShipmentByTrackingNumber_Success() {
        // Given
        Shipment shipment = new Shipment();
        shipment.setTrackingNumber("TRACK123");

        when(shipmentRepository.findByTrackingNumber("TRACK123")).thenReturn(Optional.of(shipment));
        when(shipmentMapper.toDto(shipment)).thenReturn(new ShipmentDTO());

        // When
        ShipmentDTO result = shipmentService.getShipmentByTrackingNumber("TRACK123");

        // Then
        assertNotNull(result);
    }

    @Test
    void getShipmentByTrackingNumber_NotFound() {
        // Given
        when(shipmentRepository.findByTrackingNumber("INVALID")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResponseStatusException.class, () ->
                shipmentService.getShipmentByTrackingNumber("INVALID"));
    }

    @Test
    void markInTransit_Success() {
        // Given
        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setId(1L);
        salesOrder.setStatus(com.example.demo.enums.SalesOrderStatus.RESERVED);
        
        Shipment shipment = new Shipment();
        shipment.setId(1L);
        shipment.setStatus(ShipmentStatus.PLANNED);
        shipment.setSalesOrder(salesOrder);

        when(shipmentRepository.findById(1L)).thenReturn(Optional.of(shipment));
        when(salesOrderRepository.save(salesOrder)).thenReturn(salesOrder);
        when(shipmentRepository.save(shipment)).thenReturn(shipment);
        when(shipmentMapper.toDto(shipment)).thenReturn(new ShipmentDTO());

        // When
        ShipmentDTO result = shipmentService.markInTransit(1L);

        // Then
        assertEquals(ShipmentStatus.IN_TRANSIT, shipment.getStatus());
        assertNotNull(result);
        verify(shipmentRepository).save(shipment);
        verify(salesOrderRepository).save(salesOrder);
    }

    @Test
    void markDelivered_Success() {
        // Given
        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setId(1L);
        salesOrder.setStatus(com.example.demo.enums.SalesOrderStatus.SHIPPED);
        
        Shipment shipment = new Shipment();
        shipment.setId(1L);
        shipment.setStatus(ShipmentStatus.IN_TRANSIT);
        shipment.setSalesOrder(salesOrder);

        when(shipmentRepository.findById(1L)).thenReturn(Optional.of(shipment));
        when(salesOrderRepository.save(salesOrder)).thenReturn(salesOrder);
        when(shipmentRepository.save(shipment)).thenReturn(shipment);
        when(shipmentMapper.toDto(shipment)).thenReturn(new ShipmentDTO());

        // When
        ShipmentDTO result = shipmentService.markDelivered(1L);

        // Then
        assertEquals(ShipmentStatus.DELIVERED, shipment.getStatus());
        assertNotNull(result);
        verify(shipmentRepository).save(shipment);
        verify(salesOrderRepository).save(salesOrder);
    }

    @Test
    void getShipmentBySalesOrder_Success() {
        // Given
        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setId(1L);

        Shipment shipment = new Shipment();
        shipment.setSalesOrder(salesOrder);

        when(salesOrderRepository.findById(1L)).thenReturn(Optional.of(salesOrder));
        when(shipmentRepository.findBySalesOrder(salesOrder)).thenReturn(Optional.of(shipment));
        when(shipmentMapper.toDto(shipment)).thenReturn(new ShipmentDTO());

        // When
        ShipmentDTO result = shipmentService.getShipmentBySalesOrder(1L);

        // Then
        assertNotNull(result);
    }

    @Test
    void createShipment_SalesOrderNotFound() {
        CreateShipmentDTO request = new CreateShipmentDTO();
        request.setSalesOrderId(999L);

        when(salesOrderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> shipmentService.createShipment(request));
    }

    @Test
    void createShipment_CarrierNotFound() {
        CreateShipmentDTO request = new CreateShipmentDTO();
        request.setSalesOrderId(1L);
        request.setCarrierId(999L);

        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setStatus(com.example.demo.enums.SalesOrderStatus.RESERVED);
        when(salesOrderRepository.findById(1L)).thenReturn(Optional.of(salesOrder));
        when(carrierRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> shipmentService.createShipment(request));
    }

    @Test
    void createShipment_DuplicateTrackingNumber() {
        CreateShipmentDTO request = new CreateShipmentDTO();
        request.setSalesOrderId(1L);
        request.setCarrierId(1L);
        request.setTrackingNumber("TRACK123");

        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setStatus(com.example.demo.enums.SalesOrderStatus.RESERVED);
        Carrier carrier = new Carrier();

        when(salesOrderRepository.findById(1L)).thenReturn(Optional.of(salesOrder));
        when(carrierRepository.findById(1L)).thenReturn(Optional.of(carrier));
        when(shipmentRepository.findBySalesOrder(salesOrder)).thenReturn(Optional.empty());
        when(shipmentRepository.existsByTrackingNumber("TRACK123")).thenReturn(true);

        assertThrows(ResponseStatusException.class,
                () -> shipmentService.createShipment(request));
    }

    @Test
    void updateCarrier_Success() {
        Shipment shipment = new Shipment();
        shipment.setId(1L);
        Carrier newCarrier = new Carrier();
        newCarrier.setId(2L);

        when(shipmentRepository.findById(1L)).thenReturn(Optional.of(shipment));
        when(carrierRepository.findById(2L)).thenReturn(Optional.of(newCarrier));
        when(shipmentRepository.save(shipment)).thenReturn(shipment);
        when(shipmentMapper.toDto(shipment)).thenReturn(new ShipmentDTO());

        ShipmentDTO result = shipmentService.updateCarrier(1L, 2L);

        assertNotNull(result);
        verify(shipmentRepository).save(shipment);
    }

    @Test
    void updateCarrier_ShipmentNotFound() {
        when(shipmentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> shipmentService.updateCarrier(999L, 1L));
    }

    @Test
    void updateCarrier_CarrierNotFound() {
        Shipment shipment = new Shipment();
        when(shipmentRepository.findById(1L)).thenReturn(Optional.of(shipment));
        when(carrierRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> shipmentService.updateCarrier(1L, 999L));
    }

    @Test
    void updateTrackingNumber_Success() {
        Shipment shipment = new Shipment();
        shipment.setId(1L);

        when(shipmentRepository.findById(1L)).thenReturn(Optional.of(shipment));
        when(shipmentRepository.save(shipment)).thenReturn(shipment);
        when(shipmentMapper.toDto(shipment)).thenReturn(new ShipmentDTO());

        ShipmentDTO result = shipmentService.updateTrackingNumber(1L, "NEW-TRACK");

        assertNotNull(result);
        verify(shipmentRepository).save(shipment);
    }

    @Test
    void getShipmentById_Success() {
        Shipment shipment = new Shipment();
        shipment.setId(1L);

        when(shipmentRepository.findById(1L)).thenReturn(Optional.of(shipment));
        when(shipmentMapper.toDto(shipment)).thenReturn(new ShipmentDTO());

        ShipmentDTO result = shipmentService.getShipmentById(1L);

        assertNotNull(result);
    }

    @Test
    void getShipmentById_NotFound() {
        when(shipmentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> shipmentService.getShipmentById(999L));
    }

    @Test
    void getAllShipments_Success() {
        Shipment shipment1 = new Shipment();
        Shipment shipment2 = new Shipment();

        when(shipmentRepository.findAll()).thenReturn(java.util.List.of(shipment1, shipment2));
        when(shipmentMapper.toDto(any())).thenReturn(new ShipmentDTO());

        var result = shipmentService.getAllShipments();

        assertEquals(2, result.size());
    }

    @Test
    void getShipmentsByStatus_Success() {
        Shipment shipment = new Shipment();
        shipment.setStatus(ShipmentStatus.IN_TRANSIT);

        when(shipmentRepository.findByStatus(ShipmentStatus.IN_TRANSIT))
                .thenReturn(java.util.List.of(shipment));
        when(shipmentMapper.toDto(any())).thenReturn(new ShipmentDTO());

        var result = shipmentService.getShipmentsByStatus(ShipmentStatus.IN_TRANSIT);

        assertEquals(1, result.size());
    }

    @Test
    void markInTransit_ShipmentNotFound() {
        when(shipmentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> shipmentService.markInTransit(999L));
    }

    @Test
    void markDelivered_ShipmentNotFound() {
        when(shipmentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> shipmentService.markDelivered(999L));
    }
}
