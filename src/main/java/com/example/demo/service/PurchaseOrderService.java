package com.example.demo.service;

import com.example.demo.dto.CreatePurchaseOrderDTO;
import com.example.demo.dto.PurchaseOrderDTO;
import com.example.demo.dto.ReceivePurchaseOrderDTO;
import com.example.demo.mapper.PurchaseOrderMapper;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class PurchaseOrderService {

    private final PurchaseOrderRepository poRepo;
    private final PurchaseOrderItemRepository poItemRepo;
    private final SupplierRepository supplierRepo;
    private final ProductRepository productRepo;
    private final PurchaseOrderMapper mapper;

    public PurchaseOrderService(PurchaseOrderRepository poRepo, PurchaseOrderItemRepository poItemRepo,
                                SupplierRepository supplierRepo, ProductRepository productRepo,
                                PurchaseOrderMapper mapper) {
        this.poRepo = poRepo;
        this.poItemRepo = poItemRepo;
        this.supplierRepo = supplierRepo;
        this.productRepo = productRepo;
        this.mapper = mapper;
    }

    @Transactional
    public PurchaseOrderDTO createPurchaseOrder(CreatePurchaseOrderDTO dto) {
        Supplier supplier = supplierRepo.findById(dto.getSupplierId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Supplier not found"));

        PurchaseOrder po = new PurchaseOrder();
        po.setSupplier(supplier);
        po.setIssuedDate(LocalDate.now());
        po.setExpectedDeliveryDate(dto.getExpectedDeliveryDate());
        po.setStatus(PurchaseOrder.PurchaseOrderStatus.ISSUED);
        po.setPoNumber(generatePoNumber());

        List<PurchaseOrderItem> items = dto.getItems().stream().map(itemDto -> {
            Product product = productRepo.findById(itemDto.getProductId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found: " + itemDto.getProductId()));
            PurchaseOrderItem item = new PurchaseOrderItem();
            item.setProduct(product);
            item.setQuantityOrdered(itemDto.getQuantityOrdered());
            item.setPrice(itemDto.getPrice());
            item.setPurchaseOrder(po);
            return item;
        }).toList();

        po.setItems(items);
        PurchaseOrder saved = poRepo.save(po);
        return mapper.toDto(saved);
    }

    private String generatePoNumber() {
        return "PO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    @Transactional
    public PurchaseOrderDTO receivePurchaseOrder(ReceivePurchaseOrderDTO dto) {
        PurchaseOrderItem item = poItemRepo.findById(dto.getPurchaseOrderItemId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Purchase order item not found"));

        int quantityToReceive = dto.getQuantityReceived();
        int quantityOrdered = item.getQuantityOrdered();
        int alreadyReceived = item.getQuantityReceived();

        if (quantityToReceive <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity to receive must be positive");
        }

        if (quantityToReceive > (quantityOrdered - alreadyReceived)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot receive more than ordered quantity");
        }

        item.setQuantityReceived(alreadyReceived + quantityToReceive);
        poItemRepo.save(item);

        // TODO: Update product stock in warehouse

        PurchaseOrder po = item.getPurchaseOrder();
        updatePurchaseOrderStatus(po);
        return mapper.toDto(po);
    }

    private void updatePurchaseOrderStatus(PurchaseOrder po) {
        int totalOrdered = 0;
        int totalReceived = 0;

        for (PurchaseOrderItem item : po.getItems()) {
            totalOrdered += item.getQuantityOrdered();
            totalReceived += item.getQuantityReceived();
        }

        if (totalReceived == 0) {
            po.setStatus(PurchaseOrder.PurchaseOrderStatus.ISSUED);
        } else if (totalReceived < totalOrdered) {
            po.setStatus(PurchaseOrder.PurchaseOrderStatus.PARTIALLY_RECEIVED);
        } else {
            po.setStatus(PurchaseOrder.PurchaseOrderStatus.FULLY_RECEIVED);
        }
        poRepo.save(po);
    }

    @Transactional(readOnly = true)
    public PurchaseOrderDTO getPurchaseOrderById(Long id) {
        return poRepo.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Purchase order not found"));
    }
}
