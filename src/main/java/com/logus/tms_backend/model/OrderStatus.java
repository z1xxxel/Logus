package com.logus.tms_backend.model;

public enum OrderStatus {
    NEW,
    ASSIGNED,       // Перевозчик назначен, но еще не подтвердил
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}