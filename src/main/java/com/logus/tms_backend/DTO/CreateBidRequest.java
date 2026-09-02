package com.logus.tms_backend.DTO;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreateBidRequest {
    private Long orderId;
    private Long carrierId;
    private BigDecimal price;
    private Integer estimatedDays;
    private String comment;
}