package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TokenResponseDTO {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Integer expiresIn;
}
