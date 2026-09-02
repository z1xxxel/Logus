package com.logus.tms_backend.DTO;

import lombok.Data;

@Data
public class CarrierFilterDTO {
    private Double minRating;          // Минимальный рейтинг
    private Integer minCompletedOrders; // Минимальное количество заказов
    private String city;               // Город базирования (опционально)
    private Boolean available;         // Только доступные (без активных заказов)
}