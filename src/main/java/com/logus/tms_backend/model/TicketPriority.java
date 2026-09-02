package com.logus.tms_backend.model;

public enum TicketPriority {
    LOW,      // Низкий (вопрос по функционалу)
    MEDIUM,   // Средний (проблема с заказом)
    HIGH,     // Высокий (не работает критичный функционал)
    CRITICAL  // Критический (система не работает)
}