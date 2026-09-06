package com.logus.tms_backend.service;

import com.logus.tms_backend.DTO.PricePredictionRequest;
import com.logus.tms_backend.DTO.PricePredictionResponse;
import com.logus.tms_backend.model.CargoCategory;
import com.logus.tms_backend.repository.CargoCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Service
public class PricePredictionService {

    @Autowired
    private CargoCategoryRepository categoryRepository;

    @Autowired
    private CityCoordinatesService coordinatesService;

    // Базовая ставка за километр
    private static final double BASE_RATE_PER_KM = 25.0;

    public PricePredictionResponse predictPrice(PricePredictionRequest request) {
        // 1. Рассчитываем расстояние через новый сервис координат
        double distance = calculateDistance(request.getFromCity(), request.getToCity());

        // 2. Базовая цена от расстояния
        double basePrice = distance * BASE_RATE_PER_KM;

        // 3. Коэффициент веса (чем тяжелее, тем дороже)
        double weightFactor = 1.0 + ((request.getWeight() != null ? request.getWeight() : 0) * 0.05);

        // 4. Коэффициент срочности (чем ближе дедлайн, тем дороже)
        double urgencyFactor = calculateUrgencyFactor(request.getDeadline());

        // 5. Коэффициент категории груза
        double categoryFactor = calculateCategoryFactor(request.getCategoryId());

        // 6. Итоговая предсказанная цена и уверенность
        double rawPredictedPrice = basePrice * weightFactor * urgencyFactor * categoryFactor;
        double rawConfidence = calculateConfidence(distance, request.getWeight());

        // 7. Конвертируем в BigDecimal для корректной работы с БД и JSON
        BigDecimal finalPredictedPrice = BigDecimal.valueOf(rawPredictedPrice)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal finalConfidence = BigDecimal.valueOf(rawConfidence)
                .setScale(2, RoundingMode.HALF_UP);

        // 8. Формируем объяснение
        String explanation = buildExplanation(distance, weightFactor, urgencyFactor, categoryFactor);

        // 9. Собираем факторы для ответа
        Map<String, Object> factors = new HashMap<>();
        factors.put("distance_km", Math.round(distance)); // Округляем км для красоты
        factors.put("base_price", basePrice);
        factors.put("weight_factor", Math.round(weightFactor * 100.0) / 100.0);
        factors.put("urgency_factor", Math.round(urgencyFactor * 100.0) / 100.0);
        factors.put("category_factor", Math.round(categoryFactor * 100.0) / 100.0);

        return PricePredictionResponse.builder()
                .predictedPrice(finalPredictedPrice)
                .confidence(finalConfidence)
                .currency("RUB")
                .factors(factors)
                .explanation(explanation)
                .build();
    }

    // ✅ ЕДИНСТВЕННЫЙ метод расчета расстояния (делегирование)
    private double calculateDistance(String fromCity, String toCity) {
        if (fromCity == null || toCity == null) {
            return 1000.0; // Дефолт, если города не указаны
        }
        return coordinatesService.calculateDistance(fromCity, toCity);
    }

    private double calculateUrgencyFactor(LocalDateTime deadline) {
        if (deadline == null) {
            return 1.0;
        }
        long daysUntilDeadline = ChronoUnit.DAYS.between(LocalDateTime.now(), deadline);

        if (daysUntilDeadline <= 1) {
            return 2.0;
        } else if (daysUntilDeadline <= 3) {
            return 1.5;
        } else if (daysUntilDeadline <= 7) {
            return 1.2;
        } else {
            return 1.0;
        }
    }

    private double calculateCategoryFactor(Long categoryId) {
        if (categoryId == null) {
            return 1.0;
        }

        CargoCategory category = categoryRepository.findById(categoryId).orElse(null);
        if (category == null) {
            return 1.0;
        }

        if (category.getRequiresSpecialPermit() != null && category.getRequiresSpecialPermit()) {
            return 1.3;
        }

        String categoryName = category.getName().toLowerCase();
        if (categoryName.contains("хрупк") || categoryName.contains("fragile")) {
            return 1.2;
        }
        if (categoryName.contains("опасн") || categoryName.contains("hazard")) {
            return 1.4;
        }

        return 1.0;
    }

    private double calculateConfidence(double distance, Double weight) {
        double distanceConfidence = Math.min(distance / 1000.0, 1.0);
        double weightConfidence = (weight != null && weight > 0 && weight < 50) ? 0.9 : 0.7;

        return Math.round((distanceConfidence * 0.6 + weightConfidence * 0.4) * 100.0) / 100.0;
    }

    private String buildExplanation(double distance, double weightFactor,
                                    double urgencyFactor, double categoryFactor) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Расстояние: %.0f км. ", distance));

        if (weightFactor > 1.1) {
            sb.append(String.format("Вес груза увеличивает стоимость на %.0f%%. ", (weightFactor - 1) * 100));
        }

        if (urgencyFactor > 1.1) {
            sb.append(String.format("Срочность доставки добавляет %.0f%%. ", (urgencyFactor - 1) * 100));
        }

        if (categoryFactor > 1.1) {
            sb.append(String.format("Специфика груза требует дополнительных %.0f%%. ", (categoryFactor - 1) * 100));
        }

        return sb.toString();
    }
}