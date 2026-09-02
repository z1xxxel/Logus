package com.logus.tms_backend.repository;

import com.logus.tms_backend.model.User;
import com.logus.tms_backend.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    // Получить всех пользователей определенной роли
    List<User> findByRole(UserRole role);

    // 🆕 Поиск перевозчиков с фильтрами (ПРАВИЛЬНОЕ МЕСТО для @Query)
    @Query("SELECT u FROM User u WHERE u.role = 'Carrier' " +
            "AND (:minRating IS NULL OR u.rating >= :minRating) " +
            "AND (:minCompletedOrders IS NULL OR u.completedOrders >= :minCompletedOrders) " +
            "ORDER BY u.rating DESC, u.completedOrders DESC")
    List<User> findCarriersWithFilters(
            @Param("minRating") Double minRating,
            @Param("minCompletedOrders") Integer minCompletedOrders
    );

}