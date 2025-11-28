package com.example.demo.service;

import com.example.demo.dto.SupplierDTO;
import com.example.demo.mapper.SupplierMapper;
import com.example.demo.model.Supplier;
import com.example.demo.repository.SupplierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupplierServiceTest {

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private SupplierMapper supplierMapper;

    @InjectMocks
    private SupplierService supplierService;

    private SupplierDTO supplierDTO;
    private Supplier supplier;

    @BeforeEach
    void setUp() {
        supplierDTO = new SupplierDTO();
        supplierDTO.setId(1L);
        supplierDTO.setName("Test Supplier");

        supplier = new Supplier();
        supplier.setId(1L);
        supplier.setName("Test Supplier");
    }

    @Test
    void createSupplier_Success() {
        when(supplierMapper.toEntity(any(SupplierDTO.class))).thenReturn(supplier);
        when(supplierRepository.save(any(Supplier.class))).thenReturn(supplier);
        when(supplierMapper.toDto(any(Supplier.class))).thenReturn(supplierDTO);

        SupplierDTO result = supplierService.createSupplier(supplierDTO);

        assertNotNull(result);
        assertEquals("Test Supplier", result.getName());
        verify(supplierMapper).toEntity(supplierDTO);
        verify(supplierRepository).save(supplier);
        verify(supplierMapper).toDto(supplier);
    }

    @Test
    void createSupplier_WithMinimalInfo_Success() {
        SupplierDTO minimalDTO = new SupplierDTO();
        minimalDTO.setName("Minimal Supplier");

        Supplier minimalSupplier = new Supplier();
        minimalSupplier.setName("Minimal Supplier");

        when(supplierMapper.toEntity(any(SupplierDTO.class))).thenReturn(minimalSupplier);
        when(supplierRepository.save(any(Supplier.class))).thenReturn(minimalSupplier);
        when(supplierMapper.toDto(any(Supplier.class))).thenReturn(minimalDTO);

        SupplierDTO result = supplierService.createSupplier(minimalDTO);

        assertNotNull(result);
        assertEquals("Minimal Supplier", result.getName());
        verify(supplierRepository).save(any(Supplier.class));
    }

    @Test
    void getSuppliers_Success() {
        Supplier supplier2 = new Supplier();
        supplier2.setId(2L);
        supplier2.setName("Second Supplier");

        SupplierDTO supplierDTO2 = new SupplierDTO();
        supplierDTO2.setId(2L);
        supplierDTO2.setName("Second Supplier");

        when(supplierRepository.findAll()).thenReturn(Arrays.asList(supplier, supplier2));
        when(supplierMapper.toDto(supplier)).thenReturn(supplierDTO);
        when(supplierMapper.toDto(supplier2)).thenReturn(supplierDTO2);

        List<SupplierDTO> result = supplierService.getSuppliers();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Test Supplier", result.get(0).getName());
        assertEquals("Second Supplier", result.get(1).getName());
        verify(supplierRepository).findAll();
        verify(supplierMapper, times(2)).toDto(any(Supplier.class));
    }

    @Test
    void getSuppliers_EmptyList_Success() {
        when(supplierRepository.findAll()).thenReturn(Collections.emptyList());

        List<SupplierDTO> result = supplierService.getSuppliers();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(supplierRepository).findAll();
        verify(supplierMapper, never()).toDto(any(Supplier.class));
    }

    @Test
    void getSuppliers_SingleSupplier_Success() {
        when(supplierRepository.findAll()).thenReturn(Collections.singletonList(supplier));
        when(supplierMapper.toDto(supplier)).thenReturn(supplierDTO);

        List<SupplierDTO> result = supplierService.getSuppliers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Supplier", result.get(0).getName());
        verify(supplierRepository).findAll();
        verify(supplierMapper).toDto(supplier);
    }

    @Test
    void createSupplier_WithDifferentName_Success() {
        SupplierDTO differentDTO = new SupplierDTO();
        differentDTO.setName("Different Supplier");

        Supplier differentSupplier = new Supplier();
        differentSupplier.setName("Different Supplier");

        when(supplierMapper.toEntity(differentDTO)).thenReturn(differentSupplier);
        when(supplierRepository.save(differentSupplier)).thenReturn(differentSupplier);
        when(supplierMapper.toDto(differentSupplier)).thenReturn(differentDTO);

        SupplierDTO result = supplierService.createSupplier(differentDTO);

        assertNotNull(result);
        assertEquals("Different Supplier", result.getName());
        verify(supplierRepository).save(differentSupplier);
    }

    @Test
    void getSuppliers_VerifyMapperCalls() {
        List<Supplier> suppliers = Arrays.asList(supplier, new Supplier());
        when(supplierRepository.findAll()).thenReturn(suppliers);
        when(supplierMapper.toDto(any(Supplier.class))).thenReturn(supplierDTO);

        List<SupplierDTO> result = supplierService.getSuppliers();

        assertEquals(2, result.size());
        verify(supplierMapper, times(2)).toDto(any(Supplier.class));
    }
}
