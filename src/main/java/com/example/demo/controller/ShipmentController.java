package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.CreateShipmentDTO;
import com.example.demo.dto.ShipmentDTO;
import com.example.demo.enums.ShipmentStatus;
import com.example.demo.service.ShipmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shipments")
public class ShipmentController {

    private final ShipmentService shipmentService;

    public ShipmentController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ShipmentDTO>> createShipment(@Valid @RequestBody CreateShipmentDTO dto) {
        ShipmentDTO createdShipment = shipmentService.createShipment(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdShipment, "Shipment created successfully"));
    }

    @PostMapping("/{id}/in-transit")
    public ResponseEntity<ApiResponse<ShipmentDTO>> markInTransit(@PathVariable Long id) {
        ShipmentDTO shipment = shipmentService.markInTransit(id);
        return ResponseEntity.ok(ApiResponse.success(shipment, "Shipment marked as in transit"));
    }

    @PostMapping("/{id}/delivered")
    public ResponseEntity<ApiResponse<ShipmentDTO>> markDelivered(@PathVariable Long id) {
        ShipmentDTO shipment = shipmentService.markDelivered(id);
        return ResponseEntity.ok(ApiResponse.success(shipment, "Shipment marked as delivered"));
    }

    @PutMapping("/{id}/carrier")
    public ResponseEntity<ApiResponse<ShipmentDTO>> updateCarrier(
            @PathVariable Long id,
            @RequestParam Long carrierId) {
        ShipmentDTO shipment = shipmentService.updateCarrier(id, carrierId);
        return ResponseEntity.ok(ApiResponse.success(shipment, "Carrier updated successfully"));
    }

    @PutMapping("/{id}/tracking")
    public ResponseEntity<ApiResponse<ShipmentDTO>> updateTrackingNumber(
            @PathVariable Long id,
            @RequestParam String trackingNumber) {
        ShipmentDTO shipment = shipmentService.updateTrackingNumber(id, trackingNumber);
        return ResponseEntity.ok(ApiResponse.success(shipment, "Tracking number updated successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ShipmentDTO>> getShipmentById(@PathVariable Long id) {
        ShipmentDTO shipment = shipmentService.getShipmentById(id);
        return ResponseEntity.ok(ApiResponse.success(shipment, "Shipment retrieved successfully"));
    }

    @GetMapping("/tracking/{trackingNumber}")
    public ResponseEntity<ApiResponse<ShipmentDTO>> getShipmentByTrackingNumber(@PathVariable String trackingNumber) {
        ShipmentDTO shipment = shipmentService.getShipmentByTrackingNumber(trackingNumber);
        return ResponseEntity.ok(ApiResponse.success(shipment, "Shipment retrieved successfully"));
    }

    @GetMapping("/order/{salesOrderId}")
    public ResponseEntity<ApiResponse<ShipmentDTO>> getShipmentBySalesOrder(@PathVariable Long salesOrderId) {
        ShipmentDTO shipment = shipmentService.getShipmentBySalesOrder(salesOrderId);
        return ResponseEntity.ok(ApiResponse.success(shipment, "Shipment retrieved successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ShipmentDTO>>> getAllShipments(@RequestParam(required = false) ShipmentStatus status) {
        List<ShipmentDTO> shipments;
        String message;

        if (status != null) {
            shipments = shipmentService.getShipmentsByStatus(status);
            message = "Shipments retrieved by status: " + status;
        } else {
            shipments = shipmentService.getAllShipments();
            message = "All shipments retrieved successfully";
        }

        return ResponseEntity.ok(ApiResponse.success(shipments, message));
    }
}
