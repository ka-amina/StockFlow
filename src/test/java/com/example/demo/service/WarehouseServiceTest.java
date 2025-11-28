package com.example.demo.service;

import com.example.demo.dto.WarehouseDTO;
import com.example.demo.mapper.WarehouseMapper;
import com.example.demo.model.Warehouse;
import com.example.demo.repository.WarehouseRepository;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WarehouseServiceTest {

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private WarehouseMapper warehouseMapper;

    @InjectMocks
    private WarehouseService warehouseService;

    private WarehouseDTO warehouseDTO;
    private Warehouse warehouse;

    @BeforeEach
    void setUp() {
        warehouseDTO = WarehouseDTO.builder()
                .id(1L)
                .code("WH001")
                .name("Main Warehouse")
                .active(true)
                .build();

        warehouse = Warehouse.builder()
                .id(1L)
                .code("WH001")
                .name("Main Warehouse")
                .active(true)
                .build();
    }

    @Test
    void createWarehouse_Success() {
        when(warehouseRepository.existsByCode(anyString())).thenReturn(false);
        when(warehouseMapper.toEntity(any(WarehouseDTO.class))).thenReturn(warehouse);
        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(warehouse);
        when(warehouseMapper.toDto(any(Warehouse.class))).thenReturn(warehouseDTO);

        WarehouseDTO result = warehouseService.createWarehouse(warehouseDTO);

        assertNotNull(result);
        assertEquals("WH001", result.getCode());
        verify(warehouseRepository).existsByCode("WH001");
        verify(warehouseRepository).save(any(Warehouse.class));
    }

    @Test
    void createWarehouse_BlankCode_ThrowsException() {
        WarehouseDTO invalidDTO = WarehouseDTO.builder()
                .code("")
                .name("Test Warehouse")
                .build();

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> warehouseService.createWarehouse(invalidDTO));

        assertTrue(exception.getReason().contains("Code must not be blank"));
        verify(warehouseRepository, never()).save(any());
    }

    @Test
    void createWarehouse_NullCode_ThrowsException() {
        WarehouseDTO invalidDTO = WarehouseDTO.builder()
                .name("Test Warehouse")
                .build();

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> warehouseService.createWarehouse(invalidDTO));

        assertTrue(exception.getReason().contains("Code must not be blank"));
        verify(warehouseRepository, never()).save(any());
    }

    @Test
    void createWarehouse_DuplicateCode_ThrowsException() {
        when(warehouseRepository.existsByCode(anyString())).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> warehouseService.createWarehouse(warehouseDTO));

        assertTrue(exception.getReason().contains("Code already exists"));
        verify(warehouseRepository, never()).save(any());
    }

    @Test
    void getAllWarehouses_Success() {
        Warehouse warehouse2 = Warehouse.builder()
                .id(2L)
                .code("WH002")
                .name("Secondary Warehouse")
                .build();

        WarehouseDTO warehouseDTO2 = WarehouseDTO.builder()
                .id(2L)
                .code("WH002")
                .name("Secondary Warehouse")
                .build();

        when(warehouseRepository.findAll()).thenReturn(Arrays.asList(warehouse, warehouse2));
        when(warehouseMapper.toDto(warehouse)).thenReturn(warehouseDTO);
        when(warehouseMapper.toDto(warehouse2)).thenReturn(warehouseDTO2);

        List<WarehouseDTO> result = warehouseService.getAllWarehouses();

        assertEquals(2, result.size());
        assertEquals("WH001", result.get(0).getCode());
        assertEquals("WH002", result.get(1).getCode());
        verify(warehouseRepository).findAll();
    }

    @Test
    void getAllWarehouses_EmptyList() {
        when(warehouseRepository.findAll()).thenReturn(Arrays.asList());

        List<WarehouseDTO> result = warehouseService.getAllWarehouses();

        assertTrue(result.isEmpty());
        verify(warehouseRepository).findAll();
    }

    @Test
    void getWarehouseById_Success() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(warehouseMapper.toDto(any(Warehouse.class))).thenReturn(warehouseDTO);

        WarehouseDTO result = warehouseService.getWarehouseById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("WH001", result.getCode());
        verify(warehouseRepository).findById(1L);
    }

    @Test
    void getWarehouseById_NotFound_ThrowsException() {
        when(warehouseRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> warehouseService.getWarehouseById(999L));

        assertTrue(exception.getReason().contains("Warehouse not found"));
        verify(warehouseRepository).findById(999L);
    }

    @Test
    void updateWarehouse_Success() {
        WarehouseDTO updateDTO = WarehouseDTO.builder()
                .code("WH001")
                .name("Updated Warehouse")
                .build();

        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(warehouseRepository.findByCode("WH001")).thenReturn(Optional.of(warehouse));
        doNothing().when(warehouseMapper).updateEntityFromDto(any(), any());
        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(warehouse);
        when(warehouseMapper.toDto(any(Warehouse.class))).thenReturn(updateDTO);

        WarehouseDTO result = warehouseService.updateWarehouse(1L, updateDTO);

        assertNotNull(result);
        verify(warehouseRepository).findById(1L);
        verify(warehouseRepository).save(any(Warehouse.class));
    }

    @Test
    void updateWarehouse_NotFound_ThrowsException() {
        when(warehouseRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> warehouseService.updateWarehouse(999L, warehouseDTO));

        assertTrue(exception.getReason().contains("Warehouse not found"));
        verify(warehouseRepository, never()).save(any());
    }

    @Test
    void updateWarehouse_DuplicateCode_ThrowsException() {
        Warehouse existingWarehouse = Warehouse.builder()
                .id(2L)
                .code("WH002")
                .build();

        WarehouseDTO updateDTO = WarehouseDTO.builder()
                .code("WH002")
                .name("Updated Warehouse")
                .build();

        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(warehouseRepository.findByCode("WH002")).thenReturn(Optional.of(existingWarehouse));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> warehouseService.updateWarehouse(1L, updateDTO));

        assertTrue(exception.getReason().contains("Code already exists"));
        verify(warehouseRepository, never()).save(any());
    }

    @Test
    void activateWarehouse_Success() {
        Warehouse inactiveWarehouse = Warehouse.builder()
                .id(1L)
                .active(false)
                .build();

        WarehouseDTO activeDTO = WarehouseDTO.builder()
                .id(1L)
                .active(true)
                .build();

        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(inactiveWarehouse));
        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(warehouse);
        when(warehouseMapper.toDto(any(Warehouse.class))).thenReturn(activeDTO);

        WarehouseDTO result = warehouseService.activateWarehouse(1L);

        assertNotNull(result);
        assertTrue(result.isActive());
        verify(warehouseRepository).save(any(Warehouse.class));
    }

    @Test
    void deactivateWarehouse_Success() {
        WarehouseDTO inactiveDTO = WarehouseDTO.builder()
                .id(1L)
                .active(false)
                .build();

        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(warehouse);
        when(warehouseMapper.toDto(any(Warehouse.class))).thenReturn(inactiveDTO);

        WarehouseDTO result = warehouseService.deactivateWarehouse(1L);

        assertNotNull(result);
        assertFalse(result.isActive());
        verify(warehouseRepository).save(any(Warehouse.class));
    }

    @Test
    void deleteWarehouse_Success() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        doNothing().when(warehouseRepository).delete(any(Warehouse.class));

        assertDoesNotThrow(() -> warehouseService.deleteWarehouse(1L));

        verify(warehouseRepository).findById(1L);
        verify(warehouseRepository).delete(warehouse);
    }

    @Test
    void deleteWarehouse_NotFound_ThrowsException() {
        when(warehouseRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> warehouseService.deleteWarehouse(999L));

        assertTrue(exception.getReason().contains("Warehouse not found"));
        verify(warehouseRepository, never()).delete(any());
    }
}
