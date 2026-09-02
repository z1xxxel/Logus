package com.logus.tms_backend.DTO;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CreateOrderRequest {
    private Long customerId;
    private String fromCity;
    private String fromAddress;
    private String toCity;
    private String toAddress;
    private BigDecimal weight;
    private LocalDateTime deadline;

    // 🆕 ID категории груза (опционально)
    private Long categoryId;
}