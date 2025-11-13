package com.example.demo.mapper;

import com.example.demo.dto.SupplierDTO;
import com.example.demo.model.Supplier;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SupplierMapper {
    SupplierDTO toDto(Supplier entity);
    Supplier toEntity(SupplierDTO dto);
}
