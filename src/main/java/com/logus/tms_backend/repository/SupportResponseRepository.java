package com.logus.tms_backend.repository;

import com.logus.tms_backend.model.SupportResponse;
import com.logus.tms_backend.model.SupportTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupportResponseRepository extends JpaRepository<SupportResponse, Long> {

    @Query("SELECT r FROM SupportResponse r LEFT JOIN FETCH r.author WHERE r.ticket = :ticket ORDER BY r.createdAt ASC")
    List<SupportResponse> findByTicketWithDetails(@Param("ticket") SupportTicket ticket);

    @Query("SELECT COUNT(r) FROM SupportResponse r WHERE r.ticket.author.id = :userId AND r.author.id != :userId AND r.isRead = false")
    Long countUnreadForUser(@Param("userId") Long userId);
}