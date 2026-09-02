package com.logus.tms_backend.model;

public enum TicketStatus {
    OPEN,        // Открыт
    IN_PROGRESS, // В работе
    WAITING,     // Ожидает ответа от пользователя
    CLOSED,      // Закрыт
    RESOLVED     // Решен
}