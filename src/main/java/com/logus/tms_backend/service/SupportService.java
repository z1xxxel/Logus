package com.logus.tms_backend.service;

import com.logus.tms_backend.model.*;
import com.logus.tms_backend.repository.OrderRepository;
import com.logus.tms_backend.repository.SupportResponseRepository;
import com.logus.tms_backend.repository.SupportTicketRepository;
import com.logus.tms_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SupportService {

    private final SupportTicketRepository ticketRepository;
    private final SupportResponseRepository responseRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    /**
     * 1. Создать тикет (любой пользователь)
     */
    @Transactional
    public SupportTicket createTicket(Long authorId, String subject, String description,
                                      TicketPriority priority, Long relatedOrderId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (author.getRole() != UserRole.Customer && author.getRole() != UserRole.Carrier) {
            throw new RuntimeException("Только заказчики и перевозчики могут создавать тикеты");
        }

        Order relatedOrder = null;
        if (relatedOrderId != null) {
            relatedOrder = orderRepository.findById(relatedOrderId)
                    .orElseThrow(() -> new RuntimeException("Заказ не найден"));
        }

        SupportTicket ticket = SupportTicket.builder()
                .subject(subject)
                .description(description)
                .priority(priority)
                .author(author)
                .relatedOrder(relatedOrder)
                .status(TicketStatus.OPEN)
                .build();

        return ticketRepository.save(ticket);
    }

    /**
     * 2. 🆕 Получить тикет по ID (Этот метод отсутствовал или был с ошибкой)
     */
    @Transactional(readOnly = true)
    public SupportTicket getTicketById(Long ticketId) {
        return ticketRepository.findByIdWithDetails(ticketId)
                .orElseThrow(() -> new RuntimeException("Тикет не найден"));
    }

    /**
     * 3. 🆕 Получить все сообщения тикета (история чата)
     */
    @Transactional(readOnly = true)
    public List<SupportResponse> getTicketResponses(SupportTicket ticket) {
        return responseRepository.findByTicketWithDetails(ticket);
    }

    /**
     * 4. Получить все тикеты (для поддержки и админа)
     */
    @Transactional(readOnly = true)
    public List<SupportTicket> getAllTickets() {
        return ticketRepository.findAllWithDetails();
    }

    /**
     * 5. Получить тикеты по статусу
     */
    @Transactional(readOnly = true)
    public List<SupportTicket> getTicketsByStatus(TicketStatus status) {
        return ticketRepository.findByStatusWithDetails(status);
    }

    /**
     * 6. Получить тикеты конкретного пользователя
     */
    @Transactional(readOnly = true)
    public List<SupportTicket> getTicketsByAuthor(Long authorId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        return ticketRepository.findByAuthorWithDetails(author);
    }

    /**
     * 7. Назначить тикет на сотрудника поддержки
     */
    @Transactional
    public SupportTicket assignTicket(Long ticketId, Long supportId) {
        SupportTicket ticket = ticketRepository.findByIdWithDetails(ticketId)
                .orElseThrow(() -> new RuntimeException("Тикет не найден"));

        User support = userRepository.findById(supportId)
                .orElseThrow(() -> new RuntimeException("Сотрудник не найден"));

        if (support.getRole() != UserRole.Support && support.getRole() != UserRole.Admin) {
            throw new RuntimeException("Только сотрудники поддержки могут быть назначены");
        }

        ticket.setAssignedTo(support);
        ticket.setStatus(TicketStatus.IN_PROGRESS);
        return ticketRepository.save(ticket);
    }

    /**
     * 8. 🆕 Добавить ответ с вложениями (для чата)
     */
    @Transactional
    public SupportResponse addResponseWithAttachments(Long ticketId, Long authorId,
                                                      String message, Boolean isInternalNote,
                                                      List<String> attachments) {
        SupportTicket ticket = ticketRepository.findByIdWithDetails(ticketId)
                .orElseThrow(() -> new RuntimeException("Тикет не найден"));

        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        boolean isAuthor = ticket.getAuthor().getId().equals(authorId);
        boolean isSupport = author.getRole() == UserRole.Support || author.getRole() == UserRole.Admin;

        if (!isAuthor && !isSupport) {
            throw new RuntimeException("Только автор тикета или поддержка могут отвечать");
        }

        if (isInternalNote != null && isInternalNote && !isSupport) {
            throw new RuntimeException("Только поддержка может писать внутренние заметки");
        }

        SupportResponse response = SupportResponse.builder()
                .message(message)
                .ticket(ticket)
                .author(author)
                .isInternalNote(isInternalNote != null ? isInternalNote : false)
                .isRead(false)
                .attachments(attachments != null ? attachments : new ArrayList<>())
                .build();

        // Обновляем статус тикета
        if (isSupport && ticket.getStatus() == TicketStatus.OPEN) {
            ticket.setStatus(TicketStatus.IN_PROGRESS);
            ticketRepository.save(ticket);
        }

        if (isAuthor && ticket.getStatus() == TicketStatus.IN_PROGRESS) {
            ticket.setStatus(TicketStatus.WAITING);
            ticketRepository.save(ticket);
        }

        return responseRepository.save(response);
    }

    /**
     * 9. 🆕 Отметить все сообщения тикета как прочитанные для пользователя
     */
    @Transactional
    public void markMessagesAsRead(Long ticketId, Long userId) {
        SupportTicket ticket = ticketRepository.findByIdWithDetails(ticketId)
                .orElseThrow(() -> new RuntimeException("Тикет не найден"));

        List<SupportResponse> responses = responseRepository.findByTicketWithDetails(ticket);

        for (SupportResponse response : responses) {
            // Помечаем как прочитанные только сообщения от ДРУГИХ пользователей
            if (!response.getAuthor().getId().equals(userId) && !response.getIsRead()) {
                response.setIsRead(true);
                response.setReadAt(LocalDateTime.now());
            }
        }

        responseRepository.saveAll(responses);
    }

    /**
     * 10. 🆕 Получить количество непрочитанных сообщений для пользователя
     */
    @Transactional(readOnly = true)
    public Long getUnreadCount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        List<SupportTicket> tickets = ticketRepository.findByAuthorWithDetails(user);

        long unreadCount = 0;
        for (SupportTicket ticket : tickets) {
            List<SupportResponse> responses = responseRepository.findByTicketWithDetails(ticket);
            for (SupportResponse response : responses) {
                if (!response.getAuthor().getId().equals(userId) && !response.getIsRead()) {
                    unreadCount++;
                }
            }
        }

        return unreadCount;
    }

    /**
     * 11. Закрыть тикет
     */
    @Transactional
    public SupportTicket closeTicket(Long ticketId, Long supportId) {
        SupportTicket ticket = ticketRepository.findByIdWithDetails(ticketId)
                .orElseThrow(() -> new RuntimeException("Тикет не найден"));

        User support = userRepository.findById(supportId)
                .orElseThrow(() -> new RuntimeException("Сотрудник не найден"));

        if (support.getRole() != UserRole.Support && support.getRole() != UserRole.Admin) {
            throw new RuntimeException("Только поддержка может закрывать тикеты");
        }

        ticket.setStatus(TicketStatus.CLOSED);
        ticket.setClosedAt(LocalDateTime.now());
        return ticketRepository.save(ticket);
    }

    /**
     * 12. Отметить тикет как решенный
     */
    @Transactional
    public SupportTicket resolveTicket(Long ticketId, Long supportId) {
        SupportTicket ticket = ticketRepository.findByIdWithDetails(ticketId)
                .orElseThrow(() -> new RuntimeException("Тикет не найден"));

        User support = userRepository.findById(supportId)
                .orElseThrow(() -> new RuntimeException("Сотрудник не найден"));

        if (support.getRole() != UserRole.Support && support.getRole() != UserRole.Admin) {
            throw new RuntimeException("Только поддержка может отмечать тикеты как решенные");
        }

        ticket.setStatus(TicketStatus.RESOLVED);
        return ticketRepository.save(ticket);
    }
}