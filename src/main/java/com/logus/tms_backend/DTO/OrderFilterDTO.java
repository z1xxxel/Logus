package com.logus.tms_backend.DTO;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderFilterDTO {
    private String fromCity;
    private String toCity;
    private BigDecimal minWeight;
    private BigDecimal maxWeight;
    private Integer maxEstimatedDays;
    private Boolean urgent;

    // 🆕 Фильтр по категории
    private Long categoryId;
}