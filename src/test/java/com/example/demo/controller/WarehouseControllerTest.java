package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.WarehouseDTO;
import com.example.demo.service.WarehouseService;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WarehouseControllerTest {

    @Mock
    private WarehouseService warehouseService;

    @InjectMocks
    private WarehouseController warehouseController;

    @Test
    void getWarehouseById_Success() {
        // ARRANGE
        WarehouseDTO warehouse = new WarehouseDTO();
        warehouse.setId(1L);
        warehouse.setCode("WH001");
        warehouse.setName("Main Warehouse");
        warehouse.setActive(true);

        when(warehouseService.getWarehouseById(1L)).thenReturn(warehouse);

        // ACT
        ResponseEntity<ApiResponse<WarehouseDTO>> response = warehouseController.getWarehouseById(1L);

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Warehouse retrieved successfully", response.getBody().getMessage());
        assertEquals(1L, response.getBody().getData().getId());
        verify(warehouseService, times(1)).getWarehouseById(1L);
    }

    @Test
    void getAllWarehouses_Success() {
        // ARRANGE
        WarehouseDTO warehouse1 = new WarehouseDTO();
        warehouse1.setId(1L);
        warehouse1.setCode("WH001");
        warehouse1.setName("Main Warehouse");

        WarehouseDTO warehouse2 = new WarehouseDTO();
        warehouse2.setId(2L);
        warehouse2.setCode("WH002");
        warehouse2.setName("Secondary Warehouse");

        List<WarehouseDTO> warehouses = Arrays.asList(warehouse1, warehouse2);
        when(warehouseService.getAllWarehouses()).thenReturn(warehouses);

        // ACT
        ResponseEntity<ApiResponse<List<WarehouseDTO>>> response = warehouseController.getAllWarehouses();

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Warehouses retrieved successfully", response.getBody().getMessage());
        assertEquals(2, response.getBody().getData().size());
        verify(warehouseService, times(1)).getAllWarehouses();
    }

    @Test
    void getWarehouseByCode_Success() {
        // ARRANGE
        WarehouseDTO warehouse = new WarehouseDTO();
        warehouse.setId(1L);
        warehouse.setCode("WH001");
        warehouse.setName("Main Warehouse");
        warehouse.setActive(true);

        when(warehouseService.getWarehouseById(1L)).thenReturn(warehouse);

        // ACT
        ResponseEntity<ApiResponse<WarehouseDTO>> response = warehouseController.getWarehouseById(1L);

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Warehouse retrieved successfully", response.getBody().getMessage());
        assertEquals("WH001", response.getBody().getData().getCode());
        verify(warehouseService, times(1)).getWarehouseById(1L);
    }

    @Test
    void updateWarehouse_Success() {
        // ARRANGE
        WarehouseDTO requestDto = new WarehouseDTO();
        requestDto.setCode("WH001");
        requestDto.setName("Updated Warehouse");

        WarehouseDTO updatedDto = new WarehouseDTO();
        updatedDto.setId(1L);
        updatedDto.setCode("WH001");
        updatedDto.setName("Updated Warehouse");
        updatedDto.setActive(true);

        when(warehouseService.updateWarehouse(anyLong(), any(WarehouseDTO.class))).thenReturn(updatedDto);

        // ACT
        ResponseEntity<ApiResponse<WarehouseDTO>> response = warehouseController.updateWarehouse(1L, requestDto);

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Warehouse updated successfully", response.getBody().getMessage());
        assertEquals("Updated Warehouse", response.getBody().getData().getName());
        verify(warehouseService, times(1)).updateWarehouse(1L, requestDto);
    }

    @Test
    void activateWarehouse_Success() {
        // ARRANGE
        WarehouseDTO warehouse = new WarehouseDTO();
        warehouse.setId(1L);
        warehouse.setCode("WH001");
        warehouse.setName("Main Warehouse");
        warehouse.setActive(true);

        when(warehouseService.activateWarehouse(1L)).thenReturn(warehouse);

        // ACT
        ResponseEntity<ApiResponse<WarehouseDTO>> response = warehouseController.activateWarehouse(1L);

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Warehouse activated successfully", response.getBody().getMessage());
        assertTrue(response.getBody().getData().isActive());
        verify(warehouseService, times(1)).activateWarehouse(1L);
    }

    @Test
    void deactivateWarehouse_Success() {
        // ARRANGE
        WarehouseDTO warehouse = new WarehouseDTO();
        warehouse.setId(1L);
        warehouse.setCode("WH001");
        warehouse.setName("Main Warehouse");
        warehouse.setActive(false);

        when(warehouseService.deactivateWarehouse(1L)).thenReturn(warehouse);

        // ACT
        ResponseEntity<ApiResponse<WarehouseDTO>> response = warehouseController.deactivateWarehouse(1L);

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Warehouse deactivated successfully", response.getBody().getMessage());
        assertFalse(response.getBody().getData().isActive());
        verify(warehouseService, times(1)).deactivateWarehouse(1L);
    }
}
