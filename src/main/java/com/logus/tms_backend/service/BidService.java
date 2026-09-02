package com.logus.tms_backend.service;

import com.logus.tms_backend.DTO.BidScoreDTO;
import com.logus.tms_backend.model.Bid;
import com.logus.tms_backend.model.BidStatus;
import com.logus.tms_backend.model.Order;
import com.logus.tms_backend.model.User;
import com.logus.tms_backend.repository.BidRepository;
import com.logus.tms_backend.repository.OrderRepository;
import com.logus.tms_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BidService {

    private final BidRepository bidRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final RiskPredictionService riskPredictionService; // ✅ Теперь это работает

    @Transactional
    public Bid createBid(Long orderId, Long carrierId, BigDecimal price, Integer estimatedDays, String comment) {
        Order order = orderRepository.findByIdWithDetails(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));

        User carrier = userRepository.findById(carrierId)
                .orElseThrow(() -> new RuntimeException("Перевозчик не найден"));

        if (carrier.getRole() != com.logus.tms_backend.model.UserRole.Carrier) {
            throw new RuntimeException("Только перевозчик может оставлять предложения");
        }

        Bid newBid = Bid.builder()
                .order(order)
                .carrier(carrier)
                .price(price)
                .estimatedDays(estimatedDays)
                .comment(comment)
                .status(BidStatus.PENDING) // ✅ Явно указываем статус для надежности
                .build();

        return bidRepository.save(newBid);
    }

    /**
     * 🧠 Умное ранжирование предложений
     */
    @Transactional(readOnly = true)
    public List<BidScoreDTO> getRankedBidsForOrder(Long orderId) {
        // ✅ ИСПРАВЛЕНО: используем метод с JOIN FETCH, чтобы избежать LazyInitializationException
        List<Bid> bids = bidRepository.findByOrderIdAndStatusWithDetails(orderId, BidStatus.PENDING);

        return bids.stream().map(bid -> {
            User carrier = bid.getCarrier();
            double carrierRating = (carrier.getRating() != null) ? carrier.getRating() : 3.0;

            // --- ФОРМУЛА УМНОГО СКОРИНГА ---
            double pricePenalty = bid.getPrice().doubleValue() / 10000.0;
            double timePenalty = bid.getEstimatedDays() * 2.0;
            double ratingBonus = carrierRating * 15.0;
            double smartScore = ratingBonus - pricePenalty - timePenalty;

            // 🧠 ВЫЗОВ "ML-МОДЕЛИ" для оценки риска
            double delayRisk = riskPredictionService.calculateDelayRisk(bid, carrier);

            // Формируем рекомендацию с учетом риска
            String recommendation;
            if (smartScore > 20 && delayRisk < 20.0) {
                recommendation = "Лучший выбор: Высокая надежность, низкий риск срыва срока";
            } else if (smartScore > 10 && delayRisk < 40.0) {
                recommendation = "Хороший вариант: Приемлемый баланс цены и рисков";
            } else {
                recommendation = "Рискованно: Высокая вероятность срыва сроков или завышенная цена";
            }

            return new BidScoreDTO(
                    bid,
                    Math.round(smartScore * 100.0) / 100.0,
                    recommendation,
                    Math.round(delayRisk * 10.0) / 10.0
            );
        }).sorted((b1, b2) -> {
            int scoreCompare = Double.compare(b2.smartScore(), b1.smartScore());
            if (scoreCompare != 0) return scoreCompare;
            return Double.compare(b1.delayRiskPercent(), b2.delayRiskPercent());
        }).collect(Collectors.toList());
    }

    // ✅ ОСТАВЛЯЕМ ТОЛЬКО ЭТОТ ВАРИАНТ (с JOIN FETCH)
    @Transactional(readOnly = true)
    public Bid getBidById(Long bidId) {
        return bidRepository.findByIdWithDetails(bidId)
                .orElseThrow(() -> new RuntimeException("Предложение не найдено"));
    }

    @Transactional(readOnly = true)
    public List<Bid> getBidsByOrder(Long orderId) {
        return bidRepository.findByOrderId(orderId);
    }

    @Transactional
    public void rejectBid(Long bidId) {
        Bid bid = getBidById(bidId);
        if (bid.getStatus() != BidStatus.PENDING) {
            throw new RuntimeException("Предложение уже обработано");
        }
        bid.setStatus(BidStatus.REJECTED);
        bidRepository.save(bid);
    }

    @Transactional(readOnly = true)
    public List<Bid> getAllBids() {
        return bidRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Bid> getBidsByCarrier(Long carrierId) {
        User carrier = userRepository.findById(carrierId)
                .orElseThrow(() -> new RuntimeException("Перевозчик не найден"));
        return bidRepository.findByCarrierWithDetails(carrier);
    }
}