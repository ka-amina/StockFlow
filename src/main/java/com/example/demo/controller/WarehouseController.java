package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.WarehouseDTO;
import com.example.demo.service.WarehouseService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/warehouses")
public class WarehouseController {

    private final WarehouseService service;

    public WarehouseController(WarehouseService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<WarehouseDTO>> createWarehouse(@Valid @RequestBody WarehouseDTO dto) {
        WarehouseDTO created = service.createWarehouse(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(created.getId()).toUri();
        ApiResponse<WarehouseDTO> body = new ApiResponse<>("Warehouse created successfully", created);
        return ResponseEntity.created(location).body(body);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<WarehouseDTO>>> getAllWarehouses() {
        List<WarehouseDTO> warehouses = service.getAllWarehouses();
        ApiResponse<List<WarehouseDTO>> body = new ApiResponse<>("Warehouses retrieved successfully", warehouses);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WarehouseDTO>> getWarehouseById(@PathVariable Long id) {
        WarehouseDTO warehouse = service.getWarehouseById(id);
        ApiResponse<WarehouseDTO> body = new ApiResponse<>("Warehouse retrieved successfully", warehouse);
        return ResponseEntity.ok(body);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<WarehouseDTO>> updateWarehouse(@PathVariable Long id, @Valid @RequestBody WarehouseDTO dto) {
        WarehouseDTO updated = service.updateWarehouse(id, dto);
        ApiResponse<WarehouseDTO> body = new ApiResponse<>("Warehouse updated successfully", updated);
        return ResponseEntity.ok(body);
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<WarehouseDTO>> activateWarehouse(@PathVariable Long id) {
        WarehouseDTO updated = service.activateWarehouse(id);
        ApiResponse<WarehouseDTO> body = new ApiResponse<>("Warehouse activated successfully", updated);
        return ResponseEntity.ok(body);
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<WarehouseDTO>> deactivateWarehouse(@PathVariable Long id) {
        WarehouseDTO updated = service.deactivateWarehouse(id);
        ApiResponse<WarehouseDTO> body = new ApiResponse<>("Warehouse deactivated successfully", updated);
        return ResponseEntity.ok(body);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteWarehouse(@PathVariable Long id) {
        service.deleteWarehouse(id);
        ApiResponse<Void> body = new ApiResponse<>("Warehouse deleted successfully", null);
        return ResponseEntity.ok(body);
    }
}