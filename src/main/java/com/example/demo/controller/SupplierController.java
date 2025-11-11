package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.SupplierDTO;
import com.example.demo.service.SupplierService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {

    private final SupplierService service;

    public SupplierController(SupplierService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SupplierDTO>> createSupplier(@Valid @RequestBody SupplierDTO dto) {
        SupplierDTO created = service.createSupplier(dto);
        ApiResponse<SupplierDTO> body = new ApiResponse<>("Supplier created successfully", created);
        return ResponseEntity.status(201).body(body);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SupplierDTO>>> getSuppliers() {
        List<SupplierDTO> suppliers = service.getSuppliers();
        ApiResponse<List<SupplierDTO>> body = new ApiResponse<>("Suppliers retrieved successfully", suppliers);
        return ResponseEntity.ok(body);
    }
}
