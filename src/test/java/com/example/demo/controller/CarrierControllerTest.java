package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.CarrierDTO;
import com.example.demo.enums.CarrierStatus;
import com.example.demo.service.CarrierService;
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
class CarrierControllerTest {

    @Mock
    private CarrierService carrierService;

    @InjectMocks
    private CarrierController carrierController;

    @Test
    void createCarrier_Success() {
        // ARRANGE
        CarrierDTO requestDto = new CarrierDTO();
        requestDto.setCode("CARR-001");
        requestDto.setName("Test Carrier");
        requestDto.setContactInfo("contact@carrier.com");
        requestDto.setStatus(CarrierStatus.ACTIVE);

        CarrierDTO createdDto = new CarrierDTO();
        createdDto.setId(1L);
        createdDto.setCode("CARR-001");
        createdDto.setName("Test Carrier");
        createdDto.setStatus(CarrierStatus.ACTIVE);

        when(carrierService.createCarrier(any(CarrierDTO.class))).thenReturn(createdDto);

        // ACT
        ResponseEntity<ApiResponse<CarrierDTO>> response = carrierController.createCarrier(requestDto);

        // ASSERT
        assertNotNull(response);
        assertEquals(201, response.getStatusCodeValue());
        assertEquals("Carrier created successfully", response.getBody().getMessage());
        assertEquals(1L, response.getBody().getData().getId());
        verify(carrierService, times(1)).createCarrier(requestDto);
    }

    @Test
    void getCarrierById_Success() {
        // ARRANGE
        CarrierDTO carrier = new CarrierDTO();
        carrier.setId(1L);
        carrier.setCode("CARR-001");
        carrier.setName("Test Carrier");

        when(carrierService.getCarrierById(1L)).thenReturn(carrier);

        // ACT
        ResponseEntity<ApiResponse<CarrierDTO>> response = carrierController.getCarrierById(1L);

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Carrier retrieved successfully", response.getBody().getMessage());
        assertEquals(1L, response.getBody().getData().getId());
        verify(carrierService, times(1)).getCarrierById(1L);
    }

    @Test
    void getCarrierByCode_Success() {
        // ARRANGE
        CarrierDTO carrier = new CarrierDTO();
        carrier.setId(1L);
        carrier.setCode("CARR-001");
        carrier.setName("Test Carrier");

        when(carrierService.getCarrierByCode("CARR-001")).thenReturn(carrier);

        // ACT
        ResponseEntity<ApiResponse<CarrierDTO>> response = carrierController.getCarrierByCode("CARR-001");

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Carrier retrieved successfully", response.getBody().getMessage());
        assertEquals("CARR-001", response.getBody().getData().getCode());
        verify(carrierService, times(1)).getCarrierByCode("CARR-001");
    }

    @Test
    void getAllCarriers_Success() {
        // ARRANGE
        CarrierDTO carrier1 = new CarrierDTO();
        carrier1.setId(1L);
        carrier1.setCode("CARR-001");

        CarrierDTO carrier2 = new CarrierDTO();
        carrier2.setId(2L);
        carrier2.setCode("CARR-002");

        List<CarrierDTO> carriers = Arrays.asList(carrier1, carrier2);
        when(carrierService.getAllCarriers()).thenReturn(carriers);

        // ACT
        ResponseEntity<ApiResponse<List<CarrierDTO>>> response = carrierController.getAllCarriers(null);

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("All carriers retrieved successfully", response.getBody().getMessage());
        assertEquals(2, response.getBody().getData().size());
        verify(carrierService, times(1)).getAllCarriers();
    }

    @Test
    void getCarriersByStatus_Success() {
        // ARRANGE
        CarrierDTO carrier = new CarrierDTO();
        carrier.setId(1L);
        carrier.setCode("CARR-001");
        carrier.setStatus(CarrierStatus.ACTIVE);

        List<CarrierDTO> carriers = Arrays.asList(carrier);
        when(carrierService.getCarriersByStatus(CarrierStatus.ACTIVE)).thenReturn(carriers);

        // ACT
        ResponseEntity<ApiResponse<List<CarrierDTO>>> response = carrierController.getAllCarriers(CarrierStatus.ACTIVE);

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().getMessage().contains("ACTIVE"));
        assertEquals(1, response.getBody().getData().size());
        verify(carrierService, times(1)).getCarriersByStatus(CarrierStatus.ACTIVE);
    }

    @Test
    void updateCarrier_Success() {
        // ARRANGE
        CarrierDTO requestDto = new CarrierDTO();
        requestDto.setCode("CARR-001");
        requestDto.setName("Updated Carrier");
        requestDto.setStatus(CarrierStatus.ACTIVE);

        CarrierDTO updatedDto = new CarrierDTO();
        updatedDto.setId(1L);
        updatedDto.setCode("CARR-001");
        updatedDto.setName("Updated Carrier");

        when(carrierService.updateCarrier(anyLong(), any(CarrierDTO.class))).thenReturn(updatedDto);

        // ACT
        ResponseEntity<ApiResponse<CarrierDTO>> response = carrierController.updateCarrier(1L, requestDto);

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Carrier updated successfully", response.getBody().getMessage());
        assertEquals("Updated Carrier", response.getBody().getData().getName());
        verify(carrierService, times(1)).updateCarrier(1L, requestDto);
    }

    @Test
    void updateCarrierStatus_Success() {
        // ARRANGE
        doNothing().when(carrierService).updateCarrierStatus(1L, CarrierStatus.INACTIVE);

        // ACT
        ResponseEntity<ApiResponse<Void>> response = carrierController.updateCarrierStatus(1L, CarrierStatus.INACTIVE);

        // ASSERT
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Carrier status updated successfully", response.getBody().getMessage());
        verify(carrierService, times(1)).updateCarrierStatus(1L, CarrierStatus.INACTIVE);
    }
}
