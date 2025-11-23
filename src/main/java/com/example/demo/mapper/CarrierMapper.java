package com.example.demo.mapper;

import com.example.demo.dto.CarrierDTO;
import com.example.demo.model.Carrier;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CarrierMapper {

    CarrierDTO toDto(Carrier carrier);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Carrier toEntity(CarrierDTO dto);
}
