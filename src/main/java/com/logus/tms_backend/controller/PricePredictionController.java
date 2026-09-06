package com.logus.tms_backend.controller;

import com.logus.tms_backend.DTO.PricePredictionRequest;
import com.logus.tms_backend.DTO.PricePredictionResponse;
import com.logus.tms_backend.model.Bid;
import com.logus.tms_backend.model.Order;
import com.logus.tms_backend.repository.BidRepository;
import com.logus.tms_backend.repository.OrderRepository;
import com.logus.tms_backend.service.PricePredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/predictions")
@CrossOrigin(origins = "*")
public class PricePredictionController {

    @Autowired
    private PricePredictionService predictionService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private BidRepository bidRepository;

    @PostMapping("/price")
    public ResponseEntity<PricePredictionResponse> predictPrice(@RequestBody PricePredictionRequest request) {
        return ResponseEntity.ok(predictionService.predictPrice(request));
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<PricePredictionResponse> getOrderPrediction(@PathVariable Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));

        PricePredictionRequest request = new PricePredictionRequest();
        request.setFromCity(order.getFromCity());
        request.setToCity(order.getToCity());
        request.setWeight(order.getWeight() != null ? order.getWeight().doubleValue() : null);
        request.setCategoryId(order.getCategory() != null ? order.getCategory().getId() : null);
        request.setDeadline(order.getDeadline());

        return ResponseEntity.ok(predictionService.predictPrice(request));
    }

    @GetMapping("/orders/{orderId}/compare-bids")
    public ResponseEntity<Map<String, Object>> compareBidsWithPrediction(@PathVariable Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));

        List<Bid> bids = bidRepository.findByOrderId(orderId);

        PricePredictionRequest request = new PricePredictionRequest();
        request.setFromCity(order.getFromCity());
        request.setToCity(order.getToCity());
        request.setWeight(order.getWeight() != null ? order.getWeight().doubleValue() : null);
        request.setCategoryId(order.getCategory() != null ? order.getCategory().getId() : null);
        request.setDeadline(order.getDeadline());

        PricePredictionResponse prediction = predictionService.predictPrice(request);

        // ✅ ИСПРАВЛЕНО: Вызываем .doubleValue(), так как теперь в DTO это BigDecimal
        Double finalPredictedPrice = prediction.getPredictedPrice() != null ? prediction.getPredictedPrice().doubleValue() : 0.0;

        List<Map<String, Object>> bidAnalysis = new ArrayList<>();
        for (Bid bid : bids) {
            Map<String, Object> analysis = new HashMap<>();
            analysis.put("bidId", bid.getId());
            analysis.put("carrierName", bid.getCarrier().getName());

            double bidPriceDouble = bid.getPrice() != null ? bid.getPrice().doubleValue() : 0.0;

            analysis.put("bidPrice", bidPriceDouble);
            analysis.put("predictedPrice", finalPredictedPrice);

            // Теперь математика работает корректно, так как оба значения типа Double
            double difference = bidPriceDouble - finalPredictedPrice;
            double percentDifference = finalPredictedPrice > 0 ? (difference / finalPredictedPrice) * 100 : 0;

            analysis.put("difference", Math.round(difference * 100.0) / 100.0);
            analysis.put("percentDifference", Math.round(percentDifference * 100.0) / 100.0);

            if (percentDifference < -20) {
                analysis.put("status", "TOO_CHEAP");
                analysis.put("recommendation", "Подозрительно дешево — возможен риск");
            } else if (percentDifference < -5) {
                analysis.put("status", "GOOD_DEAL");
                analysis.put("recommendation", "Выгодное предложение");
            } else if (percentDifference <= 5) {
                analysis.put("status", "FAIR_PRICE");
                analysis.put("recommendation", "Рыночная цена");
            } else if (percentDifference <= 20) {
                analysis.put("status", "SLIGHTLY_EXPENSIVE");
                analysis.put("recommendation", "Чуть выше рынка");
            } else {
                analysis.put("status", "OVERPRICED");
                analysis.put("recommendation", "Завышенная цена");
            }

            bidAnalysis.add(analysis);
        }

        bidAnalysis.sort(Comparator.comparingDouble(a -> (Double) a.get("percentDifference")));

        Map<String, Object> result = new HashMap<>();
        result.put("orderId", orderId);
        result.put("predictedPrice", finalPredictedPrice);
        result.put("confidence", prediction.getConfidence()); // Confidence тоже вернется корректно
        result.put("bidsAnalysis", bidAnalysis);
        result.put("totalBids", bids.size());

        return ResponseEntity.ok(result);
    }
}