package com.example.demo.service;

import com.example.demo.dto.InventoryDTO;
import com.example.demo.dto.InventoryMovementDTO;
import com.example.demo.dto.RecordAdjustmentDTO;
import com.example.demo.dto.RecordInboundDTO;
import com.example.demo.enums.AdjustmentType;
import com.example.demo.enums.MovementType;
import com.example.demo.mapper.InventoryMapper;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepo;
    private final InventoryMovementRepository movementRepo;
    private final ProductRepository productRepo;
    private final WarehouseRepository warehouseRepo;
    private final InventoryMapper mapper;

    public InventoryService(InventoryRepository inventoryRepo,
                            InventoryMovementRepository movementRepo,
                            ProductRepository productRepo,
                            WarehouseRepository warehouseRepo,
                            InventoryMapper mapper) {
        this.inventoryRepo = inventoryRepo;
        this.movementRepo = movementRepo;
        this.productRepo = productRepo;
        this.warehouseRepo = warehouseRepo;
        this.mapper = mapper;
    }

    @Transactional
    public InventoryMovementDTO recordInbound(RecordInboundDTO dto) {
        // Validate product exists and is active
        Product product = productRepo.findById(dto.getProductId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Product not found"));

        if (!product.isActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot record movement for inactive product");
        }

        // Validate warehouse exists and is active
        Warehouse warehouse = warehouseRepo.findById(dto.getWarehouseId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Warehouse not found"));

        if (!warehouse.isActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot record movement for inactive warehouse");
        }

        // Find or create inventory record
        Inventory inventory = inventoryRepo.findByProductAndWarehouse(product, warehouse)
                .orElseGet(() -> {
                    Inventory newInventory = Inventory.builder()
                            .product(product)
                            .warehouse(warehouse)
                            .qtyOnHand(0)
                            .qtyReserved(0)
                            .build();
                    return inventoryRepo.save(newInventory);
                });

        // Update inventory quantity
        inventory.increaseQtyOnHand(dto.getQuantity());
        inventoryRepo.save(inventory);

        // Record movement history
        InventoryMovement movement = InventoryMovement.builder()
                .product(product)
                .warehouse(warehouse)
                .type(MovementType.INBOUND)
                .quantity(dto.getQuantity())
                .occurredAt(LocalDateTime.now())
                .referenceDoc(dto.getReferenceDoc())
                .notes(dto.getNotes())
                .build();

        InventoryMovement savedMovement = movementRepo.save(movement);
        return mapper.toDto(savedMovement);
    }

    // inventory correction
    @Transactional
    public InventoryMovementDTO recordAdjustment(RecordAdjustmentDTO dto) {
        // Validate product exists and is active
        Product product = productRepo.findById(dto.getProductId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Product not found"));

        if (!product.isActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot record adjustment for inactive product");
        }

        // Validate warehouse exists and is active
        Warehouse warehouse = warehouseRepo.findById(dto.getWarehouseId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Warehouse not found"));

        if (!warehouse.isActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot record adjustment for inactive warehouse");
        }

        // Get existing inventory record (must exist for adjustments)
        Inventory inventory = inventoryRepo.findByProductAndWarehouse(product, warehouse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No inventory found for this product in this warehouse. Use INBOUND to create initial stock."));

        int adjustmentQuantity = dto.getQuantityAdjustment();
        int signedQuantity;

        // Determine if increase or decrease
        if (dto.getAdjustmentType() == AdjustmentType.INCREASE) {
            // Increase stock
            inventory.increaseQtyOnHand(adjustmentQuantity);
            signedQuantity = adjustmentQuantity;
        } else {
            // Decrease stock
            int newQtyOnHand = inventory.getQtyOnHand() - adjustmentQuantity;

            if (newQtyOnHand < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Adjustment would result in negative stock. Current: "
                                + inventory.getQtyOnHand() +
                                ", Decrease: " + adjustmentQuantity);
            }

            if (newQtyOnHand < inventory.getQtyReserved()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Cannot adjust stock below reserved quantity. Reserved: "
                                + inventory.getQtyReserved() +
                                ", New quantity would be: " + newQtyOnHand);
            }

            inventory.decreaseQtyOnHand(adjustmentQuantity);
            signedQuantity = -adjustmentQuantity;
        }

        inventoryRepo.save(inventory);

        // Record movement history (with signed quantity for clarity)
        InventoryMovement movement = InventoryMovement.builder()
                .product(product)
                .warehouse(warehouse)
                .type(MovementType.ADJUSTMENT)
                .quantity(signedQuantity)
                .occurredAt(LocalDateTime.now())
                .referenceDoc(dto.getReferenceDoc())
                .notes(dto.getAdjustmentType() + ": " + dto.getReason())
                .build();

        InventoryMovement savedMovement = movementRepo.save(movement);
        return mapper.toDto(savedMovement);
    }

    // Get inventory for a specific product in a specific warehouse
    @Transactional(readOnly = true)
    public InventoryDTO getInventory(Long productId, Long warehouseId) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Product not found"));

        Warehouse warehouse = warehouseRepo.findById(warehouseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Warehouse not found"));

        Inventory inventory = inventoryRepo.findByProductAndWarehouse(product, warehouse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No inventory found for this product in this warehouse"));

        return mapper.toDto(inventory);
    }

    // Get all inventory records for a warehouse
    @Transactional(readOnly = true)
    public List<InventoryDTO> getWarehouseInventory(Long warehouseId) {
        Warehouse warehouse = warehouseRepo.findById(warehouseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Warehouse not found"));

        return inventoryRepo.findByWarehouse(warehouse).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    // Get all inventory records for a product across all warehouses
    @Transactional(readOnly = true)
    public List<InventoryDTO> getProductInventory(Long productId) {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Product not found"));

        return inventoryRepo.findByProduct(product).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    // Get all inventory movements for a warehouse
    @Transactional(readOnly = true)
    public List<InventoryMovementDTO> getWarehouseMovements(Long warehouseId) {
        Warehouse warehouse = warehouseRepo.findById(warehouseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Warehouse not found"));

        return movementRepo.findByWarehouse(warehouse).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    // Get recent inventory movements

    @Transactional(readOnly = true)
    public List<InventoryMovementDTO> getRecentMovements() {
        return movementRepo.findTop50ByOrderByOccurredAtDesc().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    // Get all inventory records
    @Transactional(readOnly = true)
    public List<InventoryDTO> getAllInventory() {
        return inventoryRepo.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
}
