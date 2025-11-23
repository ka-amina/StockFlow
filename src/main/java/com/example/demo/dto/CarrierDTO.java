package com.example.demo.dto;

import com.example.demo.enums.CarrierStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarrierDTO {
    private Long id;
    
    @NotBlank(message = "Carrier code is required")
    private String code;
    
    @NotBlank(message = "Carrier name is required")
    private String name;
    
    private String contactInfo;
    
    @NotNull(message = "Status is required")
    private CarrierStatus status;
    
    private LocalDateTime createdAt;
}
