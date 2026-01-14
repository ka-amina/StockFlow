package com.example.demo.service;

import com.example.demo.dto.CreateShipmentDTO;
import com.example.demo.dto.ShipmentDTO;
import com.example.demo.enums.SalesOrderStatus;
import com.example.demo.enums.ShipmentStatus;
import com.example.demo.mapper.ShipmentMapper;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShipmentService {

    private final ShipmentRepository shipmentRepo;
    private final SalesOrderRepository salesOrderRepo;
    private final CarrierRepository carrierRepo;
    private final ShipmentMapper mapper;

    // Cut-off time for same-day shipment (15:00 / 3 PM)
    private static final LocalTime CUT_OFF_TIME = LocalTime.of(15, 0);

    public ShipmentService(ShipmentRepository shipmentRepo,
                           SalesOrderRepository salesOrderRepo,
                           CarrierRepository carrierRepo,
                           ShipmentMapper mapper) {
        this.shipmentRepo = shipmentRepo;
        this.salesOrderRepo = salesOrderRepo;
        this.carrierRepo = carrierRepo;
        this.mapper = mapper;
    }

    @Transactional
    public ShipmentDTO createShipment(CreateShipmentDTO dto) {
        // Validate sales order exists
        SalesOrder salesOrder = salesOrderRepo.findById(dto.getSalesOrderId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Sales order not found with ID: " + dto.getSalesOrderId()));

        // Validate order is RESERVED
        if (salesOrder.getStatus() != SalesOrderStatus.RESERVED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Can only create shipment for RESERVED orders. Current status: " + salesOrder.getStatus());
        }

        // Check if shipment already exists for this order
        if (shipmentRepo.findBySalesOrder(salesOrder).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Shipment already exists for this sales order");
        }

        // Validate carrier if provided
        Carrier carrier = null;
        if (dto.getCarrierId() != null) {
            carrier = carrierRepo.findById(dto.getCarrierId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Carrier not found with ID: " + dto.getCarrierId()));
        }

        // Validate tracking number uniqueness if provided
        if (dto.getTrackingNumber() != null && !dto.getTrackingNumber().isBlank()) {
            if (shipmentRepo.existsByTrackingNumber(dto.getTrackingNumber())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Tracking number already exists: " + dto.getTrackingNumber());
            }
        }

        // Determine planned date with cut-off logic
        LocalDateTime plannedDate = dto.getPlannedDate();
        if (plannedDate == null) {
            plannedDate = calculatePlannedDate();
        }

        // Create shipment
        Shipment shipment = Shipment.builder()
                .salesOrder(salesOrder)
                .carrier(carrier)
                .trackingNumber(dto.getTrackingNumber())
                .status(ShipmentStatus.PLANNED)
                .plannedDate(plannedDate)
                .notes(dto.getNotes())
                .build();

        Shipment savedShipment = shipmentRepo.save(shipment);
        return mapper.toDto(savedShipment);
    }

    @Transactional
    public ShipmentDTO markInTransit(Long shipmentId) {
        Shipment shipment = shipmentRepo.findById(shipmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Shipment not found with ID: " + shipmentId));

        try {
            shipment.markInTransit();
            
            // Update sales order status to SHIPPED
            SalesOrder salesOrder = shipment.getSalesOrder();
            salesOrder.ship();
            salesOrderRepo.save(salesOrder);
            
            Shipment updatedShipment = shipmentRepo.save(shipment);
            return mapper.toDto(updatedShipment);
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Transactional
    public ShipmentDTO markDelivered(Long shipmentId) {
        Shipment shipment = shipmentRepo.findById(shipmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Shipment not found with ID: " + shipmentId));

        try {
            shipment.markDelivered();
            
            // Update sales order status to DELIVERED
            SalesOrder salesOrder = shipment.getSalesOrder();
            salesOrder.deliver();
            salesOrderRepo.save(salesOrder);
            
            Shipment updatedShipment = shipmentRepo.save(shipment);
            return mapper.toDto(updatedShipment);
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Transactional
    public ShipmentDTO updateCarrier(Long shipmentId, Long carrierId) {
        Shipment shipment = shipmentRepo.findById(shipmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Shipment not found with ID: " + shipmentId));

        Carrier carrier = carrierRepo.findById(carrierId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Carrier not found with ID: " + carrierId));

        shipment.setCarrier(carrier);
        Shipment updatedShipment = shipmentRepo.save(shipment);
        return mapper.toDto(updatedShipment);
    }

    @Transactional
    public ShipmentDTO updateTrackingNumber(Long shipmentId, String trackingNumber) {
        Shipment shipment = shipmentRepo.findById(shipmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Shipment not found with ID: " + shipmentId));

        // Validate uniqueness
        if (shipmentRepo.existsByTrackingNumber(trackingNumber)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Tracking number already exists: " + trackingNumber);
        }

        shipment.setTrackingNumber(trackingNumber);
        Shipment updatedShipment = shipmentRepo.save(shipment);
        return mapper.toDto(updatedShipment);
    }

    @Transactional
    public ShipmentDTO updateShipment(Long shipmentId, CreateShipmentDTO dto) {
        Shipment shipment = shipmentRepo.findById(shipmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Shipment not found with ID: " + shipmentId));

        // Update carrier if provided
        if (dto.getCarrierId() != null) {
            Carrier carrier = carrierRepo.findById(dto.getCarrierId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Carrier not found with ID: " + dto.getCarrierId()));
            shipment.setCarrier(carrier);
        }

        // Update tracking number if provided
        if (dto.getTrackingNumber() != null && !dto.getTrackingNumber().isBlank()) {
            if (shipmentRepo.existsByTrackingNumber(dto.getTrackingNumber())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Tracking number already exists: " + dto.getTrackingNumber());
            }
            shipment.setTrackingNumber(dto.getTrackingNumber());
        }

        // Update planned date if provided
        if (dto.getPlannedDate() != null) {
            shipment.setPlannedDate(dto.getPlannedDate());
        }

        // Update notes if provided
        if (dto.getNotes() != null) {
            shipment.setNotes(dto.getNotes());
        }

        Shipment updatedShipment = shipmentRepo.save(shipment);
        return mapper.toDto(updatedShipment);
    }

    @Transactional(readOnly = true)
    public ShipmentDTO getShipmentById(Long id) {
        Shipment shipment = shipmentRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Shipment not found with ID: " + id));
        return mapper.toDto(shipment);
    }

    @Transactional(readOnly = true)
    public ShipmentDTO getShipmentByTrackingNumber(String trackingNumber) {
        Shipment shipment = shipmentRepo.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Shipment not found with tracking number: " + trackingNumber));
        return mapper.toDto(shipment);
    }

    @Transactional(readOnly = true)
    public ShipmentDTO getShipmentBySalesOrder(Long salesOrderId) {
        SalesOrder salesOrder = salesOrderRepo.findById(salesOrderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Sales order not found with ID: " + salesOrderId));

        Shipment shipment = shipmentRepo.findBySalesOrder(salesOrder)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No shipment found for sales order ID: " + salesOrderId));

        return mapper.toDto(shipment);
    }

    @Transactional(readOnly = true)
    public List<ShipmentDTO> getAllShipments() {
        return shipmentRepo.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ShipmentDTO> getShipmentsByStatus(ShipmentStatus status) {
        return shipmentRepo.findByStatus(status).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Calculate planned shipment date based on cut-off time.
     * Orders created after 15:00 are scheduled for next business day.
     */
    private LocalDateTime calculatePlannedDate() {
        LocalDateTime now = LocalDateTime.now();
        
        if (now.toLocalTime().isAfter(CUT_OFF_TIME)) {
            // After cut-off, schedule for next day
            return now.plusDays(1).withHour(9).withMinute(0).withSecond(0).withNano(0);
        } else {
            // Before cut-off, can ship today
            return now.withHour(9).withMinute(0).withSecond(0).withNano(0);
        }
    }
}
