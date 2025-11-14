package com.example.demo.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {
    private Long id;

    @NotBlank(message = "SKU must not be blank")
    private String sku;

    @NotBlank(message = "Name must not be blank")
    private String name;

    private String category;

    @Builder.Default
    private boolean active = true;

    private String description;

    @DecimalMin(value = "0.0", inclusive = true, message = "originalPrice must be >= 0")
    private BigDecimal originalPrice;

    private BigDecimal profit;
}
