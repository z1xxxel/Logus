package com.logus.tms_backend.service;

import com.logus.tms_backend.model.Bid;
import com.logus.tms_backend.model.User;
import org.springframework.stereotype.Service;

@Service
public class RiskPredictionService {

    /**
     * Эмуляция ML-модели прогнозирования риска срыва сроков
     */
    public double calculateDelayRisk(Bid bid, User carrier) {
        double baseRisk = 10.0; // Базовый риск

        // Штраф за низкий рейтинг
        double rating = carrier.getRating() != null ? carrier.getRating() : 3.0;
        if (rating < 3.0) {
            baseRisk += 30.0;
        } else if (rating < 4.0) {
            baseRisk += 15.0;
        }

        // Штраф за малый опыт
        int completedOrders = carrier.getCompletedOrders() != null ? carrier.getCompletedOrders() : 0;
        if (completedOrders < 5) {
            baseRisk += 20.0;
        }

        // Штраф за нереалистично быстрый срок (менее 2 дней)
        if (bid.getEstimatedDays() <= 2) {
            baseRisk += 25.0;
        }

        // Ограничиваем риск диапазоном 0 - 100%
        return Math.min(Math.max(baseRisk, 0.0), 100.0);
    }
}