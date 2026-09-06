package com.logus.tms_backend.repository;

import com.logus.tms_backend.model.Order;
import com.logus.tms_backend.model.OrderStatus;
import com.logus.tms_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.customer LEFT JOIN FETCH o.carrier WHERE o.id = :id")
    Optional<Order> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.customer LEFT JOIN FETCH o.carrier")
    List<Order> findAllWithDetails();

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.customer LEFT JOIN FETCH o.carrier WHERE o.customer = :customer")
    List<Order> findByCustomerWithDetails(@Param("customer") User customer);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.customer LEFT JOIN FETCH o.carrier WHERE o.carrier = :carrier")
    List<Order> findByCarrierWithDetails(@Param("carrier") User carrier);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.customer LEFT JOIN FETCH o.carrier WHERE o.status = :status")
    List<Order> findByStatusWithDetails(@Param("status") OrderStatus status);

    // 🆕 История завершенных заказов заказчика
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.customer LEFT JOIN FETCH o.carrier WHERE o.customer = :customer AND o.status IN ('COMPLETED', 'CANCELLED') ORDER BY o.deadline DESC")
    List<Order> findHistoryByCustomer(@Param("customer") User customer);

    // 🆕 История завершенных заказов перевозчика
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.customer LEFT JOIN FETCH o.carrier WHERE o.carrier = :carrier AND o.status IN ('COMPLETED', 'CANCELLED') ORDER BY o.deadline DESC")
    List<Order> findHistoryByCarrier(@Param("carrier") User carrier);

    List<Order> findByCustomerId(Long customerId);

    List<Order> findByCarrierId(Long carrierId);

    // В OrderRepository.java — обновляем запросы:

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.customer LEFT JOIN FETCH o.carrier " +
            "WHERE o.customer = :customer AND o.status IN ('NEW', 'ASSIGNED', 'IN_PROGRESS') " +
            "ORDER BY o.deadline ASC")
    List<Order> findActiveByCustomer(@Param("customer") User customer);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.customer LEFT JOIN FETCH o.carrier " +
            "WHERE o.carrier = :carrier AND o.status IN ('ASSIGNED', 'IN_PROGRESS') " +
            "ORDER BY o.deadline ASC")
    List<Order> findActiveByCarrier(@Param("carrier") User carrier);
    // 🆕 Поиск заказов с фильтрами (для перевозчика)
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.customer LEFT JOIN FETCH o.carrier LEFT JOIN FETCH o.category " +
            "WHERE o.status = 'NEW' " +
            "AND (:fromCity IS NULL OR o.fromCity = :fromCity) " +
            "AND (:toCity IS NULL OR o.toCity = :toCity) " +
            "AND (:minWeight IS NULL OR o.weight >= :minWeight) " +
            "AND (:maxWeight IS NULL OR o.weight <= :maxWeight) " +
            "AND (:deadlineBefore IS NULL OR o.deadline <= :deadlineBefore) " +
            "AND (:categoryId IS NULL OR o.category.id = :categoryId) " +
            "ORDER BY o.deadline ASC")
    List<Order> findOrdersWithFilters(
            @Param("fromCity") String fromCity,
            @Param("toCity") String toCity,
            @Param("minWeight") BigDecimal minWeight,
            @Param("maxWeight") BigDecimal maxWeight,
            @Param("deadlineBefore") LocalDateTime deadlineBefore,
            @Param("categoryId") Long categoryId // 🆕
    );
}