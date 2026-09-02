package com.logus.tms_backend.DTO;

import java.math.BigDecimal;

public record OrderStatsDTO(
        Long totalOrders,           // Всего заказов
        Long completedOrders,       // Завершенных заказов
        Long activeOrders,          // Активных заказов
        Long cancelledOrders,       // Отмененных заказов
        BigDecimal totalSpent,      // Общая сумма (для заказчика) или заработано (для перевозчика)
        BigDecimal averageOrderValue // Средний чек
) {}