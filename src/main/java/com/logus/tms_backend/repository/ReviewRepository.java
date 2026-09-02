package com.logus.tms_backend.repository;

import com.logus.tms_backend.model.Review;
import com.logus.tms_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Получить все отзывы о конкретном перевозчике
    @Query("SELECT r FROM Review r LEFT JOIN FETCH r.customer LEFT JOIN FETCH r.order WHERE r.carrier = :carrier ORDER BY r.createdAt DESC")
    List<Review> findByCarrierWithDetails(@Param("carrier") User carrier);

    // Проверить, существует ли уже отзыв на этот заказ
    boolean existsByOrder_Id(Long orderId);

    // Найти отзыв по заказу
    Optional<Review> findByOrder_Id(Long orderId);

    // Получить все отзывы конкретного заказчика (который он оставил)
    @Query("SELECT r FROM Review r LEFT JOIN FETCH r.carrier LEFT JOIN FETCH r.order WHERE r.customer = :customer ORDER BY r.createdAt DESC")
    List<Review> findByCustomerWithDetails(@Param("customer") User customer);
}