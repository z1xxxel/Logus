package com.logus.tms_backend.repository;

import com.logus.tms_backend.model.SupportTicket;
import com.logus.tms_backend.model.TicketStatus;
import com.logus.tms_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {

    @Query("SELECT t FROM SupportTicket t LEFT JOIN FETCH t.author LEFT JOIN FETCH t.assignedTo LEFT JOIN FETCH t.relatedOrder WHERE t.id = :id")
    Optional<SupportTicket> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT t FROM SupportTicket t LEFT JOIN FETCH t.author LEFT JOIN FETCH t.assignedTo WHERE t.status = :status ORDER BY t.createdAt DESC")
    List<SupportTicket> findByStatusWithDetails(@Param("status") TicketStatus status);

    @Query("SELECT t FROM SupportTicket t LEFT JOIN FETCH t.author LEFT JOIN FETCH t.assignedTo WHERE t.author = :author ORDER BY t.createdAt DESC")
    List<SupportTicket> findByAuthorWithDetails(@Param("author") User author);

    @Query("SELECT t FROM SupportTicket t LEFT JOIN FETCH t.author LEFT JOIN FETCH t.assignedTo WHERE t.assignedTo = :assigned ORDER BY t.createdAt DESC")
    List<SupportTicket> findByAssignedToWithDetails(@Param("assigned") User assigned);

    @Query("SELECT t FROM SupportTicket t LEFT JOIN FETCH t.author LEFT JOIN FETCH t.assignedTo ORDER BY t.createdAt DESC")
    List<SupportTicket> findAllWithDetails();
}