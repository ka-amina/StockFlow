package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.SupplierDTO;
import com.example.demo.service.SupplierService;
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
class SupplierControllerTest {

    @Mock
    private SupplierService supplierService;

    @InjectMocks
    private SupplierController supplierController;

    @Test
    void createSupplier_Success() {
        // ARRANGE
        SupplierDTO requestDto = new SupplierDTO();
        requestDto.setName("Test Supplier");

        SupplierDTO createdDto = new SupplierDTO();
        createdDto.setId(1L);
        createdDto.setName("Test Supplier");

        when(supplierService.createSupplier(any(SupplierDTO.class))).thenReturn(createdDto);

        // ACT
        ResponseEntity<ApiResponse<SupplierDTO>> response = supplierController.createSupplier(requestDto);

        // ASSERT
        assertNotNull(response);
        assertEquals(201, response.getStatusCodeValue());
        assertEquals("Supplier created successfully", response.getBody().getMessage());
        assertEquals(1L, response.getBody().getData().getId());
        verify(supplierService, times(1)).createSupplier(requestDto);
    }

    @Test
    void getSuppliers_Success() {
        // ARRANGE
        SupplierDTO supplier1 = new SupplierDTO();
        supplier1.setId(1L);
        supplier1.setName("Supplier 1");

        SupplierDTO supplier2 = new SupplierDTO();
        supplier2.setId(2L);
        supplier2.setName("Supplier 2");

        List<SupplierDTO> suppliers = Arrays.asList(supplier1, supplier2);
        when(supplierService.getSuppliers()).thenReturn(suppliers);

        // ACT
        ResponseEntity<ApiResponse<List<SupplierDTO>>> response = supplierController.getSuppliers();

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Suppliers retrieved successfully", response.getBody().getMessage());
        assertEquals(2, response.getBody().getData().size());
        verify(supplierService, times(1)).getSuppliers();
    }
}
