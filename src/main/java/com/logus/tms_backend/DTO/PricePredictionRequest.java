package com.logus.tms_backend.DTO;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PricePredictionRequest {
    private String fromCity;
    private String toCity;
    private Double weight;
    private Long categoryId;
    private LocalDateTime deadline;
}