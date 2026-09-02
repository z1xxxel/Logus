package com.logus.tms_backend.service;

import com.logus.tms_backend.model.*;
import com.logus.tms_backend.repository.OrderRepository;
import com.logus.tms_backend.repository.ReviewRepository;
import com.logus.tms_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    /**
     * 🏆 Оставить отзыв о перевозчике
     */
    @Transactional
    public Review createReview(Long orderId, Long customerId, Integer rating, String comment) {
        // 1. Валидация оценки
        if (rating < 1 || rating > 5) {
            throw new RuntimeException("Оценка должна быть от 1 до 5");
        }

        // 2. Проверяем, что заказ существует
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));

        // 3. Проверяем, что заказ завершен
        if (order.getStatus() != OrderStatus.COMPLETED) {
            throw new RuntimeException("Отзыв можно оставить только после завершения заказа");
        }

        // 4. Проверяем, что заказчик — владелец заказа
        if (!order.getCustomer().getId().equals(customerId)) {
            throw new RuntimeException("Только заказчик может оставить отзыв на этот заказ");
        }

        // 5. Проверяем, что перевозчик назначен
        User carrier = order.getCarrier();
        if (carrier == null) {
            throw new RuntimeException("У заказа нет перевозчика");
        }

        // 6. Проверяем, что отзыв еще не оставляли
        if (reviewRepository.existsByOrder_Id(orderId)) {
            throw new RuntimeException("Отзыв на этот заказ уже оставлен");
        }

        // 7. Получаем заказчика
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Заказчик не найден"));

        // 8. Создаем отзыв
        Review review = Review.builder()
                .order(order)
                .carrier(carrier)
                .customer(customer)
                .rating(rating)
                .comment(comment)
                .build();

        Review savedReview = reviewRepository.save(review);

        // 9. 🧠 ПЕРЕССЧИТЫВАЕМ РЕЙТИНГ ПЕРЕВОЗЧИКА
        recalculateCarrierRating(carrier);

        return savedReview;
    }

    /**
     * 🧠 Умный пересчет рейтинга перевозчика на основе ВСЕХ его отзывов
     */
    private void recalculateCarrierRating(User carrier) {
        List<Review> allReviews = reviewRepository.findByCarrierWithDetails(carrier);

        if (allReviews.isEmpty()) {
            carrier.setRating(3.0); // Если отзывов нет, ставим средний
        } else {
            // Считаем среднее арифметическое
            double averageRating = allReviews.stream()
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(3.0);

            // Округляем до 1 знака после запятой
            carrier.setRating(Math.round(averageRating * 10.0) / 10.0);
        }

        userRepository.save(carrier);
    }

    /**
     * Получить все отзывы о перевозчике
     */
    @Transactional(readOnly = true)
    public List<Review> getReviewsByCarrier(Long carrierId) {
        User carrier = userRepository.findById(carrierId)
                .orElseThrow(() -> new RuntimeException("Перевозчик не найден"));
        return reviewRepository.findByCarrierWithDetails(carrier);
    }

    /**
     * Получить все отзывы, оставленные заказчиком
     */
    @Transactional(readOnly = true)
    public List<Review> getReviewsByCustomer(Long customerId) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Заказчик не найден"));
        return reviewRepository.findByCustomerWithDetails(customer);
    }

    /**
     * Получить отзыв по ID заказа
     */
    @Transactional(readOnly = true)
    public Review getReviewByOrderId(Long orderId) {
        return reviewRepository.findByOrder_Id(orderId)
                .orElseThrow(() -> new RuntimeException("Отзыв на этот заказ не найден"));
    }
}