package com.example.demo.mapper;


import com.example.demo.dto.AuthResponseDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UsersMapper {
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "password", ignore = true)
    UserDTO toDto(User user);

    @Mapping(target = "role", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    User toEntity(UserDTO userDTO);
}
