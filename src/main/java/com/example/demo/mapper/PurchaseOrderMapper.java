package com.example.demo.mapper;

import com.example.demo.dto.PurchaseOrderDTO;
import com.example.demo.dto.PurchaseOrderItemDTO;
import com.example.demo.model.PurchaseOrder;
import com.example.demo.model.PurchaseOrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PurchaseOrderMapper {

    @Mapping(source = "supplier.id", target = "supplierId")
    @Mapping(source = "supplier.name", target = "supplierName")
    PurchaseOrderDTO toDto(PurchaseOrder entity);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.sku", target = "productSku")
    PurchaseOrderItemDTO toDto(PurchaseOrderItem entity);
}
