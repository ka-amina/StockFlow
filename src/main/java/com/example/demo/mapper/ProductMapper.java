package com.example.demo.mapper;

import org.mapstruct.*;
import com.example.demo.model.Product;
import com.example.demo.dto.ProductDTO;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductDTO toDto(Product entity);
    Product toEntity(ProductDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(ProductDTO dto, @MappingTarget Product entity);
}
