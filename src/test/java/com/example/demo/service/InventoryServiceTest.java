package com.example.demo.service;

import com.example.demo.dto.InventoryDTO;
import com.example.demo.dto.InventoryMovementDTO;
import com.example.demo.dto.RecordInboundDTO;
import com.example.demo.mapper.InventoryMapper;
import com.example.demo.model.Inventory;
import com.example.demo.model.Product;
import com.example.demo.model.Warehouse;
import com.example.demo.repository.InventoryMovementRepository;
import com.example.demo.repository.InventoryRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.WarehouseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock private InventoryRepository inventoryRepository;
    @Mock private InventoryMovementRepository inventoryMovementRepository;
    @Mock private ProductRepository productRepository;
    @Mock private WarehouseRepository warehouseRepository;
    @Mock private InventoryMapper inventoryMapper;

    @InjectMocks private InventoryService inventoryService;

    @Test
    void recordInbound_Success() {
        // Given
        RecordInboundDTO dto = new RecordInboundDTO();
        dto.setProductId(1L);
        dto.setWarehouseId(1L);
        dto.setQuantity(100);

        Product product = new Product();
        product.setId(1L);
        product.setActive(true);

        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);

        Inventory inventory = Inventory.builder()
                .product(product)
                .warehouse(warehouse)
                .qtyOnHand(50)
                .qtyReserved(0)
                .build();

        com.example.demo.model.InventoryMovement movement = new com.example.demo.model.InventoryMovement();
        InventoryMovementDTO movementDTO = new InventoryMovementDTO();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(inventoryRepository.findByProductAndWarehouse(product, warehouse)).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);
        when(inventoryMovementRepository.save(any())).thenReturn(movement);
        when(inventoryMapper.toDto(movement)).thenReturn(movementDTO);

        // When
        InventoryMovementDTO result = inventoryService.recordInbound(dto);

        // Then
        assertNotNull(result);
        verify(inventoryRepository).save(any(Inventory.class));
        verify(inventoryMovementRepository).save(any());
    }

    @Test
    void recordInbound_ProductNotFound() {
        // Given
        RecordInboundDTO dto = new RecordInboundDTO();
        dto.setProductId(999L);
        dto.setWarehouseId(1L);
        dto.setQuantity(100);

        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResponseStatusException.class, () ->
                inventoryService.recordInbound(dto));
    }

    @Test
    void getInventory_Success() {
        // Given
        Product product = new Product();
        product.setId(1L);
        
        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);

        Inventory inventory = Inventory.builder()
                .product(product)
                .warehouse(warehouse)
                .qtyOnHand(100)
                .qtyReserved(20)
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(inventoryRepository.findByProductAndWarehouse(product, warehouse)).thenReturn(Optional.of(inventory));
        when(inventoryMapper.toDto(inventory)).thenReturn(new InventoryDTO());

        // When
        InventoryDTO result = inventoryService.getInventory(1L, 1L);

        // Then
        assertNotNull(result);
        verify(inventoryMapper).toDto(inventory);
    }

    @Test
    void getInventory_NotFound() {
        // Given
        Product product = new Product();
        Warehouse warehouse = new Warehouse();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(inventoryRepository.findByProductAndWarehouse(product, warehouse)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResponseStatusException.class, () ->
                inventoryService.getInventory(1L, 1L));
    }
}
