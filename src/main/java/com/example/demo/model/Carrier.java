package com.example.demo.model;

import com.example.demo.model.enums.CarrierStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalTime;

@Entity
@Table(name = "carriers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Carrier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    private String name;

    private String email;

    private String contactPhone;

    private BigDecimal baseShippingRate;

    private Integer maxDailyCapacity;

    private Integer currentDailyShipments;

    private LocalTime cutOffTime;

    @Enumerated(EnumType.STRING)
    private CarrierStatus status;
}
