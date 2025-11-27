package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.CreateSalesOrderDTO;
import com.example.demo.dto.SalesOrderDTO;
import com.example.demo.dto.ShipmentDTO;
import com.example.demo.enums.SalesOrderStatus;
import com.example.demo.enums.ShipmentStatus;
import com.example.demo.service.SalesOrderService;
import com.example.demo.service.ShipmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SalesOrderControllerTest {

    @Mock
    private SalesOrderService salesOrderService;

    @Mock
    private ShipmentService shipmentService;

    @InjectMocks
    private SalesOrderController salesOrderController;

    @Test
    void createSalesOrder_Success() {
        // ARRANGE
        CreateSalesOrderDTO.CreateSalesOrderLineDTO lineDto = CreateSalesOrderDTO.CreateSalesOrderLineDTO.builder()
                .productId(1L)
                .quantity(10)
                .unitPrice(new BigDecimal("99.99"))
                .build();

        CreateSalesOrderDTO requestDto = CreateSalesOrderDTO.builder()
                .clientId(1L)
                .warehouseId(1L)
                .orderLines(Collections.singletonList(lineDto))
                .build();

        SalesOrderDTO createdDto = new SalesOrderDTO();
        createdDto.setId(1L);
        createdDto.setClientId(1L);
        createdDto.setStatus(SalesOrderStatus.CREATED);

        when(salesOrderService.createOrder(any(CreateSalesOrderDTO.class))).thenReturn(createdDto);

        // ACT
        ResponseEntity<ApiResponse<SalesOrderDTO>> response = salesOrderController.createOrder(requestDto);

        // ASSERT
        assertNotNull(response);
        assertEquals(201, response.getStatusCode().value());
        assertEquals("Sales order created successfully", response.getBody().getMessage());
        assertNotNull(response.getBody().getData());
        verify(salesOrderService, times(1)).createOrder(requestDto);
    }

    @Test
    void getSalesOrderById_Success() {
        // ARRANGE
        SalesOrderDTO order = new SalesOrderDTO();
        order.setId(1L);
        order.setClientId(1L);
        order.setStatus(SalesOrderStatus.CREATED);

        when(salesOrderService.getOrderById(1L)).thenReturn(order);

        // ACT
        ResponseEntity<ApiResponse<SalesOrderDTO>> response = salesOrderController.getOrderById(1L);

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Sales order retrieved successfully", response.getBody().getMessage());
        assertEquals(1L, response.getBody().getData().getId());
        verify(salesOrderService, times(1)).getOrderById(1L);
    }

    @Test
    void getAllSalesOrders_Success() {
        // ARRANGE
        SalesOrderDTO order1 = new SalesOrderDTO();
        order1.setId(1L);
        order1.setStatus(SalesOrderStatus.CREATED);

        SalesOrderDTO order2 = new SalesOrderDTO();
        order2.setId(2L);
        order2.setStatus(SalesOrderStatus.RESERVED);

        List<SalesOrderDTO> orders = Arrays.asList(order1, order2);
        when(salesOrderService.getAllOrders()).thenReturn(orders);

        // ACT
        ResponseEntity<ApiResponse<List<SalesOrderDTO>>> response = salesOrderController.getAllOrders(null, null);

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("All sales orders retrieved successfully", response.getBody().getMessage());
        assertEquals(2, response.getBody().getData().size());
        verify(salesOrderService, times(1)).getAllOrders();
    }

    @Test
    void getSalesOrdersByClientId_Success() {
        // ARRANGE
        SalesOrderDTO order1 = new SalesOrderDTO();
        order1.setId(1L);
        order1.setClientId(1L);
        order1.setStatus(SalesOrderStatus.CREATED);

        List<SalesOrderDTO> orders = Arrays.asList(order1);
        when(salesOrderService.getOrdersByClient(1L)).thenReturn(orders);

        // ACT
        ResponseEntity<ApiResponse<List<SalesOrderDTO>>> response = salesOrderController.getAllOrders(null, 1L);

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Sales orders retrieved for client ID: 1", response.getBody().getMessage());
        assertEquals(1, response.getBody().getData().size());
        assertEquals(1L, response.getBody().getData().get(0).getClientId());
        verify(salesOrderService, times(1)).getOrdersByClient(1L);
    }

    @Test
    void reserveInventory_Success() {
        // ARRANGE
        SalesOrderDTO order = new SalesOrderDTO();
        order.setId(1L);
        order.setStatus(SalesOrderStatus.RESERVED);

        when(salesOrderService.reserveOrder(1L)).thenReturn(order);

        // ACT
        ResponseEntity<ApiResponse<SalesOrderDTO>> response = salesOrderController.reserveOrder(1L);

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Sales order reserved successfully", response.getBody().getMessage());
        assertEquals(SalesOrderStatus.RESERVED, response.getBody().getData().getStatus());
        verify(salesOrderService, times(1)).reserveOrder(1L);
    }

    @Test
    void shipSalesOrder_Success() {
        // ARRANGE
        ShipmentDTO shipment = new ShipmentDTO();
        shipment.setId(1L);
        shipment.setSalesOrderId(1L);

        ShipmentDTO transitShipment = new ShipmentDTO();
        transitShipment.setId(1L);
        transitShipment.setStatus(ShipmentStatus.IN_TRANSIT);

        SalesOrderDTO order = new SalesOrderDTO();
        order.setId(1L);
        order.setStatus(SalesOrderStatus.SHIPPED);

        when(shipmentService.getShipmentBySalesOrder(1L)).thenReturn(shipment);
        when(shipmentService.markInTransit(1L)).thenReturn(transitShipment);
        when(salesOrderService.getOrderById(1L)).thenReturn(order);

        // ACT
        ResponseEntity<ApiResponse<SalesOrderDTO>> response = salesOrderController.markOrderShipped(1L);

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Sales order marked as SHIPPED", response.getBody().getMessage());
        verify(shipmentService, times(1)).getShipmentBySalesOrder(1L);
        verify(shipmentService, times(1)).markInTransit(1L);
        verify(salesOrderService, times(1)).getOrderById(1L);
    }

    @Test
    void cancelSalesOrder_Success() {
        // ARRANGE
        SalesOrderDTO order = new SalesOrderDTO();
        order.setId(1L);
        order.setStatus(SalesOrderStatus.CANCELED);

        when(salesOrderService.cancelOrder(1L)).thenReturn(order);

        // ACT
        ResponseEntity<ApiResponse<SalesOrderDTO>> response = salesOrderController.cancelOrder(1L);

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Sales order canceled successfully", response.getBody().getMessage());
        assertEquals(SalesOrderStatus.CANCELED, response.getBody().getData().getStatus());
        verify(salesOrderService, times(1)).cancelOrder(1L);
    }

    @Test
    void completeSalesOrder_Success() {
        // ARRANGE
        ShipmentDTO shipment = new ShipmentDTO();
        shipment.setId(1L);
        shipment.setSalesOrderId(1L);

        ShipmentDTO deliveredShipment = new ShipmentDTO();
        deliveredShipment.setId(1L);
        deliveredShipment.setStatus(ShipmentStatus.DELIVERED);

        SalesOrderDTO order = new SalesOrderDTO();
        order.setId(1L);
        order.setStatus(SalesOrderStatus.DELIVERED);

        when(shipmentService.getShipmentBySalesOrder(1L)).thenReturn(shipment);
        when(shipmentService.markDelivered(1L)).thenReturn(deliveredShipment);
        when(salesOrderService.getOrderById(1L)).thenReturn(order);

        // ACT
        ResponseEntity<ApiResponse<SalesOrderDTO>> response = salesOrderController.markOrderDelivered(1L);

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Sales order marked as DELIVERED", response.getBody().getMessage());
        verify(shipmentService, times(1)).getShipmentBySalesOrder(1L);
        verify(shipmentService, times(1)).markDelivered(1L);
        verify(salesOrderService, times(1)).getOrderById(1L);
    }
}
