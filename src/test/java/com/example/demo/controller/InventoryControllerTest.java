package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.InventoryDTO;
import com.example.demo.dto.InventoryMovementDTO;
import com.example.demo.dto.RecordAdjustmentDTO;
import com.example.demo.dto.RecordInboundDTO;
import com.example.demo.enums.AdjustmentType;
import com.example.demo.service.InventoryService;
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
class InventoryControllerTest {

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private InventoryController inventoryController;

    @Test
    void recordInbound_Success() {
        // ARRANGE
        RecordInboundDTO requestDto = new RecordInboundDTO();
        requestDto.setProductId(1L);
        requestDto.setWarehouseId(1L);
        requestDto.setQuantity(100);
        requestDto.setReferenceDoc("PO-001");

        InventoryMovementDTO movement = new InventoryMovementDTO();
        movement.setId(1L);
        movement.setProductId(1L);
        movement.setWarehouseId(1L);
        movement.setQuantity(100);

        when(inventoryService.recordInbound(any(RecordInboundDTO.class))).thenReturn(movement);

        // ACT
        ResponseEntity<ApiResponse<InventoryMovementDTO>> response = inventoryController.recordInbound(requestDto);

        // ASSERT
        assertNotNull(response);
        assertEquals(201, response.getStatusCode().value());
        assertEquals("Stock entry recorded successfully", response.getBody().getMessage());
        assertNotNull(response.getBody().getData());
        verify(inventoryService, times(1)).recordInbound(requestDto);
    }

    @Test
    void recordAdjustment_Success() {
        // ARRANGE
        RecordAdjustmentDTO requestDto = new RecordAdjustmentDTO();
        requestDto.setProductId(1L);
        requestDto.setWarehouseId(1L);
        requestDto.setQuantity(10);
        requestDto.setAdjustmentType(AdjustmentType.DECREASE);
        requestDto.setReason("Damaged during handling");

        InventoryMovementDTO movement = new InventoryMovementDTO();
        movement.setId(1L);
        movement.setProductId(1L);
        movement.setWarehouseId(1L);
        movement.setQuantity(10);

        when(inventoryService.recordAdjustment(any(RecordAdjustmentDTO.class))).thenReturn(movement);

        // ACT
        ResponseEntity<ApiResponse<InventoryMovementDTO>> response = inventoryController.recordAdjustment(requestDto);

        // ASSERT
        assertNotNull(response);
        assertEquals(201, response.getStatusCode().value());
        assertEquals("Inventory adjustment recorded successfully", response.getBody().getMessage());
        assertNotNull(response.getBody().getData());
        verify(inventoryService, times(1)).recordAdjustment(requestDto);
    }

    @Test
    void getAllInventory_Success() {
        // ARRANGE
        InventoryDTO inventory1 = new InventoryDTO();
        inventory1.setId(1L);
        inventory1.setProductId(1L);
        inventory1.setWarehouseId(1L);
        inventory1.setQtyOnHand(100);

        InventoryDTO inventory2 = new InventoryDTO();
        inventory2.setId(2L);
        inventory2.setProductId(2L);
        inventory2.setWarehouseId(1L);
        inventory2.setQtyOnHand(50);

        List<InventoryDTO> inventoryList = Arrays.asList(inventory1, inventory2);
        when(inventoryService.getAllInventory()).thenReturn(inventoryList);

        // ACT
        ResponseEntity<ApiResponse<List<InventoryDTO>>> response = inventoryController.getAllInventory();

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Inventory retrieved successfully", response.getBody().getMessage());
        assertEquals(2, response.getBody().getData().size());
        verify(inventoryService, times(1)).getAllInventory();
    }

    @Test
    void getInventoryByProductId_Success() {
        // ARRANGE
        InventoryDTO inventory = new InventoryDTO();
        inventory.setId(1L);
        inventory.setProductId(1L);
        inventory.setWarehouseId(1L);
        inventory.setQtyOnHand(100);

        List<InventoryDTO> inventoryList = Arrays.asList(inventory);
        when(inventoryService.getProductInventory(1L)).thenReturn(inventoryList);

        // ACT
        ResponseEntity<ApiResponse<List<InventoryDTO>>> response = inventoryController.getProductInventory(1L);

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Product inventory retrieved successfully", response.getBody().getMessage());
        assertEquals(1, response.getBody().getData().size());
        assertEquals(1L, response.getBody().getData().get(0).getProductId());
        verify(inventoryService, times(1)).getProductInventory(1L);
    }

    @Test
    void getInventoryByWarehouseId_Success() {
        // ARRANGE
        InventoryDTO inventory1 = new InventoryDTO();
        inventory1.setId(1L);
        inventory1.setProductId(1L);
        inventory1.setWarehouseId(1L);
        inventory1.setQtyOnHand(100);

        InventoryDTO inventory2 = new InventoryDTO();
        inventory2.setId(2L);
        inventory2.setProductId(2L);
        inventory2.setWarehouseId(1L);
        inventory2.setQtyOnHand(50);

        List<InventoryDTO> inventoryList = Arrays.asList(inventory1, inventory2);
        when(inventoryService.getWarehouseInventory(1L)).thenReturn(inventoryList);

        // ACT
        ResponseEntity<ApiResponse<List<InventoryDTO>>> response = inventoryController.getWarehouseInventory(1L);

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Warehouse inventory retrieved successfully", response.getBody().getMessage());
        assertEquals(2, response.getBody().getData().size());
        assertEquals(1L, response.getBody().getData().get(0).getWarehouseId());
        verify(inventoryService, times(1)).getWarehouseInventory(1L);
    }

    @Test
    void getInventoryByProductAndWarehouse_Success() {
        // ARRANGE
        InventoryDTO inventory = new InventoryDTO();
        inventory.setId(1L);
        inventory.setProductId(1L);
        inventory.setWarehouseId(1L);
        inventory.setQtyOnHand(100);

        when(inventoryService.getInventory(1L, 1L)).thenReturn(inventory);

        // ACT
        ResponseEntity<ApiResponse<InventoryDTO>> response = inventoryController.getInventory(1L, 1L);

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Inventory retrieved successfully", response.getBody().getMessage());
        assertEquals(1L, response.getBody().getData().getProductId());
        assertEquals(1L, response.getBody().getData().getWarehouseId());
        verify(inventoryService, times(1)).getInventory(1L, 1L);
    }
}
