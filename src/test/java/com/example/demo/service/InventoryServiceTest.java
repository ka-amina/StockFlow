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

    @Test
    void recordInbound_InactiveProduct() {
        RecordInboundDTO dto = new RecordInboundDTO();
        dto.setProductId(1L);
        dto.setWarehouseId(1L);
        dto.setQuantity(100);

        Product product = new Product();
        product.setActive(false);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThrows(ResponseStatusException.class,
                () -> inventoryService.recordInbound(dto));
    }

    @Test
    void recordInbound_InactiveWarehouse() {
        RecordInboundDTO dto = new RecordInboundDTO();
        dto.setProductId(1L);
        dto.setWarehouseId(1L);
        dto.setQuantity(100);

        Product product = new Product();
        product.setActive(true);
        Warehouse warehouse = new Warehouse();
        warehouse.setActive(false);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));

        assertThrows(ResponseStatusException.class,
                () -> inventoryService.recordInbound(dto));
    }

    @Test
    void recordAdjustment_Increase() {
        com.example.demo.dto.RecordAdjustmentDTO dto = new com.example.demo.dto.RecordAdjustmentDTO();
        dto.setProductId(1L);
        dto.setWarehouseId(1L);
        dto.setQuantity(50);
        dto.setAdjustmentType(com.example.demo.enums.AdjustmentType.INCREASE);

        Product product = new Product();
        product.setActive(true);
        Warehouse warehouse = new Warehouse();
        warehouse.setActive(true);

        Inventory inventory = Inventory.builder()
                .product(product)
                .warehouse(warehouse)
                .qtyOnHand(100)
                .qtyReserved(0)
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(inventoryRepository.findByProductAndWarehouse(product, warehouse)).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(any())).thenReturn(inventory);
        when(inventoryMovementRepository.save(any())).thenReturn(new com.example.demo.model.InventoryMovement());
        when(inventoryMapper.toDto(any(com.example.demo.model.InventoryMovement.class))).thenReturn(new InventoryMovementDTO());

        InventoryMovementDTO result = inventoryService.recordAdjustment(dto);

        assertNotNull(result);
        verify(inventoryRepository).save(any());
    }

    @Test
    void recordAdjustment_Decrease() {
        com.example.demo.dto.RecordAdjustmentDTO dto = new com.example.demo.dto.RecordAdjustmentDTO();
        dto.setProductId(1L);
        dto.setWarehouseId(1L);
        dto.setQuantity(30);
        dto.setAdjustmentType(com.example.demo.enums.AdjustmentType.DECREASE);

        Product product = new Product();
        product.setActive(true);
        Warehouse warehouse = new Warehouse();
        warehouse.setActive(true);

        Inventory inventory = Inventory.builder()
                .product(product)
                .warehouse(warehouse)
                .qtyOnHand(100)
                .qtyReserved(20)
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(inventoryRepository.findByProductAndWarehouse(product, warehouse)).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(any())).thenReturn(inventory);
        when(inventoryMovementRepository.save(any())).thenReturn(new com.example.demo.model.InventoryMovement());
        when(inventoryMapper.toDto(any(com.example.demo.model.InventoryMovement.class))).thenReturn(new InventoryMovementDTO());

        InventoryMovementDTO result = inventoryService.recordAdjustment(dto);

        assertNotNull(result);
        verify(inventoryRepository).save(any());
    }

    @Test
    void recordAdjustment_NegativeStock() {
        com.example.demo.dto.RecordAdjustmentDTO dto = new com.example.demo.dto.RecordAdjustmentDTO();
        dto.setProductId(1L);
        dto.setWarehouseId(1L);
        dto.setQuantity(150);
        dto.setAdjustmentType(com.example.demo.enums.AdjustmentType.DECREASE);

        Product product = new Product();
        product.setActive(true);
        Warehouse warehouse = new Warehouse();
        warehouse.setActive(true);

        Inventory inventory = Inventory.builder()
                .product(product)
                .warehouse(warehouse)
                .qtyOnHand(100)
                .qtyReserved(0)
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(inventoryRepository.findByProductAndWarehouse(product, warehouse)).thenReturn(Optional.of(inventory));

        assertThrows(ResponseStatusException.class,
                () -> inventoryService.recordAdjustment(dto));
    }

    @Test
    void recordAdjustment_BelowReserved() {
        com.example.demo.dto.RecordAdjustmentDTO dto = new com.example.demo.dto.RecordAdjustmentDTO();
        dto.setProductId(1L);
        dto.setWarehouseId(1L);
        dto.setQuantity(60);
        dto.setAdjustmentType(com.example.demo.enums.AdjustmentType.DECREASE);

        Product product = new Product();
        product.setActive(true);
        Warehouse warehouse = new Warehouse();
        warehouse.setActive(true);

        Inventory inventory = Inventory.builder()
                .product(product)
                .warehouse(warehouse)
                .qtyOnHand(100)
                .qtyReserved(50)
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(inventoryRepository.findByProductAndWarehouse(product, warehouse)).thenReturn(Optional.of(inventory));

        assertThrows(ResponseStatusException.class,
                () -> inventoryService.recordAdjustment(dto));
    }

    @Test
    void getWarehouseInventory_Success() {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);
        Inventory inv1 = new Inventory();
        Inventory inv2 = new Inventory();

        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(inventoryRepository.findByWarehouse(warehouse)).thenReturn(java.util.List.of(inv1, inv2));
        when(inventoryMapper.toDto(any(Inventory.class))).thenReturn(new InventoryDTO());

        var result = inventoryService.getWarehouseInventory(1L);

        assertEquals(2, result.size());
    }

    @Test
    void getProductInventory_Success() {
        Product product = new Product();
        product.setId(1L);
        Inventory inv1 = new Inventory();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(inventoryRepository.findByProduct(product)).thenReturn(java.util.List.of(inv1));
        when(inventoryMapper.toDto(any(Inventory.class))).thenReturn(new InventoryDTO());

        var result = inventoryService.getProductInventory(1L);

        assertEquals(1, result.size());
    }

    @Test
    void getAllInventory_Success() {
        Inventory inv1 = new Inventory();
        Inventory inv2 = new Inventory();

        when(inventoryRepository.findAll()).thenReturn(java.util.List.of(inv1, inv2));
        when(inventoryMapper.toDto(any(Inventory.class))).thenReturn(new InventoryDTO());

        var result = inventoryService.getAllInventory();

        assertEquals(2, result.size());
    }

    @Test
    void getWarehouseMovements_Success() {
        Warehouse warehouse = new Warehouse();
        com.example.demo.model.InventoryMovement mov1 = new com.example.demo.model.InventoryMovement();

        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        when(inventoryMovementRepository.findByWarehouse(warehouse))
                .thenReturn(java.util.List.of(mov1));
        when(inventoryMapper.toDto(any(com.example.demo.model.InventoryMovement.class)))
                .thenReturn(new InventoryMovementDTO());

        var result = inventoryService.getWarehouseMovements(1L);

        assertEquals(1, result.size());
    }

    @Test
    void getRecentMovements_Success() {
        com.example.demo.model.InventoryMovement mov1 = new com.example.demo.model.InventoryMovement();

        when(inventoryMovementRepository.findTop50ByOrderByOccurredAtDesc())
                .thenReturn(java.util.List.of(mov1));
        when(inventoryMapper.toDto(any(com.example.demo.model.InventoryMovement.class)))
                .thenReturn(new InventoryMovementDTO());

        var result = inventoryService.getRecentMovements();

        assertEquals(1, result.size());
    }
}
