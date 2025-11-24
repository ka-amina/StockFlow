package com.example.demo.service;

import com.example.demo.dto.CreateSalesOrderDTO;
import com.example.demo.dto.SalesOrderDTO;
import com.example.demo.enums.SalesOrderStatus;
import com.example.demo.mapper.SalesOrderMapper;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SalesOrderServiceTest {

    @Mock private SalesOrderRepository salesOrderRepository;
    @Mock private ClientRepository clientRepository;
    @Mock private WarehouseRepository warehouseRepository;
    @Mock private UserRepository userRepository;
    @Mock private ProductRepository productRepository;
    @Mock private InventoryRepository inventoryRepository;
    @Mock private SalesOrderMapper salesOrderMapper;

    @InjectMocks private SalesOrderService salesOrderService;

    @Test
    void createOrder_Success() {
        // Given
        CreateSalesOrderDTO request = new CreateSalesOrderDTO();
        request.setClientId(1L);
        request.setWarehouseId(1L);
        
        CreateSalesOrderDTO.CreateSalesOrderLineDTO lineDto = new CreateSalesOrderDTO.CreateSalesOrderLineDTO();
        lineDto.setProductId(1L);
        lineDto.setQuantity(5);
        lineDto.setUnitPrice(BigDecimal.valueOf(100));
        request.setOrderLines(List.of(lineDto));

        Client client = new Client();
        client.setId(1L);
        client.setActive(true);
        
        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);
        
        Product product = new Product();
        product.setId(1L);
        product.setSku("SKU-001");
        product.setActive(true);
        
        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setId(1L);
        salesOrder.setStatus(SalesOrderStatus.CREATED);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(salesOrderRepository.save(any(SalesOrder.class))).thenReturn(salesOrder);
        when(salesOrderMapper.toDto(salesOrder)).thenReturn(new SalesOrderDTO());

        // When
        SalesOrderDTO result = salesOrderService.createOrder(request);

        // Then
        assertNotNull(result);
        verify(salesOrderRepository).save(any(SalesOrder.class));
    }

    @Test
    void createOrder_ClientNotFound() {
        // Given
        CreateSalesOrderDTO request = new CreateSalesOrderDTO();
        request.setClientId(999L);
        request.setWarehouseId(1L);
        request.setOrderLines(new ArrayList<>());

        when(clientRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResponseStatusException.class, () ->
                salesOrderService.createOrder(request));
    }

    @Test
    void reserveOrder_Success() {
        // Given
        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setId(1L);
        salesOrder.setStatus(SalesOrderStatus.CREATED);
        salesOrder.setOrderLines(new ArrayList<>());
        
        Warehouse warehouse = new Warehouse();
        salesOrder.setWarehouse(warehouse);

        when(salesOrderRepository.findById(1L)).thenReturn(Optional.of(salesOrder));
        when(salesOrderRepository.save(any())).thenReturn(salesOrder);
        when(salesOrderMapper.toDto(salesOrder)).thenReturn(new SalesOrderDTO());

        // When
        SalesOrderDTO result = salesOrderService.reserveOrder(1L);

        // Then
        assertNotNull(result);
        verify(salesOrderRepository).save(salesOrder);
    }

    @Test
    void reserveOrder_InvalidStatus() {
        // Given
        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setStatus(SalesOrderStatus.SHIPPED);

        when(salesOrderRepository.findById(1L)).thenReturn(Optional.of(salesOrder));

        // When & Then
        assertThrows(ResponseStatusException.class, () ->
                salesOrderService.reserveOrder(1L));
    }

    @Test
    void cancelOrder_Success() {
        // Given
        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setId(1L);
        salesOrder.setStatus(SalesOrderStatus.CREATED);
        salesOrder.setOrderLines(new ArrayList<>());

        when(salesOrderRepository.findById(1L)).thenReturn(Optional.of(salesOrder));
        when(salesOrderRepository.save(any())).thenReturn(salesOrder);
        when(salesOrderMapper.toDto(salesOrder)).thenReturn(new SalesOrderDTO());

        // When
        SalesOrderDTO result = salesOrderService.cancelOrder(1L);

        // Then
        assertNotNull(result);
        verify(salesOrderRepository).save(salesOrder);
    }

    @Test
    void getOrderById_Success() {
        // Given
        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setId(1L);

        when(salesOrderRepository.findById(1L)).thenReturn(Optional.of(salesOrder));
        when(salesOrderMapper.toDto(salesOrder)).thenReturn(new SalesOrderDTO());

        // When
        SalesOrderDTO result = salesOrderService.getOrderById(1L);

        // Then
        assertNotNull(result);
    }

    @Test
    void getOrderById_NotFound() {
        // Given
        when(salesOrderRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResponseStatusException.class, () ->
                salesOrderService.getOrderById(999L));
    }
}
