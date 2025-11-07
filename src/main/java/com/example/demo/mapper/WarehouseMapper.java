package com.example.demo.mapper;

import org.mapstruct.*;
import com.example.demo.model.Warehouse;
import com.example.demo.dto.WarehouseDTO;

@Mapper(componentModel = "spring")
public interface WarehouseMapper {
    WarehouseDTO toDto(Warehouse entity);
    Warehouse toEntity(WarehouseDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(WarehouseDTO dto, @MappingTarget Warehouse entity);
}