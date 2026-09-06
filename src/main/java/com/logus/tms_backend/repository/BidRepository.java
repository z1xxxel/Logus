package com.logus.tms_backend.repository;

import com.logus.tms_backend.model.Bid;
import com.logus.tms_backend.model.BidStatus;
import com.logus.tms_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {

    List<Bid> findByOrderIdAndStatus(Long orderId, BidStatus status);

    List<Bid> findByCarrierId(Long carrierId);

    List<Bid> findByOrderId(Long orderId);


    // 🆕 Получить все предложения конкретного перевозчика с загрузкой связанных данных
    @Query("SELECT b FROM Bid b LEFT JOIN FETCH b.order LEFT JOIN FETCH b.carrier WHERE b.carrier = :carrier")
    List<Bid> findByCarrierWithDetails(@Param("carrier") User carrier);
    @Query("SELECT b FROM Bid b LEFT JOIN FETCH b.order LEFT JOIN FETCH b.carrier WHERE b.id = :id")
    Optional<Bid> findByIdWithDetails(@Param("id") Long id);
    @Query("SELECT b FROM Bid b LEFT JOIN FETCH b.carrier WHERE b.order.id = :orderId AND b.status = :status")
    List<Bid> findByOrderIdAndStatusWithDetails(@Param("orderId") Long orderId, @Param("status") BidStatus status);
}