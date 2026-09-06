package com.logus.tms_backend.DTO;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderFilterDTO {
    private String fromCity;
    private String toCity;
    private BigDecimal minWeight;
    private BigDecimal maxWeight;
    private Boolean urgent;
    private Long categoryId;
}