package com.logus.tms_backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "support_responses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupportResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2000)
    private String message;

    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private SupportTicket ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isInternalNote = false;

    // 🆕 Статус прочтения
    @Builder.Default
    @Column(nullable = false)
    private Boolean isRead = false;

    // 🆕 Время прочтения
    private LocalDateTime readAt;

    // 🆕 Вложения (список URL файлов)
    @ElementCollection
    @CollectionTable(name = "response_attachments", joinColumns = @JoinColumn(name = "response_id"))
    @Column(name = "file_url")
    @Builder.Default
    private List<String> attachments = new ArrayList<>();
}