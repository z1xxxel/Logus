package com.logus.tms_backend.controller;

import com.logus.tms_backend.DTO.CarrierFilterDTO;
import com.logus.tms_backend.model.User;
import com.logus.tms_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carriers")
@RequiredArgsConstructor
public class CarrierController {

    private final UserService userService;

    // 🆕 Поиск перевозчиков с фильтрами
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchCarriers(
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Integer minCompletedOrders) {

        CarrierFilterDTO filter = new CarrierFilterDTO();
        filter.setMinRating(minRating);
        filter.setMinCompletedOrders(minCompletedOrders);

        return ResponseEntity.ok(userService.findCarriersWithFilters(filter));
    }

    // Получить всех перевозчиков
    @GetMapping
    public ResponseEntity<List<User>> getAllCarriers() {
        return ResponseEntity.ok(userService.getAllCarriers());
    }
}