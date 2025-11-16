package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.InventoryDTO;
import com.example.demo.dto.InventoryMovementDTO;
import com.example.demo.dto.RecordInboundDTO;
import com.example.demo.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/inbound")
    public ResponseEntity<ApiResponse<InventoryMovementDTO>> recordInbound(@Valid @RequestBody RecordInboundDTO dto) {
        InventoryMovementDTO movement = inventoryService.recordInbound(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(movement, "Stock entry recorded successfully"));
    }

    // Get all inventory records

    @GetMapping
    public ResponseEntity<ApiResponse<List<InventoryDTO>>> getAllInventory() {
        List<InventoryDTO> inventory = inventoryService.getAllInventory();
        return ResponseEntity.ok(ApiResponse.success(inventory, "Inventory retrieved successfully"));
    }

    //  Get inventory for a specific product in a specific warehouse
    @GetMapping("/product/{productId}/warehouse/{warehouseId}")
    public ResponseEntity<ApiResponse<InventoryDTO>> getInventory(
            @PathVariable Long productId,
            @PathVariable Long warehouseId) {
        InventoryDTO inventory = inventoryService.getInventory(productId, warehouseId);
        return ResponseEntity.ok(ApiResponse.success(inventory, "Inventory retrieved successfully"));
    }


    //  Get all inventory for a specific warehouse
    @GetMapping("/warehouse/{warehouseId}")
    public ResponseEntity<ApiResponse<List<InventoryDTO>>> getWarehouseInventory(@PathVariable Long warehouseId) {
        List<InventoryDTO> inventory = inventoryService.getWarehouseInventory(warehouseId);
        return ResponseEntity.ok(ApiResponse.success(inventory, "Warehouse inventory retrieved successfully"));
    }


    //  Get all inventory for a specific product across all warehouses
    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<List<InventoryDTO>>> getProductInventory(@PathVariable Long productId) {
        List<InventoryDTO> inventory = inventoryService.getProductInventory(productId);
        return ResponseEntity.ok(ApiResponse.success(inventory, "Product inventory retrieved successfully"));
    }

    // Get all movements for a warehouse
    @GetMapping("/movements/warehouse/{warehouseId}")
    public ResponseEntity<ApiResponse<List<InventoryMovementDTO>>> getWarehouseMovements(
            @PathVariable Long warehouseId) {
        List<InventoryMovementDTO> movements = inventoryService.getWarehouseMovements(warehouseId);
        return ResponseEntity.ok(ApiResponse.success(movements, "Warehouse movements retrieved successfully"));
    }

    //  Get recent inventory movements
    @GetMapping("/movements/recent")
    public ResponseEntity<ApiResponse<List<InventoryMovementDTO>>> getRecentMovements() {
        List<InventoryMovementDTO> movements = inventoryService.getRecentMovements();
        return ResponseEntity.ok(ApiResponse.success(movements, "Recent movements retrieved successfully"));
    }
}
