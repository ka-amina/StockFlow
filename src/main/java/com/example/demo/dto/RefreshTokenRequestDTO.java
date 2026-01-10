package com.example.demo.dto;


import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RefreshTokenRequestDTO {
    private String refreshToken;
}
