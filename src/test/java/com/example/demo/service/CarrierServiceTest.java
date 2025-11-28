package com.example.demo.service;

import com.example.demo.dto.CarrierDTO;
import com.example.demo.enums.CarrierStatus;
import com.example.demo.mapper.CarrierMapper;
import com.example.demo.model.Carrier;
import com.example.demo.repository.CarrierRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarrierServiceTest {

    @Mock private CarrierRepository carrierRepo;
    @Mock private CarrierMapper mapper;

    @InjectMocks private CarrierService carrierService;

    @Test
    void createCarrier_Success() {
        // Given
        CarrierDTO dto = new CarrierDTO();
        dto.setCode("CARR-001");
        dto.setName("Test Carrier");
        dto.setStatus(CarrierStatus.ACTIVE);

        Carrier carrier = new Carrier();
        carrier.setId(1L);
        carrier.setCode("CARR-001");

        when(carrierRepo.existsByCode("CARR-001")).thenReturn(false);
        when(mapper.toEntity(dto)).thenReturn(carrier);
        when(carrierRepo.save(carrier)).thenReturn(carrier);
        when(mapper.toDto(carrier)).thenReturn(dto);

        // When
        CarrierDTO result = carrierService.createCarrier(dto);

        // Then
        assertNotNull(result);
        verify(carrierRepo).save(carrier);
    }

    @Test
    void createCarrier_DuplicateCode() {
        // Given
        CarrierDTO dto = new CarrierDTO();
        dto.setCode("CARR-001");

        when(carrierRepo.existsByCode("CARR-001")).thenReturn(true);

        // When & Then
        assertThrows(ResponseStatusException.class, () ->
                carrierService.createCarrier(dto));
        verify(carrierRepo, never()).save(any());
    }

    @Test
    void getCarrierById_Success() {
        // Given
        Carrier carrier = new Carrier();
        carrier.setId(1L);
        carrier.setCode("CARR-001");

        when(carrierRepo.findById(1L)).thenReturn(Optional.of(carrier));
        when(mapper.toDto(carrier)).thenReturn(new CarrierDTO());

        // When
        CarrierDTO result = carrierService.getCarrierById(1L);

        // Then
        assertNotNull(result);
    }

    @Test
    void getCarrierById_NotFound() {
        // Given
        when(carrierRepo.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResponseStatusException.class, () ->
                carrierService.getCarrierById(999L));
    }

    @Test
    void getCarrierByCode_Success() {
        // Given
        Carrier carrier = new Carrier();
        carrier.setCode("CARR-001");

        when(carrierRepo.findByCode("CARR-001")).thenReturn(Optional.of(carrier));
        when(mapper.toDto(carrier)).thenReturn(new CarrierDTO());

        // When
        CarrierDTO result = carrierService.getCarrierByCode("CARR-001");

        // Then
        assertNotNull(result);
    }

    @Test
    void getCarrierByCode_NotFound() {
        // Given
        when(carrierRepo.findByCode("INVALID")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResponseStatusException.class, () ->
                carrierService.getCarrierByCode("INVALID"));
    }

    @Test
    void getAllCarriers_Success() {
        // Given
        Carrier carrier1 = new Carrier();
        Carrier carrier2 = new Carrier();
        List<Carrier> carriers = Arrays.asList(carrier1, carrier2);

        when(carrierRepo.findAll()).thenReturn(carriers);
        when(mapper.toDto(any())).thenReturn(new CarrierDTO());

        // When
        List<CarrierDTO> result = carrierService.getAllCarriers();

        // Then
        assertEquals(2, result.size());
    }

    @Test
    void getCarriersByStatus_Success() {
        // Given
        Carrier carrier = new Carrier();
        carrier.setStatus(CarrierStatus.ACTIVE);
        List<Carrier> carriers = Arrays.asList(carrier);

        when(carrierRepo.findByStatus(CarrierStatus.ACTIVE)).thenReturn(carriers);
        when(mapper.toDto(any())).thenReturn(new CarrierDTO());

        // When
        List<CarrierDTO> result = carrierService.getCarriersByStatus(CarrierStatus.ACTIVE);

        // Then
        assertEquals(1, result.size());
    }

    @Test
    void updateCarrier_Success() {
        // Given
        CarrierDTO dto = new CarrierDTO();
        dto.setCode("CARR-001");
        dto.setName("Updated Carrier");
        dto.setStatus(CarrierStatus.ACTIVE);

        Carrier carrier = new Carrier();
        carrier.setId(1L);
        carrier.setCode("CARR-001");

        when(carrierRepo.findById(1L)).thenReturn(Optional.of(carrier));
        when(carrierRepo.save(carrier)).thenReturn(carrier);
        when(mapper.toDto(carrier)).thenReturn(dto);

        // When
        CarrierDTO result = carrierService.updateCarrier(1L, dto);

        // Then
        assertNotNull(result);
        verify(carrierRepo).save(carrier);
    }

    @Test
    void updateCarrier_NotFound() {
        // Given
        CarrierDTO dto = new CarrierDTO();
        when(carrierRepo.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResponseStatusException.class, () ->
                carrierService.updateCarrier(999L, dto));
    }

    @Test
    void updateCarrier_DuplicateCodeChange() {
        // Given
        CarrierDTO dto = new CarrierDTO();
        dto.setCode("CARR-002");

        Carrier carrier = new Carrier();
        carrier.setId(1L);
        carrier.setCode("CARR-001");

        when(carrierRepo.findById(1L)).thenReturn(Optional.of(carrier));
        when(carrierRepo.existsByCode("CARR-002")).thenReturn(true);

        // When & Then
        assertThrows(ResponseStatusException.class, () ->
                carrierService.updateCarrier(1L, dto));
    }

    @Test
    void updateCarrierStatus_Success() {
        // Given
        Carrier carrier = new Carrier();
        carrier.setId(1L);
        carrier.setStatus(CarrierStatus.ACTIVE);

        when(carrierRepo.findById(1L)).thenReturn(Optional.of(carrier));

        // When
        carrierService.updateCarrierStatus(1L, CarrierStatus.INACTIVE);

        // Then
        assertEquals(CarrierStatus.INACTIVE, carrier.getStatus());
        verify(carrierRepo).save(carrier);
    }

    @Test
    void updateCarrierStatus_NotFound() {
        // Given
        when(carrierRepo.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResponseStatusException.class, () ->
                carrierService.updateCarrierStatus(999L, CarrierStatus.INACTIVE));
    }

    @Test
    void deleteCarrier_Success() {
        Carrier carrier = new Carrier();
        carrier.setId(1L);

        when(carrierRepo.findById(1L)).thenReturn(Optional.of(carrier));

        carrierService.updateCarrierStatus(1L, CarrierStatus.INACTIVE);

        assertEquals(CarrierStatus.INACTIVE, carrier.getStatus());
        verify(carrierRepo).save(carrier);
    }

    @Test
    void getAllCarriers_EmptyList() {
        when(carrierRepo.findAll()).thenReturn(List.of());

        List<CarrierDTO> result = carrierService.getAllCarriers();

        assertTrue(result.isEmpty());
    }

    @Test
    void getCarriersByStatus_EmptyList() {
        when(carrierRepo.findByStatus(CarrierStatus.ACTIVE)).thenReturn(List.of());

        List<CarrierDTO> result = carrierService.getCarriersByStatus(CarrierStatus.ACTIVE);

        assertTrue(result.isEmpty());
    }

    @Test
    void updateCarrier_SameCode_Success() {
        CarrierDTO dto = new CarrierDTO();
        dto.setCode("CARR-001");
        dto.setName("Updated Name");
        dto.setStatus(CarrierStatus.ACTIVE);

        Carrier carrier = new Carrier();
        carrier.setId(1L);
        carrier.setCode("CARR-001");

        when(carrierRepo.findById(1L)).thenReturn(Optional.of(carrier));
        when(carrierRepo.save(carrier)).thenReturn(carrier);
        when(mapper.toDto(carrier)).thenReturn(dto);

        CarrierDTO result = carrierService.updateCarrier(1L, dto);

        assertNotNull(result);
        verify(carrierRepo).save(carrier);
        verify(carrierRepo, never()).existsByCode("CARR-001");
    }
}
