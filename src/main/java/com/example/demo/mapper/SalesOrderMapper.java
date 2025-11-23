package com.example.demo.mapper;

import com.example.demo.dto.SalesOrderDTO;
import com.example.demo.dto.SalesOrderLineDTO;
import com.example.demo.model.SalesOrder;
import com.example.demo.model.SalesOrderLine;
import org.mapstruct.*;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface SalesOrderMapper {

    @Mapping(source = "client.id", target = "clientId")
    @Mapping(source = "client.name", target = "clientName")
    @Mapping(source = "warehouse.id", target = "warehouseId")
    @Mapping(source = "warehouse.code", target = "warehouseCode")
    @Mapping(source = "orderLines", target = "orderLines")
    @Mapping(target = "totalAmount", expression = "java(calculateTotalAmount(salesOrder))")
    SalesOrderDTO toDto(SalesOrder salesOrder);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.sku", target = "productSku")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(target = "lineTotal", expression = "java(line.getLineTotal())")
    SalesOrderLineDTO toLineDto(SalesOrderLine line);

    default BigDecimal calculateTotalAmount(SalesOrder salesOrder) {
        if (salesOrder.getOrderLines() == null || salesOrder.getOrderLines().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return salesOrder.getOrderLines().stream()
                .map(SalesOrderLine::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
