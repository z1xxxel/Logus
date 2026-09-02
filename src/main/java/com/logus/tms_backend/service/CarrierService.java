package com.logus.tms_backend.service;

import com.logus.tms_backend.DTO.CarrierFilterDTO;
import com.logus.tms_backend.model.User;
import com.logus.tms_backend.model.UserRole;
import com.logus.tms_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarrierService {

    private final UserRepository userRepository;

    /**
     * 🆕 Поиск перевозчиков с фильтрами (для заказчика)
     */
    @Transactional(readOnly = true)
    public List<User> searchCarriersWithFilters(CarrierFilterDTO filter) {
        return userRepository.findCarriersWithFilters(
                filter.getMinRating(),
                filter.getMinCompletedOrders()
        );
    }

    /**
     * Получить всех перевозчиков
     */
    @Transactional(readOnly = true)
    public List<User> getAllCarriers() {
        return userRepository.findByRole(UserRole.Carrier);
    }
}