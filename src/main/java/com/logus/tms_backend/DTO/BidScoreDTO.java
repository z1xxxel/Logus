package com.logus.tms_backend.DTO;

import com.logus.tms_backend.model.Bid;

public record BidScoreDTO(
        Bid bid,
        Double smartScore,
        String recommendation,
        Double delayRiskPercent // Вероятность срыва срока (от 0.0 до 100.0)
) {}