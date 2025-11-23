package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.CarrierDTO;
import com.example.demo.enums.CarrierStatus;
import com.example.demo.service.CarrierService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carriers")
public class CarrierController {

    private final CarrierService carrierService;

    public CarrierController(CarrierService carrierService) {
        this.carrierService = carrierService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CarrierDTO>> createCarrier(@Valid @RequestBody CarrierDTO dto) {
        CarrierDTO createdCarrier = carrierService.createCarrier(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdCarrier, "Carrier created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CarrierDTO>> getCarrierById(@PathVariable Long id) {
        CarrierDTO carrier = carrierService.getCarrierById(id);
        return ResponseEntity.ok(ApiResponse.success(carrier, "Carrier retrieved successfully"));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<CarrierDTO>> getCarrierByCode(@PathVariable String code) {
        CarrierDTO carrier = carrierService.getCarrierByCode(code);
        return ResponseEntity.ok(ApiResponse.success(carrier, "Carrier retrieved successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CarrierDTO>>> getAllCarriers(@RequestParam(required = false) CarrierStatus status) {
        List<CarrierDTO> carriers;
        String message;

        if (status != null) {
            carriers = carrierService.getCarriersByStatus(status);
            message = "Carriers retrieved by status: " + status;
        } else {
            carriers = carrierService.getAllCarriers();
            message = "All carriers retrieved successfully";
        }

        return ResponseEntity.ok(ApiResponse.success(carriers, message));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CarrierDTO>> updateCarrier(
            @PathVariable Long id,
            @Valid @RequestBody CarrierDTO dto) {
        CarrierDTO updatedCarrier = carrierService.updateCarrier(id, dto);
        return ResponseEntity.ok(ApiResponse.success(updatedCarrier, "Carrier updated successfully"));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Void>> updateCarrierStatus(
            @PathVariable Long id,
            @RequestParam CarrierStatus status) {
        carrierService.updateCarrierStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(null, "Carrier status updated successfully"));
    }
}
