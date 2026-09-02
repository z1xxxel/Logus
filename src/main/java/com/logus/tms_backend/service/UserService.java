package com.logus.tms_backend.service;

import com.logus.tms_backend.DTO.CarrierFilterDTO;
import com.logus.tms_backend.model.OrderStatus;
import com.logus.tms_backend.model.User;
import com.logus.tms_backend.model.UserRole;
import com.logus.tms_backend.repository.OrderRepository;
import com.logus.tms_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public User createUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Пользователь с таким email уже существует");
        }
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }

    @Transactional(readOnly = true)
    public List<User> findCarriersWithFilters(CarrierFilterDTO filter) {
        return userRepository.findCarriersWithFilters(
                filter.getMinRating(),
                filter.getMinCompletedOrders()
        );
    }

    @Transactional(readOnly = true)
    public List<User> getAllCarriers() {
        return userRepository.findByRole(UserRole.Carrier);
    }

    /**
     * 🛠️ Обновить данные пользователя (только Admin)
     */
    @Transactional
    public User updateUser(Long userId, String name, String email, UserRole role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (!user.getEmail().equals(email)) {
            if (userRepository.findByEmail(email).isPresent()) {
                throw new RuntimeException("Email уже используется");
            }
        }

        user.setName(name);
        user.setEmail(email);
        user.setRole(role);

        return userRepository.save(user);
    }

    /**
     * 🛠️ Удалить пользователя (только Admin)
     */
    @Transactional
    public void deleteUser(Long userId, Long adminId) {
        if (userId.equals(adminId)) {
            throw new RuntimeException("Нельзя удалить свою собственную учетную запись");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // Проверка на активные заказы как для заказчика, так и для перевозчика
        long activeAsCustomer = orderRepository.findByCustomerWithDetails(user).stream()
                .filter(o -> o.getStatus() == OrderStatus.NEW || o.getStatus() == OrderStatus.IN_PROGRESS)
                .count();

        long activeAsCarrier = orderRepository.findByCarrierWithDetails(user).stream()
                .filter(o -> o.getStatus() == OrderStatus.IN_PROGRESS)
                .count();

        if (activeAsCustomer > 0 || activeAsCarrier > 0) {
            throw new RuntimeException("Нельзя удалить пользователя с активными заказами");
        }

        userRepository.delete(user);
    }

    /**
     * 🛠️ Получить общую статистику системы (только Admin)
     */
    @Transactional(readOnly = true)
    public Map<String, Long> getAdminStats() {
        Map<String, Long> stats = new HashMap<>();

        stats.put("totalUsers", userRepository.count());
        stats.put("totalCustomers", (long) userRepository.findByRole(UserRole.Customer).size());
        stats.put("totalCarriers", (long) userRepository.findByRole(UserRole.Carrier).size());
        stats.put("totalOrders", orderRepository.count());

        long activeOrders = orderRepository.findByStatusWithDetails(OrderStatus.NEW).size() +
                orderRepository.findByStatusWithDetails(OrderStatus.IN_PROGRESS).size();
        stats.put("activeOrders", activeOrders);

        stats.put("completedOrders", (long) orderRepository.findByStatusWithDetails(OrderStatus.COMPLETED).size());

        return stats;
    }
}