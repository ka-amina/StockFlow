package com.example.demo.service;

import com.example.demo.dto.CreateSalesOrderDTO;
import com.example.demo.dto.SalesOrderDTO;
import com.example.demo.enums.SalesOrderStatus;
import com.example.demo.mapper.SalesOrderMapper;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SalesOrderService {

    private final SalesOrderRepository salesOrderRepo;
    private final ClientRepository clientRepo;
    private final WarehouseRepository warehouseRepo;
    private final ProductRepository productRepo;
    private final InventoryRepository inventoryRepo;
    private final UserRepository userRepo;
    private final SalesOrderMapper mapper;

    public SalesOrderService(SalesOrderRepository salesOrderRepo,
                             ClientRepository clientRepo,
                             WarehouseRepository warehouseRepo,
                             ProductRepository productRepo,
                             InventoryRepository inventoryRepo,
                             UserRepository userRepo,
                             SalesOrderMapper mapper) {
        this.salesOrderRepo = salesOrderRepo;
        this.clientRepo = clientRepo;
        this.warehouseRepo = warehouseRepo;
        this.productRepo = productRepo;
        this.inventoryRepo = inventoryRepo;
        this.userRepo = userRepo;
        this.mapper = mapper;
    }

    @Transactional
    public SalesOrderDTO createOrder(CreateSalesOrderDTO dto) {
        // Validate client exists and is active
        Client client = clientRepo.findById(dto.getClientId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Client not found with ID: " + dto.getClientId()));

        if (!client.isActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot create order for inactive client");
        }

        // Validate warehouse exists and is active
        Warehouse warehouse = warehouseRepo.findById(dto.getWarehouseId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Warehouse not found with ID: " + dto.getWarehouseId()));

        if (!warehouse.isActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot create order for inactive warehouse");
        }

        // Validate order lines are not empty
        if (dto.getOrderLines() == null || dto.getOrderLines().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Order must contain at least one line");
        }

        // Optionally validate and set user if provided
        User user = null;
        if (dto.getUserId() != null) {
            user = userRepo.findById(dto.getUserId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "User not found with ID: " + dto.getUserId()));
            
            if (!user.getActive()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Cannot assign inactive user to order");
            }
        }

        // Create sales order
        SalesOrder salesOrder = SalesOrder.builder()
                .client(client)
                .warehouse(warehouse)
                .user(user)
                .status(SalesOrderStatus.CREATED)
                .notes(dto.getNotes())
                .build();

        // Add order lines
        for (CreateSalesOrderDTO.CreateSalesOrderLineDTO lineDto : dto.getOrderLines()) {
            // Validate product exists and is active
            Product product = productRepo.findById(lineDto.getProductId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Product not found with ID: " + lineDto.getProductId()));

            if (!product.isActive()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Cannot add inactive product to order: " + product.getSku());
            }

            // Validate quantity is positive
            if (lineDto.getQuantity() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Quantity must be positive for product: " + product.getSku());
            }

            // Validate unit price is positive
            if (lineDto.getUnitPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Unit price must be positive for product: " + product.getSku());
            }

            SalesOrderLine line = SalesOrderLine.builder()
                    .product(product)
                    .quantity(lineDto.getQuantity())
                    .unitPrice(lineDto.getUnitPrice())
                    .build();

            salesOrder.addOrderLine(line);
        }

        SalesOrder savedOrder = salesOrderRepo.save(salesOrder);
        return mapper.toDto(savedOrder);
    }

    @Transactional
    public SalesOrderDTO reserveOrder(Long orderId) {
        // Find the order
        SalesOrder order = salesOrderRepo.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Sales order not found with ID: " + orderId));

        // Validate order is in CREATED status
        if (order.getStatus() != SalesOrderStatus.CREATED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Can only reserve orders in CREATED status. Current status: " + order.getStatus());
        }

        // Check availability for all lines and collect insufficient items
        StringBuilder insufficientStock = new StringBuilder();
        boolean hasInsufficientStock = false;

        for (SalesOrderLine line : order.getOrderLines()) {
            Inventory inventory = inventoryRepo.findByProductAndWarehouse(
                            line.getProduct(), order.getWarehouse())
                    .orElse(null);

            if (inventory == null) {
                hasInsufficientStock = true;
                insufficientStock.append(String.format(
                        "Product '%s' (SKU: %s): No inventory record found in warehouse '%s'. ",
                        line.getProduct().getName(),
                        line.getProduct().getSku(),
                        order.getWarehouse().getCode()
                ));
            } else {
                int available = inventory.getAvailable();
                int required = line.getQuantity();

                if (available < required) {
                    hasInsufficientStock = true;
                    insufficientStock.append(String.format(
                            "Product '%s' (SKU: %s): Required %d, Available %d. ",
                            line.getProduct().getName(),
                            line.getProduct().getSku(),
                            required,
                            available
                    ));
                }
            }
        }

        // If insufficient stock, throw exception with details
        if (hasInsufficientStock) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Insufficient stock for reservation: " + insufficientStock.toString());
        }

        // All checks passed, proceed with reservation
        for (SalesOrderLine line : order.getOrderLines()) {
            Inventory inventory = inventoryRepo.findByProductAndWarehouse(
                            line.getProduct(), order.getWarehouse())
                    .orElseThrow(); // We already validated this exists

            try {
                inventory.increaseQtyReserved(line.getQuantity());
                inventoryRepo.save(inventory);
            } catch (IllegalStateException e) {
                // This should not happen as we validated availability
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Failed to reserve stock for product " + line.getProduct().getSku() +
                                ": " + e.getMessage());
            }
        }

        // Update order status to RESERVED
        order.reserve();
        SalesOrder savedOrder = salesOrderRepo.save(order);

        return mapper.toDto(savedOrder);
    }

    @Transactional
    public SalesOrderDTO cancelOrder(Long orderId) {
        // Find the order
        SalesOrder order = salesOrderRepo.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Sales order not found with ID: " + orderId));

        // If order is RESERVED, release the reservations
        if (order.getStatus() == SalesOrderStatus.RESERVED) {
            for (SalesOrderLine line : order.getOrderLines()) {
                Inventory inventory = inventoryRepo.findByProductAndWarehouse(
                                line.getProduct(), order.getWarehouse())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                                "Inventory not found for reserved order line"));

                try {
                    inventory.decreaseQtyReserved(line.getQuantity());
                    inventoryRepo.save(inventory);
                } catch (IllegalStateException e) {
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Failed to release reservation for product " + line.getProduct().getSku() +
                                    ": " + e.getMessage());
                }
            }
        }

        // Cancel the order
        try {
            order.cancel();
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        SalesOrder savedOrder = salesOrderRepo.save(order);
        return mapper.toDto(savedOrder);
    }

    @Transactional(readOnly = true)
    public SalesOrderDTO getOrderById(Long orderId) {
        SalesOrder order = salesOrderRepo.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Sales order not found with ID: " + orderId));

        return mapper.toDto(order);
    }

    @Transactional(readOnly = true)
    public List<SalesOrderDTO> getAllOrders() {
        return salesOrderRepo.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SalesOrderDTO> getOrdersByStatus(SalesOrderStatus status) {
        return salesOrderRepo.findByStatus(status).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SalesOrderDTO> getOrdersByClient(Long clientId) {
        Client client = clientRepo.findById(clientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Client not found with ID: " + clientId));

        return salesOrderRepo.findByClient(client).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
}
