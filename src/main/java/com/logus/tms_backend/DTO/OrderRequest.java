package com.logus.tms_backend.DTO;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OrderRequest {
    private Long customerId;
    private String fromCity;
    private String fromAddress;
    private String toCity;
    private String toAddress;
    private Double weight;
    private LocalDateTime deadline;
    private Long categoryId;
}