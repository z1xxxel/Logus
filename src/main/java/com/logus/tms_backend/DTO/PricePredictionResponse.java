package com.logus.tms_backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PricePredictionResponse {
    private BigDecimal predictedPrice;
    private BigDecimal confidence;
    private String currency;
    private Map<String, Object> factors;
    private String explanation;
}