package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.CreatePurchaseOrderDTO;
import com.example.demo.dto.PurchaseOrderDTO;
import com.example.demo.dto.PurchaseOrderItemDTO;
import com.example.demo.dto.ReceivePurchaseOrderDTO;
import com.example.demo.enums.PurchaseOrderStatus;
import com.example.demo.service.PurchaseOrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderControllerTest {

    @Mock
    private PurchaseOrderService purchaseOrderService;

    @InjectMocks
    private PurchaseOrderController purchaseOrderController;

    @Test
    void createPurchaseOrder_Success() {
        // ARRANGE
        PurchaseOrderItemDTO itemDto = new PurchaseOrderItemDTO();
        itemDto.setProductId(1L);
        itemDto.setQuantityOrdered(100);
        itemDto.setPrice(new BigDecimal("50.00"));

        CreatePurchaseOrderDTO requestDto = new CreatePurchaseOrderDTO();
        requestDto.setSupplierId(1L);
        requestDto.setExpectedDeliveryDate(LocalDate.now().plusDays(7));
        requestDto.setItems(Collections.singletonList(itemDto));

        PurchaseOrderDTO createdDto = new PurchaseOrderDTO();
        createdDto.setId(1L);
        createdDto.setSupplierId(1L);
        createdDto.setStatus(PurchaseOrderStatus.CREATED);

        when(purchaseOrderService.createPurchaseOrder(any(CreatePurchaseOrderDTO.class))).thenReturn(createdDto);

        // ACT
        ResponseEntity<ApiResponse<PurchaseOrderDTO>> response = purchaseOrderController.createPurchaseOrder(requestDto);

        // ASSERT
        assertNotNull(response);
        assertEquals(201, response.getStatusCode().value());
        assertEquals("Purchase order created successfully", response.getBody().getMessage());
        assertNotNull(response.getBody().getData());
        verify(purchaseOrderService, times(1)).createPurchaseOrder(requestDto);
    }

    @Test
    void getPurchaseOrderById_Success() {
        // ARRANGE
        PurchaseOrderDTO order = new PurchaseOrderDTO();
        order.setId(1L);
        order.setSupplierId(1L);
        order.setStatus(PurchaseOrderStatus.CREATED);

        when(purchaseOrderService.getPurchaseOrderById(1L)).thenReturn(order);

        // ACT
        ResponseEntity<ApiResponse<PurchaseOrderDTO>> response = purchaseOrderController.getPurchaseOrderById(1L);

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Purchase order retrieved successfully", response.getBody().getMessage());
        assertEquals(1L, response.getBody().getData().getId());
        verify(purchaseOrderService, times(1)).getPurchaseOrderById(1L);
    }

    @Test
    void receivePurchaseOrder_Success() {
        // ARRANGE
        ReceivePurchaseOrderDTO receiveDto = new ReceivePurchaseOrderDTO();
        receiveDto.setPurchaseOrderItemId(1L);
        receiveDto.setQuantityReceived(100);

        PurchaseOrderDTO receivedDto = new PurchaseOrderDTO();
        receivedDto.setId(1L);
        receivedDto.setStatus(PurchaseOrderStatus.RECEIVED);

        when(purchaseOrderService.receivePurchaseOrder(any(ReceivePurchaseOrderDTO.class))).thenReturn(receivedDto);

        // ACT
        ResponseEntity<ApiResponse<PurchaseOrderDTO>> response = purchaseOrderController.receivePurchaseOrder(1L, receiveDto);

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Purchase order received successfully", response.getBody().getMessage());
        assertEquals(PurchaseOrderStatus.RECEIVED, response.getBody().getData().getStatus());
        verify(purchaseOrderService, times(1)).receivePurchaseOrder(receiveDto);
    }

    @Test
    void cancelPurchaseOrder_Success() {
        // ARRANGE
        PurchaseOrderDTO cancelledDto = new PurchaseOrderDTO();
        cancelledDto.setId(1L);
        cancelledDto.setStatus(PurchaseOrderStatus.CANCELED);

        when(purchaseOrderService.cancelPurchaseOrder(1L)).thenReturn(cancelledDto);

        // ACT
        ResponseEntity<ApiResponse<PurchaseOrderDTO>> response = purchaseOrderController.cancelPurchaseOrder(1L);

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Purchase order cancelled successfully", response.getBody().getMessage());
        assertEquals(PurchaseOrderStatus.CANCELED, response.getBody().getData().getStatus());
        verify(purchaseOrderService, times(1)).cancelPurchaseOrder(1L);
    }
}
