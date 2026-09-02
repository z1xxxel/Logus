package com.logus.tms_backend.controller;

import com.logus.tms_backend.model.*;
import com.logus.tms_backend.repository.UserRepository;
import com.logus.tms_backend.service.FileStorageService;
import com.logus.tms_backend.service.SupportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/support")
@RequiredArgsConstructor
public class SupportController {

    private final SupportService supportService;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    /**
     * 1. Создать тикет (обращение в поддержку)
     */
    @PostMapping("/tickets")
    public ResponseEntity<SupportTicket> createTicket(
            @RequestParam Long authorId,
            @RequestParam String subject,
            @RequestParam String description,
            @RequestParam(defaultValue = "MEDIUM") TicketPriority priority,
            @RequestParam(required = false) Long relatedOrderId) {

        SupportTicket ticket = supportService.createTicket(authorId, subject, description, priority, relatedOrderId);
        return new ResponseEntity<>(ticket, HttpStatus.CREATED);
    }

    /**
     * 2. Получить тикет по ID
     */
    @GetMapping("/tickets/{id}")
    public ResponseEntity<SupportTicket> getTicketById(@PathVariable Long id) {
        return ResponseEntity.ok(supportService.getTicketById(id));
    }

    /**
     * 3. Получить все тикеты (для поддержки и админа)
     */
    @GetMapping("/tickets")
    public ResponseEntity<List<SupportTicket>> getAllTickets() {
        return ResponseEntity.ok(supportService.getAllTickets());
    }

    /**
     * 4. Получить тикеты по статусу
     */
    @GetMapping("/tickets/status/{status}")
    public ResponseEntity<List<SupportTicket>> getTicketsByStatus(@PathVariable TicketStatus status) {
        return ResponseEntity.ok(supportService.getTicketsByStatus(status));
    }

    /**
     * 5. Получить тикеты конкретного пользователя
     */
    @GetMapping("/tickets/user/{userId}")
    public ResponseEntity<List<SupportTicket>> getTicketsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(supportService.getTicketsByAuthor(userId));
    }

    /**
     * 6. Назначить тикет на сотрудника поддержки
     */
    @PutMapping("/tickets/{id}/assign")
    public ResponseEntity<SupportTicket> assignTicket(
            @PathVariable Long id,
            @RequestParam Long supportId) {
        return ResponseEntity.ok(supportService.assignTicket(id, supportId));
    }

    /**
     * 7. Отправить сообщение в чат тикета (с возможными вложениями)
     */
    @PostMapping("/tickets/{id}/messages")
    public ResponseEntity<SupportResponse> sendMessage(
            @PathVariable Long id,
            @RequestParam Long authorId,
            @RequestParam String message,
            @RequestParam(required = false) Boolean isInternalNote,
            @RequestParam(required = false) List<MultipartFile> attachments) {

        List<String> attachmentUrls = new ArrayList<>();

        if (attachments != null && !attachments.isEmpty()) {
            for (MultipartFile file : attachments) {
                try {
                    String url = fileStorageService.storeFile(file);
                    attachmentUrls.add(url);
                } catch (IOException e) {
                    throw new RuntimeException("Ошибка загрузки файла: " + e.getMessage());
                }
            }
        }

        SupportResponse response = supportService.addResponseWithAttachments(
                id, authorId, message, isInternalNote, attachmentUrls);

        return ResponseEntity.ok(response);
    }

    /**
     * 8. Получить все сообщения тикета (история чата)
     */
    @GetMapping("/tickets/{id}/messages")
    public ResponseEntity<List<SupportResponse>> getTicketMessages(@PathVariable Long id) {
        SupportTicket ticket = supportService.getTicketById(id);
        return ResponseEntity.ok(supportService.getTicketResponses(ticket));
    }

    /**
     * 9. Отметить сообщения в тикете как прочитанные
     */
    @PutMapping("/tickets/{id}/mark-read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long id,
            @RequestParam Long userId) {
        supportService.markMessagesAsRead(id, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * 10. Получить количество непрочитанных сообщений для пользователя
     */
    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount(@RequestParam Long userId) {
        return ResponseEntity.ok(supportService.getUnreadCount(userId));
    }

    /**
     * 11. Закрыть тикет
     */
    @PutMapping("/tickets/{id}/close")
    public ResponseEntity<SupportTicket> closeTicket(
            @PathVariable Long id,
            @RequestParam Long supportId) {
        return ResponseEntity.ok(supportService.closeTicket(id, supportId));
    }

    /**
     * 12. Отметить тикет как решенный
     */
    @PutMapping("/tickets/{id}/resolve")
    public ResponseEntity<SupportTicket> resolveTicket(
            @PathVariable Long id,
            @RequestParam Long supportId) {
        return ResponseEntity.ok(supportService.resolveTicket(id, supportId));
    }
}