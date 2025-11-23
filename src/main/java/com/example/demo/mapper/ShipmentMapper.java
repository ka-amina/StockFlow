package com.example.demo.mapper;

import com.example.demo.dto.ShipmentDTO;
import com.example.demo.model.Shipment;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ShipmentMapper {

    @Mapping(source = "salesOrder.id", target = "salesOrderId")
    @Mapping(target = "salesOrderReference", expression = "java(generateOrderReference(shipment))")
    @Mapping(source = "carrier.id", target = "carrierId")
    @Mapping(source = "carrier.name", target = "carrierName")
    @Mapping(source = "carrier.code", target = "carrierCode")
    ShipmentDTO toDto(Shipment shipment);

    default String generateOrderReference(Shipment shipment) {
        if (shipment.getSalesOrder() != null) {
            return "SO-" + shipment.getSalesOrder().getId();
        }
        return null;
    }
}
